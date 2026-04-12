package com.thepanel.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ThePanelApp(
    viewModel: PanelViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val panelState by viewModel.panelState.collectAsStateWithLifecycle()
    val settings by viewModel.settingsState.collectAsStateWithLifecycle()
    val nextAlarm by viewModel.nextAlarmLabel.collectAsStateWithLifecycle()

    DashboardRoute(
        panelState = panelState,
        settings = settings,
        nextAlarm = nextAlarm,
        onVerifyPin = viewModel::verifyPin,
        onToggleKiosk = viewModel::toggleKioskMode,
        onUpdateWeatherRefresh = viewModel::updateWeatherRefresh,
        onUpdateLocationRefresh = viewModel::updateLocationRefresh,
        onToggleOfflineWeather = viewModel::toggleOfflineWeather,
        onUpdatePin = viewModel::updatePin,
        onUpdateQuickLaunch = viewModel::updateQuickLaunch,
        onLaunchApp = viewModel::launchApp,
        onPlayPauseMedia = viewModel::playPauseMedia,
        onMediaNext = viewModel::mediaNext,
        onMediaPrevious = viewModel::mediaPrevious,
        onLaunchAssistant = viewModel::launchAssistant,
        onRefreshWeather = viewModel::refreshWeatherNow,
        onAddAlarm = viewModel::addAlarm,
        onToggleAlarm = viewModel::toggleAlarm
    )
}
