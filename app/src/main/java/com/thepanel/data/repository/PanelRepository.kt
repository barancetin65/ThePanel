package com.thepanel.data.repository

import androidx.compose.ui.graphics.Color
import com.thepanel.data.local.AlarmEntity
import com.thepanel.data.local.PanelDao
import com.thepanel.data.model.AdminSettings
import com.thepanel.data.model.AlarmItem
import com.thepanel.data.model.ClockState
import com.thepanel.data.model.LocationState
import com.thepanel.data.model.PanelState
import com.thepanel.data.model.QuickLaunchConfig
import com.thepanel.data.model.QuickLaunchItem
import com.thepanel.data.model.SystemState
import com.thepanel.data.service.AlarmManagerService
import com.thepanel.data.service.AppLauncherService
import com.thepanel.data.service.BatteryService
import com.thepanel.data.service.ConnectivityService
import com.thepanel.data.service.KioskService
import com.thepanel.data.service.LocationService
import com.thepanel.data.service.MediaControlService
import com.thepanel.data.service.SystemInfoService
import com.thepanel.data.service.WeatherService
import com.thepanel.data.settings.AdminSettingsStore
import com.thepanel.data.util.formatAlarmTime
import com.thepanel.data.util.formatClockZone
import com.thepanel.data.util.nowDateLabel
import com.thepanel.data.util.nowTimeLabel
import com.thepanel.ui.theme.AccentDanger
import com.thepanel.ui.theme.AccentMint
import com.thepanel.ui.theme.AccentSky
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.ZoneId

