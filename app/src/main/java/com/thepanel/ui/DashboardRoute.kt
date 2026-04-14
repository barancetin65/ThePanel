package com.thepanel.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Assistant
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.NetworkWifi
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.thepanel.data.model.AdminSettings
import com.thepanel.data.model.AppInfo
import com.thepanel.data.model.PanelState
import com.thepanel.data.model.QuickLaunchConfig
import com.thepanel.data.model.QuickLaunchItem
import com.thepanel.ui.theme.AccentDanger
import com.thepanel.ui.theme.AccentMint
import com.thepanel.ui.theme.AccentSky
import com.thepanel.ui.theme.BackgroundEnd
import com.thepanel.ui.theme.BackgroundStart
import com.thepanel.ui.theme.SurfacePrimary
import com.thepanel.ui.theme.SurfaceSecondary
import com.thepanel.ui.theme.TextMuted

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardRoute(
    panelState: PanelState,
    settings: AdminSettings,
    nextAlarm: String,
    onVerifyPin: (String) -> Boolean,
    onToggleKiosk: () -> Unit,
    onUpdateWeatherRefresh: (Int) -> Unit,
    onUpdateLocationRefresh: (Int) -> Unit,
    onToggleOfflineWeather: () -> Unit,
    onUpdatePin: (String) -> Unit,
    onUpdateQuickLaunch: (Int, String, String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onPlayPauseMedia: () -> Unit,
    onMediaNext: () -> Unit,
    onMediaPrevious: () -> Unit,
    onLaunchAssistant: () -> Unit,
    onRefreshWeather: () -> Unit,
    onAddAlarm: (String, Int, Int, Boolean) -> Unit,
    onToggleAlarm: (Long, Boolean) -> Unit,
    onToggleLightTheme: () -> Unit,
    getInstalledApps: () -> List<AppInfo>
) {
    var adminSheetVisible by remember { mutableStateOf(false) }
    var adminUnlocked by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var allAppsVisible by remember { mutableStateOf(false) }

    val bgStart = if (settings.useLightTheme) Color(0xFFE3F2FD) else BackgroundStart
    val bgEnd = if (settings.useLightTheme) Color(0xFFBBDEFB) else BackgroundEnd

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(bgStart, bgEnd)))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1.25f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HeroClockCard(panelState, nextAlarm)
                WeatherCard(panelState, onRefreshWeather)
                LocationCard(panelState)
                PermissionsCard(panelState)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusStrip(panelState)
                MediaCard(panelState, onPlayPauseMedia, onMediaPrevious, onMediaNext, onLaunchAssistant)
                QuickLaunchCard(panelState.quickLaunchItems, onLaunchApp, onOpenAllApps = { allAppsVisible = true })
                AlarmCard(panelState, onToggleAlarm)
                SystemCard(panelState)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        FloatingSettingsButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { adminSheetVisible = true }
        )

        AnimatedVisibility(
            visible = adminSheetVisible,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            AdminPanel(
                settings = settings,
                unlocked = adminUnlocked,
                pinInput = pinInput,
                pinError = pinError,
                onPinChange = {
                    pinInput = it
                    pinError = null
                },
                onUnlock = {
                    adminUnlocked = onVerifyPin(pinInput)
                    if (!adminUnlocked) pinError = "Incorrect PIN"
                },
                onClose = {
                    adminSheetVisible = false
                    adminUnlocked = false
                    pinInput = ""
                    pinError = null
                },
                onToggleKiosk = onToggleKiosk,
                onUpdateWeatherRefresh = onUpdateWeatherRefresh,
                onUpdateLocationRefresh = onUpdateLocationRefresh,
                onToggleOfflineWeather = onToggleOfflineWeather,
                onUpdatePin = onUpdatePin,
                onUpdateQuickLaunch = onUpdateQuickLaunch,
                onAddAlarm = onAddAlarm,
                onToggleLightTheme = onToggleLightTheme,
                getInstalledApps = getInstalledApps
            )
        }

        if (allAppsVisible) {
            AllAppsDialog(
                apps = getInstalledApps(),
                onLaunchApp = {
                    onLaunchApp(it)
                    allAppsVisible = false
                },
                onClose = { allAppsVisible = false }
            )
        }
    }
}

