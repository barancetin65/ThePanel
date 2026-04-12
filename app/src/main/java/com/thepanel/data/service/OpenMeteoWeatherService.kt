package com.thepanel.data.service

import com.thepanel.data.local.PanelDao
import com.thepanel.data.local.WeatherCacheEntity
import com.thepanel.data.model.WeatherState
import com.thepanel.data.util.formatFeelsLike
import com.thepanel.data.util.formatHumidity
import com.thepanel.data.util.formatIsoTime
import com.thepanel.data.util.formatTemperature
import com.thepanel.data.util.formatUpdatedAt
import com.thepanel.data.util.formatWind
import com.thepanel.data.util.weatherSummaryFromCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class OpenMeteoWeatherService(
    private val client: OkHttpClient,
    private val dao: PanelDao
) : WeatherService {
    override fun cachedForecast(): Flow<WeatherState> {
        return dao.observeWeatherCache().map { cache ->
            if (cache == null) {
                WeatherState()
            } else {
                WeatherState(
                    available = true,
                    summary = weatherSummaryFromCode(cache.weatherCode),
                    temperature = formatTemperature(cache.temperatureC),
                    feelsLike = formatFeelsLike(cache.feelsLikeC),
                    wind = formatWind(cache.windSpeedKmh),
                    humidity = formatHumidity(cache.humidityPercent),
                    sunrise = formatIsoTime(cache.sunriseIso),
                    sunset = formatIsoTime(cache.sunsetIso),
                    updatedAt = formatUpdatedAt(cache.fetchedAtEpochMs),
                    offlineCached = true
                )
            }
        }
    }

    override suspend fun refreshForecast(latitude: Double, longitude: Double): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,apparent_temperature,weather_code,wind_speed_10m,relative_humidity_2m&daily=sunrise,sunset&timezone=auto&forecast_days=1"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                check(response.isSuccessful) { "Hava verisi alÄ±namadÄ±: ${response.code}" }
                val body = response.body?.string().orEmpty()
                val json = JSONObject(body)
                val current = json.getJSONObject("current")
                val daily = json.getJSONObject("daily")
                val entity = WeatherCacheEntity(
                    latitude = latitude,
                    longitude = longitude,
                    summary = weatherSummaryFromCode(current.getInt("weather_code")),
                    temperatureC = current.getDouble("temperature_2m"),
                    feelsLikeC = current.getDouble("apparent_temperature"),
                    windSpeedKmh = current.getDouble("wind_speed_10m"),
                    humidityPercent = current.getInt("relative_humidity_2m"),
                    sunriseIso = daily.getJSONArray("sunrise").optString(0),
                    sunsetIso = daily.getJSONArray("sunset").optString(0),
                    fetchedAtEpochMs = System.currentTimeMillis(),
                    weatherCode = current.getInt("weather_code")
                )
                dao.upsertWeatherCache(entity)
            }
        }
    }
}
