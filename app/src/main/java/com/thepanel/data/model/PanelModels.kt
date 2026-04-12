package com.thepanel.data.model

import androidx.compose.ui.graphics.Color
import com.thepanel.ui.theme.AccentDanger
import com.thepanel.ui.theme.AccentMint
import com.thepanel.ui.theme.AccentSky
import kotlinx.serialization.Serializable

data class PanelState(
    val clock: ClockState = ClockState(),
    val weather: WeatherState = WeatherState(),
    val location: LocationState = LocationState(),
    val battery: BatteryState = BatteryState(),
    val connectivity: ConnectivityState = ConnectivityState(),
    val media: MediaState = MediaState(),
    val alarms: List<AlarmItem> = emptyList(),
    val quickLaunchItems: List<QuickLaunchItem> = emptyList(),
    val system: SystemState = SystemState(),
    val permissions: PermissionState = PermissionState()
)

data class ClockState(
    val time: String = "--:--",
    val dateLabel: String = "",
    val timezoneLabel: String = ""
)

data class WeatherState(
    val available: Boolean = false,
    val summary: String = "Veri bekleniyor",
    val temperature: String = "--",
    val feelsLike: String = "",
    val wind: String = "",
    val humidity: String = "",
    val sunrise: String = "",
    val sunset: String = "",
    val updatedAt: String = "HenÃ¼z senkron yok",
    val offlineCached: Boolean = false,
    val error: String? = null,
    val accent: Color = AccentSky
)

data class LocationState(
    val available: Boolean = false,
    val country: String = "",
    val province: String = "",
    val district: String = "",
    val latitude: String = "--",
    val longitude: String = "--",
    val speed: String = "-- km/s",
    val heading: String = "",
    val accuracy: String = "",
    val altitude: String = "",
    val error: String? = null
)

data class BatteryState(
    val level: Int = 0,
    val charging: Boolean = false,
    val healthLabel: String = "Durum bekleniyor",
    val estimatedTime: String = ""
)

data class ConnectivityState(
    val online: Boolean = false,
    val transport: String = "Ã‡evrimdÄ±ÅŸÄ±",
    val signalLabel: String = "",
    val lastSeenOnline: String = ""
)

data class MediaState(
    val available: Boolean = false,
    val title: String = "Etkin medya yok",
    val subtitle: String = "",
    val source: String = "",
    val playing: Boolean = false,
    val progressLabel: String = "",
    val artworkUrl: String? = null,
    val canSkip: Boolean = false,
    val permissionRequired: Boolean = false
)

data class AlarmItem(
    val id: Long,
    val title: String,
    val time: String,
    val enabled: Boolean,
    val repeatLabel: String
)

data class QuickLaunchItem(
    val slot: Int,
    val label: String,
    val packageName: String,
    val installed: Boolean,
    val color: Color
)

data class SystemState(
    val assistantAvailable: Boolean = false,
    val kioskEnabled: Boolean = false,
    val bluetoothEnabled: Boolean = false,
    val storageLabel: String = "",
    val notes: String = ""
)

data class AdminSettings(
    val adminPin: String = "2468",
    val kioskMode: Boolean = false,
    val weatherRefreshMinutes: Int = 30,
    val locationRefreshSeconds: Int = 5,
    val useOfflineWeatherCache: Boolean = true,
    val quickLaunchSlots: List<QuickLaunchConfig> = List(4) { index ->
        QuickLaunchConfig(slot = index, label = "", packageName = "")
    }
)

@Serializable
data class QuickLaunchConfig(
    val slot: Int,
    val label: String,
    val packageName: String
)

data class PermissionState(
    val hasLocationPermission: Boolean = false,
    val notificationListenerEnabled: Boolean = false,
    val exactAlarmReady: Boolean = false
)

enum class PanelStatusColor(val color: Color) {
    Good(AccentMint),
    Info(AccentSky),
    Warn(Color(0xFFFFC145)),
    Bad(AccentDanger)
}
