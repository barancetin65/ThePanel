# The Panel

Karavan tableti icin tasarlanmis, Home Assistant Lovelace hissinde calisan Android kiosk dashboard uygulamasi.

## Bu surumde gercek olanlar

- Gercek saat ve tarih
- Fused Location Provider ile canli koordinat ve GPS hiz
- Geocoder ile il, ilce, ulke cozumleme
- Open-Meteo ile canli hava durumu ve Room cache
- BatteryManager ile pil durumu
- ConnectivityManager ile internet durumu
- AlarmManager ile gercek alarm planlama
- Notification listener tabanli medya oturumu kontrolu
- PackageManager ile gercek uygulama baslatma
- PIN korumali admin paneli
- DataStore ile ayarlarin kalici saklanmasi

## Kurulum notlari

- Android Studio ile projeyi acin
- Ilk acilista konum ve bildirim izinlerini verin
- Medya kontrolu icin bildirim erisimi verin
- Exact alarm izni gerekiyorsa sistem ayarlarindan acin
- Hizli baslat slotlarina kullanmak istediginiz paket adlarini admin panelden girin

## Sinirlar

- Bu ortamda `java` ve `gradle` olmadigi icin burada derleme yapilamadi
- Kiosk modu Android cihazda tam kilit icin device-owner yapilandirmasi isteyebilir
- YouTube kontrolu aktif media session mevcutsa calisir
