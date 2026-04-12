package com.thepanel.data.util

fun weatherSummaryFromCode(code: Int?): String {
    return when (code) {
        0 -> "AÃ§Ä±k"
        1, 2 -> "ParÃ§alÄ± bulutlu"
        3 -> "KapalÄ±"
        45, 48 -> "Sisli"
        51, 53, 55 -> "Ã‡iseli"
        56, 57 -> "Donan Ã§isenti"
        61, 63, 65 -> "YaÄŸmurlu"
        66, 67 -> "Donan yaÄŸmur"
        71, 73, 75 -> "KarlÄ±"
        77 -> "Kar taneli"
        80, 81, 82 -> "SaÄŸanak"
        85, 86 -> "Kar saÄŸanaÄŸÄ±"
        95 -> "FÄ±rtÄ±na"
        96, 99 -> "Dolu riski"
        else -> "Bilinmiyor"
    }
}
