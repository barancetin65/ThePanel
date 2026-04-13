package com.thepanel.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.thepanel.ThePanelApplication
import com.thepanel.data.model.AdminSettings
import com.thepanel.data.model.PanelState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PanelViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as ThePanelApplication).container.repository

    val panelState: StateFlow<PanelState> = repository.panelState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PanelState())

    val settingsState: StateFlow<AdminSettings> = repository.settings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AdminSettings())

    val nextAlarmLabel: StateFlow<String> = panelState
        .map { state -> state.alarms.firstOrNull { it.enabled }?.let { "${it.title} Â· ${it.time}" } ?: "Alarm yok" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Alarm yok")

    fun verifyPin(pin: String): Boolean = repository.verifyPin(pin, settingsState.value)

    fun updatePin(newPin: String) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(adminPin = newPin) }
        }
    }

    fun toggleKioskMode() {
        viewModelScope.launch {
            repository.setKioskMode(!settingsState.value.kioskMode)
        }
    }

    fun updateWeatherRefresh(minutes: Int) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(weatherRefreshMinutes = minutes) }
        }
    }

    fun updateLocationRefresh(seconds: Int) {
        viewModelScope.launch {
            repository.updateSettings { it.copy(locationRefreshSeconds = seconds) }
        }
    }

    fun toggleOfflineWeather() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(useOfflineWeatherCache = !it.useOfflineWeatherCache) }
        }
    }

    fun toggleLightTheme() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(isLightTheme = !it.isLightTheme) }
        }
    }

    fun toggleBiometric() {
        viewModelScope.launch {
            repository.updateSettings { it.copy(biometricEnabled = !it.biometricEnabled) }
        }
    }

    fun updateQuickLaunch(slot: Int, label: String, packageName: String) {
        viewModelScope.launch {
            repository.updateSettings { settings ->
                settings.copy(
                    quickLaunchSlots = settings.quickLaunchSlots.map {
                        if (it.slot == slot) it.copy(label = label, packageName = packageName) else it
                    }
                )
            }
        }
    }

    fun launchApp(packageName: String) {
        if (packageName.isBlank()) return
        repository.launchApp(packageName)
    }

    fun playPauseMedia() {
        viewModelScope.launch { repository.playPauseMedia() }
    }

    fun mediaNext() {
        viewModelScope.launch { repository.mediaNext() }
    }

    fun mediaPrevious() {
        viewModelScope.launch { repository.mediaPrevious() }
    }

    fun launchAssistant() {
        viewModelScope.launch { repository.launchAssistant() }
    }

    fun refreshWeatherNow() {
        viewModelScope.launch { repository.refreshWeatherNow() }
    }

    fun addAlarm(title: String, hour: Int, minute: Int, repeatDaily: Boolean) {
        viewModelScope.launch { repository.addAlarm(title, hour, minute, repeatDaily) }
    }

    fun toggleAlarm(id: Long, enabled: Boolean) {
        viewModelScope.launch { repository.toggleAlarm(id, enabled) }
    }
}
