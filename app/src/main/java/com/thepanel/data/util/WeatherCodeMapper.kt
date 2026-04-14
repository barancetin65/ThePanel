package com.thepanel.data.util

fun weatherSummaryFromCode(code: Int?): String {
    return when (code) {
        0 -> "Clear"
        1, 2 -> "Partly cloudy"
        3 -> "Overcast"
        45, 48 -> "Foggy"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61, 63, 65 -> "Rainy"
        66, 67 -> "Freezing rain"
        71, 73, 75 -> "Snowy"
        77 -> "Snow grains"
        80, 81, 82 -> "Showers"
        85, 86 -> "Snow showers"
        95 -> "Storm"
        96, 99 -> "Hail risk"
        else -> "Unknown"
    }
}
