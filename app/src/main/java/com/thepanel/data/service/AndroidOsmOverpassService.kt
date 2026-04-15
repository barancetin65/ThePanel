package com.thepanel.data.service

import com.thepanel.data.model.BoondockingSpot
import com.thepanel.data.model.Campground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class AndroidOsmOverpassService(
    private val client: OkHttpClient
) : OsmOverpassService {
    override suspend fun findCampgrounds(latitude: Double, longitude: Double): Result<List<Campground>> = withContext(Dispatchers.IO) {
        runCatching {
            val query = """
                [out:json];
                (
                  node["tourism"="camp_site"](around:50000,$latitude,$longitude);
                  way["tourism"="camp_site"](around:50000,$latitude,$longitude);
                  node["tourism"="caravan_site"](around:50000,$latitude,$longitude);
                  way["tourism"="caravan_site"](around:50000,$latitude,$longitude);
                );
                out center;
            """.trimIndent()
            fetchOsmData(query) { obj ->
                Campground(
                    name = obj.optString("name", "Unnamed Campground"),
                    latitude = obj.optDouble("lat", obj.optJSONObject("center")?.optDouble("lat") ?: 0.0),
                    longitude = obj.optDouble("lon", obj.optJSONObject("center")?.optDouble("lon") ?: 0.0),
                    description = obj.optJSONObject("tags")?.optString("description", "") ?: ""
                )
            }
        }
    }

    override suspend fun findBoondockingSpots(latitude: Double, longitude: Double): Result<List<BoondockingSpot>> = withContext(Dispatchers.IO) {
        runCatching {
            val query = """
                [out:json];
                (
                  node["amenity"="parking"]["motorhome"="yes"]["fee"="no"](around:50000,$latitude,$longitude);
                  way["amenity"="parking"]["motorhome"="yes"]["fee"="no"](around:50000,$latitude,$longitude);
                );
                out center;
            """.trimIndent()
            fetchOsmData(query) { obj ->
                BoondockingSpot(
                    name = obj.optString("name", "Free Parking Spot"),
                    latitude = obj.optDouble("lat", obj.optJSONObject("center")?.optDouble("lat") ?: 0.0),
                    longitude = obj.optDouble("lon", obj.optJSONObject("center")?.optDouble("lon") ?: 0.0),
                    description = obj.optJSONObject("tags")?.optString("description", "") ?: ""
                )
            }
        }
    }

    private fun <T> fetchOsmData(query: String, mapper: (JSONObject) -> T): List<T> {
        val url = "https://overpass-api.de/api/interpreter"
        val request = Request.Builder()
            .url(url)
            .post(query.toRequestBody())
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return emptyList()
            val body = response.body?.string() ?: return emptyList()
            val json = JSONObject(body)
            val elements = json.getJSONArray("elements")
            return List(elements.length()) { i ->
                val obj = elements.getJSONObject(i)
                val tags = obj.optJSONObject("tags")
                // If name is in tags, use it
                val name = tags?.optString("name", "") ?: ""
                val mergedObj = JSONObject(obj.toString())
                if (name.isNotBlank()) {
                    mergedObj.put("name", name)
                }
                mapper(mergedObj)
            }
        }
    }
}
