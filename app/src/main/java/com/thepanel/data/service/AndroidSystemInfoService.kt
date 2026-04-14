package com.thepanel.data.service

import android.Manifest
import android.app.AlarmManager
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.thepanel.data.model.PermissionState

class AndroidSystemInfoService(
    private val context: Context
) : SystemInfoService {
    override fun bluetoothEnabled(): Boolean = runCatching {
        BluetoothAdapter.getDefaultAdapter()?.isEnabled == true
    }.getOrDefault(false)

    override fun storageLabel(): String {
        val stat = StatFs(Environment.getDataDirectory().path)
        val totalGb = stat.totalBytes / 1024 / 1024 / 1024
        val freeGb = stat.availableBytes / 1024 / 1024 / 1024
        val usedPercent = (((totalGb - freeGb).toFloat() / totalGb.coerceAtLeast(1)) * 100).toInt()
        return "$totalGb GB / $usedPercent% used"
    }

    override fun permissionState(): PermissionState {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners").orEmpty()
        val listenerEnabled = enabledListeners.contains(ComponentName(context, PanelNotificationListenerService::class.java).flattenToString())
        return PermissionState(
            hasLocationPermission = hasLocationPermission(),
            notificationListenerEnabled = listenerEnabled,
            exactAlarmReady = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms() else true
        )
    }

    override fun assistantAvailable(): Boolean {
        val intent = android.content.Intent(android.content.Intent.ACTION_ASSIST)
        return intent.resolveActivity(context.packageManager) != null
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }
}
