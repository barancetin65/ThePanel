package com.thepanel.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thepanel.data.model.AdminSettings
import com.thepanel.data.model.QuickLaunchConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "admin_settings")

class AdminSettingsStore(
    private val context: Context,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun settings(): Flow<AdminSettings> {
        return context.dataStore.data.map { prefs ->
            AdminSettings(
                adminPin = prefs[PIN] ?: "2468",
                kioskMode = prefs[KIOSK] ?: false,
                weatherRefreshMinutes = prefs[WEATHER_REFRESH_MINUTES] ?: 30,
                locationRefreshSeconds = prefs[LOCATION_REFRESH_SECONDS] ?: 5,
                useOfflineWeatherCache = prefs[USE_OFFLINE_CACHE] ?: true,
                useLightTheme = prefs[LIGHT_THEME] ?: false,
                quickLaunchSlots = decodeSlots(prefs[QUICK_LAUNCH_SLOTS])
            )
        }
    }

    suspend fun update(transform: (AdminSettings) -> AdminSettings) {
        context.dataStore.edit { prefs ->
            val current = AdminSettings(
                adminPin = prefs[PIN] ?: "2468",
                kioskMode = prefs[KIOSK] ?: false,
                weatherRefreshMinutes = prefs[WEATHER_REFRESH_MINUTES] ?: 30,
                locationRefreshSeconds = prefs[LOCATION_REFRESH_SECONDS] ?: 5,
                useOfflineWeatherCache = prefs[USE_OFFLINE_CACHE] ?: true,
                useLightTheme = prefs[LIGHT_THEME] ?: false,
                quickLaunchSlots = decodeSlots(prefs[QUICK_LAUNCH_SLOTS])
            )
            val updated = transform(current)
            prefs[PIN] = updated.adminPin
            prefs[KIOSK] = updated.kioskMode
            prefs[WEATHER_REFRESH_MINUTES] = updated.weatherRefreshMinutes
            prefs[LOCATION_REFRESH_SECONDS] = updated.locationRefreshSeconds
            prefs[USE_OFFLINE_CACHE] = updated.useOfflineWeatherCache
            prefs[LIGHT_THEME] = updated.useLightTheme
            prefs[QUICK_LAUNCH_SLOTS] = json.encodeToString(updated.quickLaunchSlots)
        }
    }

    private fun decodeSlots(raw: String?): List<QuickLaunchConfig> {
        return runCatching {
            if (raw.isNullOrBlank()) {
                List(4) { index -> QuickLaunchConfig(index, "", "") }
            } else {
                json.decodeFromString<List<QuickLaunchConfig>>(raw)
            }
        }.getOrElse {
            List(4) { index -> QuickLaunchConfig(index, "", "") }
        }
    }

    private companion object {
        val PIN = stringPreferencesKey("pin")
        val KIOSK = booleanPreferencesKey("kiosk")
        val WEATHER_REFRESH_MINUTES = intPreferencesKey("weather_refresh_minutes")
        val LOCATION_REFRESH_SECONDS = intPreferencesKey("location_refresh_seconds")
        val USE_OFFLINE_CACHE = booleanPreferencesKey("use_offline_cache")
        val LIGHT_THEME = booleanPreferencesKey("light_theme")
        val QUICK_LAUNCH_SLOTS = stringPreferencesKey("quick_launch_slots")
    }
}
