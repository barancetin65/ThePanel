package com.thepanel.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Assistant
import androidx.compose.material.icons.rounded.BatteryChargingFull
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thepanel.data.model.AdminSettings
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
    onToggleAlarm: (Long, Boolean) -> Unit
) {
    var adminSheetVisible by remember { mutableStateOf(false) }
    var adminUnlocked by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(BackgroundStart, BackgroundEnd)))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(
                modifier = Modifier.weight(1.25f),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                HeroClockCard(panelState, nextAlarm)
                WeatherCard(panelState, onRefreshWeather)
                LocationCard(panelState)
                PermissionsCard(panelState)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                StatusStrip(panelState)
                MediaCard(panelState, onPlayPauseMedia, onMediaPrevious, onMediaNext, onLaunchAssistant)
                QuickLaunchCard(panelState.quickLaunchItems, onLaunchApp)
                AlarmCard(panelState, onToggleAlarm)
                SystemCard(panelState)
            }
        }

        FloatingSettingsButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { adminSheetVisible = true }
        )

        AnimatedVisibility(adminSheetVisible, modifier = Modifier.align(Alignment.CenterEnd)) {
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
                    if (!adminUnlocked) pinError = "PIN hatali"
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
                onAddAlarm = onAddAlarm
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
                Text(panelState.clock.time, fontSize = 72.sp, fontWeight = FontWeight.Bold)
                Text(panelState.clock.timezoneLabel, color = TextMuted)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Pill("Sonraki alarm", AccentSky)
                Text(nextAlarm, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
                Text(
                    if (panelState.system.kioskEnabled) "Kiosk etkin" else "Kiosk pasif",
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun WeatherCard(panelState: PanelState, onRefreshWeather: () -> Unit) {
    PanelCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CardTitle(Icons.Rounded.Cloud, "Hava Durumu")
                Text(panelState.weather.temperature, fontSize = 54.sp, fontWeight = FontWeight.Bold)
                Text(panelState.weather.summary, fontSize = 22.sp)
                if (panelState.weather.feelsLike.isNotBlank()) Text(panelState.weather.feelsLike, color = TextMuted)
                Text(panelState.weather.updatedAt, color = TextMuted)
                panelState.weather.error?.let { Text(it, color = AccentDanger) }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatLine("Ruzgar", panelState.weather.wind.ifBlank { "-" })
                StatLine("Nem", panelState.weather.humidity.ifBlank { "-" })
                StatLine("Gundogumu", panelState.weather.sunrise.ifBlank { "-" })
                StatLine("Gunbatimi", panelState.weather.sunset.ifBlank { "-" })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Pill(if (panelState.weather.offlineCached) "Offline cache" else "Canli", panelState.weather.accent)
                    IconButton(onClick = onRefreshWeather) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Yenile", tint = AccentSky)
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
            CardTitle(Icons.Rounded.MyLocation, "Konum ve Surus")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        listOf(panelState.location.district, panelState.location.province)
                            .filter { it.isNotBlank() }
                            .joinToString(", ")
                            .ifBlank { "Konum bekleniyor" },
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(panelState.location.country.ifBlank { "Ulke bilgisi yok" }, color = TextMuted)
                    Text("Lat ${panelState.location.latitude}", color = TextMuted)
                    Text("Lon ${panelState.location.longitude}", color = TextMuted)
                    panelState.location.error?.let { Text(it, color = AccentDanger) }
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(panelState.location.speed, fontSize = 40.sp, fontWeight = FontWeight.Bold, color = AccentMint)
                    Text(panelState.location.heading.ifBlank { "-" }, color = TextMuted)
                    Text(panelState.location.accuracy.ifBlank { "-" }, color = TextMuted)
                    Text(panelState.location.altitude.ifBlank { "-" }, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun PermissionsCard(panelState: PanelState) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CardTitle(Icons.Rounded.Assistant, "Hazirlik")
            Text(if (panelState.permissions.hasLocationPermission) "Konum izni hazir" else "Konum izni gerekli")
            Text(
                if (panelState.permissions.notificationListenerEnabled) {
                    "Medya kontrolu hazir"
                } else {
                    "Spotify ve YouTube kontrolu icin bildirim erisimi verin"
                },
                color = TextMuted
            )
            Text(
                if (panelState.permissions.exactAlarmReady) "Exact alarm hazir" else "Exact alarm izni gerekli",
                color = TextMuted
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun StatusStrip(panelState: PanelState) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        CompactStatusCard(Icons.Rounded.BatteryChargingFull, "Batarya", "${panelState.battery.level}%", panelState.battery.healthLabel)
        CompactStatusCard(Icons.Rounded.NetworkWifi, "Internet", panelState.connectivity.transport, panelState.connectivity.signalLabel)
        CompactStatusCard(Icons.Rounded.Speed, "Hiz", panelState.location.speed, panelState.location.heading.ifBlank { "-" })
        CompactStatusCard(Icons.Rounded.Storage, "Depolama", panelState.system.storageLabel, if (panelState.system.bluetoothEnabled) "Bluetooth acik" else "Bluetooth kapali")
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
            CardTitle(Icons.Rounded.PlayCircle, "Medya Merkezi")
            Text(panelState.media.title, color = if (panelState.media.permissionRequired) AccentDanger else MaterialTheme.colorScheme.onSurface, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            if (panelState.media.subtitle.isNotBlank()) Text(panelState.media.subtitle, color = TextMuted)
            if (panelState.media.source.isNotBlank()) Text(panelState.media.source, color = TextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MediaAction(Icons.Rounded.SkipPrevious, onMediaPrevious)
                MediaAction(if (panelState.media.playing) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle, onPlayPauseMedia)
                MediaAction(Icons.Rounded.SkipNext, onMediaNext)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ElevatedButton(onClick = onLaunchAssistant) { Text("Assistant") }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun QuickLaunchCard(items: List<QuickLaunchItem>, onLaunchApp: (String) -> Unit) {
    PanelCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CardTitle(Icons.Rounded.Map, "Hizli Baslat")
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
                            Text(if (item.installed) item.packageName else "Uygulama yok", color = TextMuted, fontSize = 12.sp)
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
            CardTitle(Icons.Rounded.Alarm, "Alarmlar")
            if (panelState.alarms.isEmpty()) {
                Text("Henuz alarm yok", color = TextMuted)
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
                        Text(alarm.time, fontWeight = FontWeight.Bold)
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
            CardTitle(Icons.Rounded.Assistant, "Sistem")
            Text(if (panelState.system.assistantAvailable) "Assistant hazir" else "Assistant erisilemiyor", fontWeight = FontWeight.SemiBold)
            Text(panelState.connectivity.lastSeenOnline.ifBlank { "Baglanti durumu izleniyor" }, color = TextMuted)
        }
    }
}

@Composable
private fun FloatingSettingsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier, color = SurfaceSecondary, shadowElevation = 14.dp, shape = CircleShape) {
        IconButton(onClick = onClick, modifier = Modifier.size(68.dp)) {
            Icon(Icons.Rounded.Settings, contentDescription = "Ayarlar", tint = AccentSky)
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
    onAddAlarm: (String, Int, Int, Boolean) -> Unit
) {
    var weatherRefresh by remember(settings.weatherRefreshMinutes) { mutableStateOf(settings.weatherRefreshMinutes.toString()) }
    var locationRefresh by remember(settings.locationRefreshSeconds) { mutableStateOf(settings.locationRefreshSeconds.toString()) }
    var newPin by remember { mutableStateOf("") }
    var alarmTitle by remember { mutableStateOf("") }
    var alarmHour by remember { mutableStateOf("") }
    var alarmMinute by remember { mutableStateOf("") }
    var repeatDaily by remember { mutableStateOf(true) }
    val slotLabels = remember(settings.quickLaunchSlots) { mutableStateListOf(*settings.quickLaunchSlots.toTypedArray()) }

    PanelCard(
        modifier = Modifier
            .width(500.dp)
            .padding(start = 24.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Admin Panel", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onClose) { Text("Kapat") }
            }

            if (!unlocked) {
                Text("Kiosk ve kritik ayarlar icin PIN gerekli.", color = TextMuted)
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = onPinChange,
                    label = { Text("Admin PIN") },
                    isError = pinError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (pinError != null) Text(pinError, color = AccentDanger)
                Button(onClick = onUnlock, modifier = Modifier.fillMaxWidth()) { Text("Panele Gir") }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Kiosk modu")
                    Switch(checked = settings.kioskMode, onCheckedChange = { onToggleKiosk() })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Offline hava cache")
                    Switch(checked = settings.useOfflineWeatherCache, onCheckedChange = { onToggleOfflineWeather() })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weatherRefresh,
                        onValueChange = {
                            weatherRefresh = it.filter(Char::isDigit)
                            weatherRefresh.toIntOrNull()?.let(onUpdateWeatherRefresh)
                        },
                        label = { Text("Hava dk") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = locationRefresh,
                        onValueChange = {
                            locationRefresh = it.filter(Char::isDigit)
                            locationRefresh.toIntOrNull()?.let(onUpdateLocationRefresh)
                        },
                        label = { Text("Konum sn") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { newPin = it.filter(Char::isDigit).take(6) },
                    label = { Text("Yeni admin PIN") },
                    modifier = Modifier.fillMaxWidth()
                )
                ElevatedButton(onClick = { if (newPin.length >= 4) onUpdatePin(newPin) }) {
                    Text("PIN Guncelle")
                }

                Text("Hizli baslat slotlari", fontWeight = FontWeight.SemiBold)
                slotLabels.forEachIndexed { index, config ->
                    QuickLaunchEditor(
                        config = config,
                        onChanged = { updated ->
                            slotLabels[index] = updated
                            onUpdateQuickLaunch(updated.slot, updated.label, updated.packageName)
                        }
                    )
                }

                Text("Yeni alarm", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(value = alarmTitle, onValueChange = { alarmTitle = it }, label = { Text("Baslik") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = alarmHour, onValueChange = { alarmHour = it.filter(Char::isDigit).take(2) }, label = { Text("Saat") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = alarmMinute, onValueChange = { alarmMinute = it.filter(Char::isDigit).take(2) }, label = { Text("Dakika") }, modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Her gun tekrar")
                    Switch(checked = repeatDaily, onCheckedChange = { repeatDaily = it })
                }
                ElevatedButton(
                    onClick = {
                        val hour = alarmHour.toIntOrNull()
                        val minute = alarmMinute.toIntOrNull()
                        if (alarmTitle.isNotBlank() && hour != null && minute != null && hour in 0..23 && minute in 0..59) {
                            onAddAlarm(alarmTitle, hour, minute, repeatDaily)
                            alarmTitle = ""
                            alarmHour = ""
                            alarmMinute = ""
                        }
                    }
                ) {
                    Text("Alarm Ekle")
                }
            }
        }
    }
}

@Composable
private fun QuickLaunchEditor(config: QuickLaunchConfig, onChanged: (QuickLaunchConfig) -> Unit) {
    var label by remember(config.label) { mutableStateOf(config.label) }
    var packageName by remember(config.packageName) { mutableStateOf(config.packageName) }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = label,
            onValueChange = {
                label = it
                onChanged(config.copy(label = it, packageName = packageName))
            },
            label = { Text("Etiket ${config.slot + 1}") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = packageName,
            onValueChange = {
                packageName = it
                onChanged(config.copy(label = label, packageName = it))
            },
            label = { Text("Paket adi") },
            modifier = Modifier.weight(1.4f)
        )
    }
}

@Composable
private fun PanelCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfacePrimary.copy(alpha = 0.92f)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
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
private fun MediaAction(icon: ImageVector, onClick: () -> Unit) {
    Surface(color = SurfaceSecondary, shape = CircleShape) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = null)
        }
    }
}
