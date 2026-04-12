package com.thepanel

import android.content.Context
import com.thepanel.data.local.PanelDatabase
import com.thepanel.data.repository.PanelRepository
import com.thepanel.data.service.AlarmScheduler
import com.thepanel.data.service.AndroidAppLauncherService
import com.thepanel.data.service.AndroidBatteryService
import com.thepanel.data.service.AndroidConnectivityService
import com.thepanel.data.service.AndroidKioskService
import com.thepanel.data.service.AndroidLocationService
import com.thepanel.data.service.AndroidMediaControlService
import com.thepanel.data.service.AndroidSystemInfoService
import com.thepanel.data.service.OpenMeteoWeatherService
import com.thepanel.data.settings.AdminSettingsStore
import okhttp3.OkHttpClient

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database = PanelDatabase.create(appContext)
    private val dao = database.panelDao()
    private val settingsStore = AdminSettingsStore(appContext)
    private val httpClient = OkHttpClient()
    private val alarmScheduler = AlarmScheduler(appContext)
    private val appLauncherService = AndroidAppLauncherService(appContext)
    private val systemInfoService = AndroidSystemInfoService(appContext)

    val repository = PanelRepository(
        dao = dao,
        settingsStore = settingsStore,
        weatherService = OpenMeteoWeatherService(httpClient, dao),
        locationService = AndroidLocationService(appContext),
        batteryService = AndroidBatteryService(appContext),
        connectivityService = AndroidConnectivityService(appContext),
        mediaControlService = AndroidMediaControlService(appContext),
        appLauncherService = appLauncherService,
        kioskService = AndroidKioskService(appContext),
        alarmScheduler = alarmScheduler,
        systemInfoService = systemInfoService
    )
}
