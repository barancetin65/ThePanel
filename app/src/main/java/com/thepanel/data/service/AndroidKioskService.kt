package com.thepanel.data.service

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import com.thepanel.admin.PanelDeviceAdminReceiver

class AndroidKioskService(
    private val context: Context
) : KioskService {
    private val prefs = context.getSharedPreferences("kiosk_state", Context.MODE_PRIVATE)
    private val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    private val adminComponent = ComponentName(context, PanelDeviceAdminReceiver::class.java)

    override fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("enabled", enabled).apply()
        if (isDeviceOwner()) {
            if (enabled) {
                devicePolicyManager.setLockTaskPackages(adminComponent, arrayOf(context.packageName))
                devicePolicyManager.setStatusBarDisabled(adminComponent, true)
            } else {
                devicePolicyManager.setStatusBarDisabled(adminComponent, false)
                devicePolicyManager.setLockTaskPackages(adminComponent, emptyArray())
            }
        }
    }

    override fun isEnabled(): Boolean = prefs.getBoolean("enabled", false)

    fun isDeviceOwner(): Boolean = devicePolicyManager.isDeviceOwnerApp(context.packageName)
}
