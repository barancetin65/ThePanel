package com.thepanel.data.util

import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale("tr"))
private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy EEEE", Locale("tr"))
private val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale("tr"))
private val coordinateFormatter = DecimalFormat("0.000000")

fun nowTimeLabel(zoneId: ZoneId): String = LocalDateTime.now(zoneId).format(timeFormatter)

fun nowDateLabel(zoneId: ZoneId): String = LocalDateTime.now(zoneId).format(dateFormatter)

fun formatDayOfWeek(isoDate: String): String {
    return runCatching {
        java.time.LocalDate.parse(isoDate).format(dayFormatter)
    }.getOrDefault("")
}

fun formatClockZone(zoneId: ZoneId): String = zoneId.id

fun formatTemperature(valueCelsius: Double?): String = valueCelsius?.let { "${it.roundToInt()}Â°" } ?: "--"

fun formatFeelsLike(valueCelsius: Double?): String = valueCelsius?.let { "Hissedilen ${it.roundToInt()}Â°" }.orEmpty()

fun formatWind(kmh: Double?): String = kmh?.let { "RÃ¼zgar ${it.roundToInt()} km/s" }.orEmpty()

fun formatHumidity(percent: Int?): String = percent?.let { "Nem %$it" }.orEmpty()

fun formatIsoTime(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return runCatching {
        Instant.parse(iso).atZone(ZoneId.systemDefault()).format(timeFormatter)
    }.getOrElse {
        runCatching {
            LocalDateTime.parse(iso).format(timeFormatter)
        }.getOrDefault("")
    }
}

fun formatUpdatedAt(epochMs: Long?): String {
    if (epochMs == null || epochMs <= 0) return "HenÃ¼z senkron yok"
    val dt = Instant.ofEpochMilli(epochMs).atZone(ZoneId.systemDefault()).format(timeFormatter)
    return "Son gÃ¼ncelleme $dt"
}

fun formatCoordinate(value: Double?): String = value?.let { coordinateFormatter.format(it) } ?: "--"

fun formatSpeedMps(value: Float?): String = value?.let { "${(it * 3.6f).roundToInt()} km/s" } ?: "-- km/s"

fun formatHeading(value: Float?): String = value?.let { "${it.roundToInt()}Â°" }.orEmpty()

fun formatAccuracy(value: Float?): String = value?.let { "${it.roundToInt()} m doÄŸruluk" }.orEmpty()

fun formatAltitude(value: Double?): String = value?.let { "${it.roundToInt()} m" }.orEmpty()

fun formatAlarmTime(hour: Int, minute: Int): String = "%02d:%02d".format(hour, minute)
