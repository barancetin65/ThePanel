package com.thepanel.data.service

import com.thepanel.data.local.AlarmEntity
import com.thepanel.data.model.BatteryState
import com.thepanel.data.model.ConnectivityState
import com.thepanel.data.model.LocationState
import com.thepanel.data.model.MediaState
import com.thepanel.data.model.PermissionState
import com.thepanel.data.model.WeatherState
import kotlinx.coroutines.flow.Flow

interface WeatherService {
    fun cachedForecast(): Flow<WeatherState>
    suspend fun refreshForecast(latitude: Double, longitude: Double): Result<Unit>
}

interface LocationService {
    fun liveLocation(): Flow<LocationState>
}

interface BatteryService {
    fun batteryState(): Flow<BatteryState>
}

interface ConnectivityService {
    fun connectivityState(): Flow<ConnectivityState>
}

interface MediaControlService {
    fun activeMedia(): Flow<MediaState>
    suspend fun playPause()
    suspend fun skipNext()
    suspend fun skipPrevious()
    suspend fun launchAssistant()
}

interface AppLauncherService {
    fun launch(packageName: String): Boolean
    fun isInstalled(packageName: String): Boolean
    fun appLabel(packageName: String): String?
}

interface KioskService {
    fun setEnabled(enabled: Boolean)
    fun isEnabled(): Boolean
}

interface SystemInfoService {
    fun bluetoothEnabled(): Boolean
    fun storageLabel(): String
    fun permissionState(): PermissionState
    fun assistantAvailable(): Boolean
}

interface AlarmManagerService {
    suspend fun upsertAlarm(entity: AlarmEntity): Long
    suspend fun setAlarmEnabled(id: Long, enabled: Boolean)
    suspend fun deleteAlarm(id: Long)
}
