package com.thepanel.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val summary: String,
    val temperatureC: Double,
    val feelsLikeC: Double,
    val windSpeedKmh: Double,
    val humidityPercent: Int,
    val sunriseIso: String,
    val sunsetIso: String,
    val fetchedAtEpochMs: Long,
    val weatherCode: Int,
    val forecastJson: String = ""
)