class PanelRepository(
    private val dao: PanelDao,
    private val settingsStore: AdminSettingsStore,
    private val weatherService: WeatherService,
    private val locationService: LocationService,
    private val batteryService: BatteryService,
    private val connectivityService: ConnectivityService,
    private val mediaControlService: MediaControlService,
    private val appLauncherService: AppLauncherService,
    private val kioskService: KioskService,
    private val alarmScheduler: AlarmManagerService,
    private val systemInfoService: SystemInfoService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val zoneId = ZoneId.systemDefault()
    private val latestLocation = MutableStateFlow<LocationState?>(null)
    private val latestSettings = MutableStateFlow(AdminSettings())
    private var lastWeatherRefreshAt: Long = 0L

    init {
        scope.launch {
            settingsStore.settings().collect { latestSettings.value = it }
        }
        scope.launch {
            locationService.liveLocation().collect { location ->
                latestLocation.value = location
                val lat = location.latitude.toDoubleOrNull()
                val lon = location.longitude.toDoubleOrNull()
                val refreshIntervalMs = latestSettings.value.weatherRefreshMinutes.coerceAtLeast(5) * 60_000L
                val shouldRefresh = System.currentTimeMillis() - lastWeatherRefreshAt >= refreshIntervalMs
                if (lat != null && lon != null && shouldRefresh) {
                    weatherService.refreshForecast(lat, lon)
                    lastWeatherRefreshAt = System.currentTimeMillis()
                }
            }
        }
    }

    fun settings(): Flow<AdminSettings> = settingsStore.settings()

    fun panelState(): Flow<PanelState> {
        val clockAndWeather = combine(tickerClock(), weatherService.cachedForecast()) { clock, weather ->
            clock to weather
        }
        val withLocation = combine(clockAndWeather, locationService.liveLocation()) { pair, location ->
            Triple(pair.first, pair.second, location)
        }
        val withBattery = combine(withLocation, batteryService.batteryState()) { triple, battery ->
            ClockWeatherLocationBattery(
                clock = triple.first,
                weather = triple.second,
                location = triple.third,
                battery = battery
            )
        }
        val withConnectivity = combine(withBattery, connectivityService.connectivityState()) { state, connectivity ->
            ClockWeatherLocationBatteryConnectivity(
                clock = state.clock,
                weather = state.weather,
                location = state.location,
                battery = state.battery,
                connectivity = connectivity
            )
        }
        val liveState = combine(withConnectivity, mediaControlService.activeMedia()) { state, media ->
            LivePanelState(
                clock = state.clock,
                weather = state.weather,
                location = state.location,
                battery = state.battery,
                connectivity = state.connectivity,
                media = media
            )
        }

        return combine(liveState, dao.observeAlarms(), settingsStore.settings()) { live, alarms, settings ->
            PanelState(
                clock = live.clock,
                weather = live.weather,
                location = live.location,
                battery = live.battery,
                connectivity = live.connectivity,
                media = live.media,
                alarms = alarms.map { it.toModel() },
                quickLaunchItems = settings.quickLaunchSlots.toQuickLaunchItems(),
                system = SystemState(
                    assistantAvailable = systemInfoService.assistantAvailable(),
                    kioskEnabled = kioskService.isEnabled(),
                    bluetoothEnabled = systemInfoService.bluetoothEnabled(),
                    storageLabel = systemInfoService.storageLabel(),
                    notes = ""
                ),
                permissions = systemInfoService.permissionState()
            )
        }
    }

    suspend fun updateSettings(transform: (AdminSettings) -> AdminSettings) {
        settingsStore.update(transform)
    }

    suspend fun setKioskMode(enabled: Boolean) {
        kioskService.setEnabled(enabled)
        settingsStore.update { it.copy(kioskMode = enabled) }
    }

    fun verifyPin(pin: String, current: AdminSettings): Boolean = pin == current.adminPin

    fun launchApp(packageName: String): Boolean = appLauncherService.launch(packageName)

    suspend fun playPauseMedia() = mediaControlService.playPause()

    suspend fun mediaNext() = mediaControlService.skipNext()

    suspend fun mediaPrevious() = mediaControlService.skipPrevious()

    suspend fun launchAssistant() = mediaControlService.launchAssistant()

    suspend fun refreshWeatherNow() {
        val location = latestLocation.value ?: return
        val lat = location.latitude.toDoubleOrNull() ?: return
        val lon = location.longitude.toDoubleOrNull() ?: return
        weatherService.refreshForecast(lat, lon)
        lastWeatherRefreshAt = System.currentTimeMillis()
    }

    suspend fun addAlarm(title: String, hour: Int, minute: Int, repeatDaily: Boolean) {
        val id = dao.upsertAlarm(
            AlarmEntity(
                title = title,
                hour = hour,
                minute = minute,
                enabled = true,
                repeatDaily = repeatDaily
            )
        )
        alarmScheduler.upsertAlarm(
            AlarmEntity(
                id = id,
                title = title,
                hour = hour,
                minute = minute,
                enabled = true,
                repeatDaily = repeatDaily
            )
        )
    }

    suspend fun toggleAlarm(id: Long, enabled: Boolean) {
        dao.setAlarmEnabled(id, enabled)
        if (enabled) {
            dao.getAlarmById(id)?.let { alarmScheduler.upsertAlarm(it.copy(enabled = true)) }
        } else {
            alarmScheduler.setAlarmEnabled(id, false)
        }
    }

    private fun tickerClock(): Flow<ClockState> = flow {
        while (true) {
            emit(
                ClockState(
                    time = nowTimeLabel(zoneId),
                    dateLabel = nowDateLabel(zoneId),
                    timezoneLabel = formatClockZone(zoneId)
                )
            )
            delay(1_000L)
        }
    }

    private fun AlarmEntity.toModel(): AlarmItem {
        return AlarmItem(
            id = id,
            title = title,
            time = formatAlarmTime(hour, minute),
            enabled = enabled,
            repeatLabel = if (repeatDaily) "Her gun" else "Tek sefer"
        )
    }

    private fun List<QuickLaunchConfig>.toQuickLaunchItems(): List<QuickLaunchItem> {
        val palette = listOf(AccentSky, AccentMint, Color(0xFFFFC145), AccentDanger)
        return map { config ->
            val packageName = config.packageName.trim()
            val resolvedLabel = config.label.ifBlank {
                if (packageName.isNotBlank()) appLauncherService.appLabel(packageName).orEmpty() else ""
            }
            QuickLaunchItem(
                slot = config.slot,
                label = resolvedLabel.ifBlank { "Bos slot" },
                packageName = packageName,
                installed = packageName.isNotBlank() && appLauncherService.isInstalled(packageName),
                color = palette[config.slot % palette.size]
            )
        }
    }
}

private data class LivePanelState(
    val clock: ClockState,
    val weather: com.thepanel.data.model.WeatherState,
    val location: LocationState,
    val battery: com.thepanel.data.model.BatteryState,
    val connectivity: com.thepanel.data.model.ConnectivityState,
    val media: com.thepanel.data.model.MediaState
)

private data class ClockWeatherLocationBattery(
    val clock: ClockState,
    val weather: com.thepanel.data.model.WeatherState,
    val location: LocationState,
    val battery: com.thepanel.data.model.BatteryState
)

private data class ClockWeatherLocationBatteryConnectivity(
    val clock: ClockState,
    val weather: com.thepanel.data.model.WeatherState,
    val location: LocationState,
    val battery: com.thepanel.data.model.BatteryState,
    val connectivity: com.thepanel.data.model.ConnectivityState
)
