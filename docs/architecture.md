# Architecture

## Core idea

Uygulama `offline-first` bir Android kiosk dashboard olarak tasarlandi.

- `ui/`: Lovelace tarzi Compose ekranlari
- `data/model/`: UI'nin tükettigi durum modelleri
- `data/local/`: Room tabloları
- `data/settings/`: DataStore tabanli admin ayarlari
- `data/repository/`: tum gercek servisleri tek state agacinda birlestirir
- `data/service/`: Android servisleri ve ag istemcileri

## Live data sources

- `Open-Meteo`: canli forecast, Room cache
- `FusedLocationProvider`: koordinat ve hiz
- `Geocoder`: il, ilce, ulke
- `ConnectivityManager`: baglanti durumu
- `BatteryManager`: pil seviyesi
- `MediaSessionManager`: aktif medya session kontrolu
- `AlarmManager`: exact alarm
- `PackageManager`: uygulama baslatma
- `SharedPreferences + lock task`: kiosk tercihi ve Activity davranisi

## Current gaps

- Bildirimli alarm var, tam ekran alarm deneyimi daha da zenginlestirilebilir
- Medya ekrani aktif session uzerinden calisir; Spotify App Remote eklenirse daha zengin olur
- Tam kurumsal kiosk icin device-owner provisioning gerekebilir