@Composable
private fun HeroClockCard(panelState: PanelState, nextAlarm: String) {
    PanelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(panelState.clock.dateLabel, color = TextMuted, fontSize = 18.sp)
                Text(
                    panelState.clock.time,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { contentDescription = "Current time ${panelState.clock.time}" }
                )
                Text(panelState.clock.timezoneLabel, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Pill("Next alarm", AccentSky)
                Text(nextAlarm, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
                Text(
                    if (panelState.system.kioskEnabled) "Kiosk enabled" else "Kiosk disabled",
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun WeatherCard(panelState: PanelState, onRefreshWeather: () -> Unit) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CardTitle(Icons.Rounded.Cloud, "Weather")
                    Text(panelState.weather.temperature, fontSize = 54.sp, fontWeight = FontWeight.Bold)
                    Text(panelState.weather.summary, fontSize = 22.sp)
                    if (panelState.weather.feelsLike.isNotBlank()) Text(panelState.weather.feelsLike, color = TextMuted)
                    Text(panelState.weather.updatedAt, color = TextMuted)
                    panelState.weather.error?.let { Text(it, color = AccentDanger) }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatLine("Wind", panelState.weather.wind.ifBlank { "-" })
                    StatLine("Humidity", panelState.weather.humidity.ifBlank { "-" })
                    StatLine("Sunrise", panelState.weather.sunrise.ifBlank { "-" })
                    StatLine("Sunset", panelState.weather.sunset.ifBlank { "-" })
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Pill(if (panelState.weather.offlineCached) "Offline cache" else "Live", panelState.weather.accent)
                        IconButton(onClick = onRefreshWeather) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh weather", tint = AccentSky)
                        }
                    }
                }
            }
            
            if (panelState.weather.forecasts.isNotEmpty()) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    panelState.weather.forecasts.take(5).forEach { forecast ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(forecast.day.take(3), fontSize = 14.sp, color = TextMuted)
                            Text(forecast.tempMax, fontWeight = FontWeight.Bold)
                            Text(forecast.tempMin, fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationCard(panelState: PanelState) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle(Icons.Rounded.MyLocation, "Location & Driving")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        listOf(panelState.location.district, panelState.location.province)
                            .filter { it.isNotBlank() }
                            .joinToString(", ")
                            .ifBlank { "Waiting for location" },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(panelState.location.country.ifBlank { "No country info" }, color = TextMuted)
                    Text("Lat ${panelState.location.latitude} · Lon ${panelState.location.longitude}", color = TextMuted, fontSize = 14.sp)
                    panelState.location.error?.let { Text(it, color = AccentDanger) }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(panelState.location.speed, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = AccentMint)
                    Text(panelState.location.heading.ifBlank { "-" }, color = TextMuted)
                    Text(panelState.location.accuracy.ifBlank { "-" }, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun PermissionsCard(panelState: PanelState) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CardTitle(Icons.Rounded.Assistant, "Preparation")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusIndicator(panelState.permissions.hasLocationPermission)
                Text("Location permission")
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusIndicator(panelState.permissions.notificationListenerEnabled)
                Text("Media access")
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatusIndicator(panelState.permissions.exactAlarmReady)
                Text("Alarm permission")
            }
        }
    }
}

@Composable
private fun StatusIndicator(active: Boolean) {
    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if (active) AccentMint else AccentDanger))
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun StatusStrip(panelState: PanelState) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CompactStatusCard(Icons.Rounded.BatteryChargingFull, "Battery", "${panelState.battery.level}%", panelState.battery.healthLabel)
        CompactStatusCard(Icons.Rounded.NetworkWifi, "Internet", panelState.connectivity.transport, panelState.connectivity.signalLabel)
        CompactStatusCard(Icons.Rounded.Speed, "Speed", panelState.location.speed, panelState.location.heading.ifBlank { "-" })
        CompactStatusCard(Icons.Rounded.Storage, "Storage", panelState.system.storageLabel, if (panelState.system.bluetoothEnabled) "Bluetooth on" else "Bluetooth off")
    }
}

@Composable
private fun MediaCard(
    panelState: PanelState,
    onPlayPauseMedia: () -> Unit,
    onMediaPrevious: () -> Unit,
    onMediaNext: () -> Unit,
    onLaunchAssistant: () -> Unit
) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle(Icons.Rounded.PlayCircle, "Media Center")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceSecondary)) {
                    Icon(Icons.Rounded.PlayCircle, contentDescription = null, modifier = Modifier.align(Alignment.Center).size(32.dp), tint = TextMuted)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(panelState.media.title, color = if (panelState.media.permissionRequired) AccentDanger else MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (panelState.media.subtitle.isNotBlank()) Text(panelState.media.subtitle, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (panelState.media.source.isNotBlank()) Text(panelState.media.source, color = AccentSky, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MediaAction(Icons.Rounded.SkipPrevious, "Previous", onMediaPrevious)
                    MediaAction(if (panelState.media.playing) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle, if (panelState.media.playing) "Pause" else "Play", onPlayPauseMedia)
                    MediaAction(Icons.Rounded.SkipNext, "Next", onMediaNext)
                }
                ElevatedButton(onClick = onLaunchAssistant) {
                    Icon(Icons.Rounded.Assistant, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Assistant")
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun QuickLaunchCard(items: List<QuickLaunchItem>, onLaunchApp: (String) -> Unit, onOpenAllApps: () -> Unit) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CardTitle(Icons.Rounded.Map, "Quick Launch")
                TextButton(onClick = onOpenAllApps) {
                    Icon(Icons.Rounded.Apps, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("ALL APPS")
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { item ->
                    Surface(
                        color = item.color.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .border(1.dp, item.color.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                            .clickable(enabled = item.installed) { onLaunchApp(item.packageName) }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                            Text(item.label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                            Text(if (item.installed) item.packageName else "No app", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlarmCard(panelState: PanelState, onToggleAlarm: (Long, Boolean) -> Unit) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle(Icons.Rounded.Alarm, "Alarms")
            if (panelState.alarms.isEmpty()) {
                Text("No alarms yet", color = TextMuted)
            }
            panelState.alarms.forEach { alarm ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(alarm.title, fontWeight = FontWeight.SemiBold)
                        Text(alarm.repeatLabel, color = TextMuted)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(alarm.time, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(Modifier.width(12.dp))
                        Switch(checked = alarm.enabled, onCheckedChange = { onToggleAlarm(alarm.id, it) })
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
            }
        }
    }
}

@Composable
private fun SystemCard(panelState: PanelState) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CardTitle(Icons.Rounded.Assistant, "System")
            Text(if (panelState.system.assistantAvailable) "Assistant ready" else "Assistant unavailable", fontWeight = FontWeight.SemiBold)
            Text(panelState.connectivity.lastSeenOnline.ifBlank { "Monitoring connection status" }, color = TextMuted)
        }
    }
}

@Composable
private fun FloatingSettingsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier, color = SurfaceSecondary, shadowElevation = 14.dp, shape = CircleShape) {
        IconButton(onClick = onClick, modifier = Modifier.size(68.dp)) {
            Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = AccentSky)
        }
    }
}

@Composable
private fun AdminPanel(
    settings: AdminSettings,
    unlocked: Boolean,
    pinInput: String,
    pinError: String?,
    onPinChange: (String) -> Unit,
    onUnlock: () -> Unit,
    onClose: () -> Unit,
    onToggleKiosk: () -> Unit,
    onUpdateWeatherRefresh: (Int) -> Unit,
    onUpdateLocationRefresh: (Int) -> Unit,
    onToggleOfflineWeather: () -> Unit,
    onUpdatePin: (String) -> Unit,
    onUpdateQuickLaunch: (Int, String, String) -> Unit,
    onAddAlarm: (String, Int, Int, Boolean) -> Unit,
    onToggleLightTheme: () -> Unit,
    getInstalledApps: () -> List<AppInfo>
) {
    var weatherRefresh by remember(settings.weatherRefreshMinutes) { mutableStateOf(settings.weatherRefreshMinutes.toString()) }
    var locationRefresh by remember(settings.locationRefreshSeconds) { mutableStateOf(settings.locationRefreshSeconds.toString()) }
    var newPin by remember { mutableStateOf("") }
    var alarmTitle by remember { mutableStateOf("") }
    var alarmHour by remember { mutableStateOf("") }
    var alarmMinute by remember { mutableStateOf("") }
    var repeatDaily by remember { mutableStateOf(true) }
    val slotLabels = remember(settings.quickLaunchSlots) { mutableStateListOf(*settings.quickLaunchSlots.toTypedArray()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.98f))
            .clickable(enabled = false) {} // Consume clicks
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Admin Panel", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onClose) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(32.dp))
                }
            }

            if (!unlocked) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("PIN required for critical settings.", color = TextMuted, fontSize = 20.sp)
                    Spacer(Modifier.height(24.dp))
                    
                    Text(pinInput.map { "*" }.joinToString(" "), fontSize = 48.sp, fontWeight = FontWeight.Bold, letterSpacing = 8.sp)
                    if (pinError != null) Text(pinError, color = AccentDanger)
                    
                    Spacer(Modifier.height(32.dp))
                    
                    // Simple PIN Keyboard
                    val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.width(300.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(numbers) { num ->
                            Surface(
                                onClick = {
                                    when (num) {
                                        "C" -> onPinChange("")
                                        "OK" -> onUnlock()
                                        else -> if (pinInput.length < 6) onPinChange(pinInput + num)
                                    }
                                },
                                shape = CircleShape,
                                color = SurfaceSecondary,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(num, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    // Left Column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        AdminSection("General Settings") {
                            AdminToggle("Kiosk Mode", settings.kioskMode) { onToggleKiosk() }
                            AdminToggle("Light Theme", settings.useLightTheme) { onToggleLightTheme() }
                            AdminToggle("Offline Weather", settings.useOfflineWeatherCache) { onToggleOfflineWeather() }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = weatherRefresh,
                                    onValueChange = {
                                        weatherRefresh = it.filter(Char::isDigit)
                                        weatherRefresh.toIntOrNull()?.let(onUpdateWeatherRefresh)
                                    },
                                    label = { Text("Weather Refresh (min)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = locationRefresh,
                                    onValueChange = {
                                        locationRefresh = it.filter(Char::isDigit)
                                        locationRefresh.toIntOrNull()?.let(onUpdateLocationRefresh)
                                    },
                                    label = { Text("Location Refresh (sec)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        AdminSection("Security") {
                            OutlinedTextField(
                                value = newPin,
                                onValueChange = { newPin = it.filter(Char::isDigit).take(6) },
                                label = { Text("New Admin PIN") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(onClick = { if (newPin.length >= 4) { onUpdatePin(newPin); newPin = "" } }, modifier = Modifier.fillMaxWidth()) {
                                Text("Update PIN")
                            }
                        }
                    }

                    // Right Column
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        AdminSection("Quick Launch Slots") {
                            val apps = getInstalledApps()
                            slotLabels.forEachIndexed { index, config ->
                                QuickLaunchEditor(
                                    config = config,
                                    apps = apps,
                                    onChanged = { updated ->
                                        slotLabels[index] = updated
                                        onUpdateQuickLaunch(updated.slot, updated.label, updated.packageName)
                                    }
                                )
                                if (index < slotLabels.size - 1) Spacer(Modifier.height(8.dp))
                            }
                        }

                        AdminSection("New Alarm") {
                            OutlinedTextField(value = alarmTitle, onValueChange = { alarmTitle = it }, label = { Text("Alarm Title") }, modifier = Modifier.fillMaxWidth())
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(value = alarmHour, onValueChange = { alarmHour = it.filter(Char::isDigit).take(2) }, label = { Text("Hour") }, modifier = Modifier.weight(1f))
                                OutlinedTextField(value = alarmMinute, onValueChange = { alarmMinute = it.filter(Char::isDigit).take(2) }, label = { Text("Minute") }, modifier = Modifier.weight(1f))
                            }
                            AdminToggle("Repeat Daily", repeatDaily) { repeatDaily = it }
                            Button(
                                onClick = {
                                    val hour = alarmHour.toIntOrNull()
                                    val minute = alarmMinute.toIntOrNull()
                                    if (alarmTitle.isNotBlank() && hour != null && minute != null && hour in 0..23 && minute in 0..59) {
                                        onAddAlarm(alarmTitle, hour, minute, repeatDaily)
                                        alarmTitle = ""
                                        alarmHour = ""
                                        alarmMinute = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Alarm")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = AccentSky, fontSize = 18.sp)
        Surface(
            color = SurfacePrimary.copy(alpha = 0.4f),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
        }
    }
}

@Composable
private fun AdminToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun QuickLaunchEditor(config: QuickLaunchConfig, apps: List<AppInfo>, onChanged: (QuickLaunchConfig) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = config.label,
                onValueChange = { onChanged(config.copy(label = it)) },
                label = { Text("Slot ${config.slot + 1} Label") },
                modifier = Modifier.weight(1f)
            )
            Box(modifier = Modifier.weight(1.4f)) {
                OutlinedTextField(
                    value = config.packageName,
                    onValueChange = { onChanged(config.copy(packageName = it)) },
                    label = { Text("Package Name") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.AutoMirrored.Rounded.List, contentDescription = "Select App")
                        }
                    }
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    apps.forEach { app ->
                        DropdownMenuItem(
                            text = { 
                                Column {
                                    Text(app.label)
                                    Text(app.packageName, fontSize = 11.sp, color = TextMuted)
                                }
                            },
                            onClick = {
                                onChanged(config.copy(label = app.label, packageName = app.packageName))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllAppsDialog(apps: List<AppInfo>, onLaunchApp: (String) -> Unit, onClose: () -> Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("All Apps", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(32.dp))
                    }
                }
                Spacer(Modifier.height(16.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(160.dp),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(apps) { app ->
                        Surface(
                            onClick = { onLaunchApp(app.packageName) },
                            color = SurfaceSecondary,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(100.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(app.label, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(app.packageName, fontSize = 10.sp, color = TextMuted, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun CardTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = AccentSky)
        Text(title, color = TextMuted, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun CompactStatusCard(icon: ImageVector, title: String, value: String, subtitle: String) {
    Surface(color = SurfaceSecondary.copy(alpha = 0.95f), shape = RoundedCornerShape(22.dp), modifier = Modifier.width(180.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = AccentSky)
            Text(title, color = TextMuted)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(subtitle, color = TextMuted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextMuted)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Pill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(text, color = color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun MediaAction(icon: ImageVector, description: String, onClick: () -> Unit) {
    Surface(color = SurfaceSecondary, shape = CircleShape) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = description)
        }
    }
}
