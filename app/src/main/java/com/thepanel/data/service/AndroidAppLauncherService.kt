package com.thepanel.data.service

import android.content.Context
import android.content.Intent

class AndroidAppLauncherService(
    private val context: Context
) : AppLauncherService {
    override fun launch(packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    override fun isInstalled(packageName: String): Boolean {
        return runCatching { context.packageManager.getPackageInfo(packageName, 0) }.isSuccess
    }

    override fun appLabel(packageName: String): String? {
        return runCatching {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            context.packageManager.getApplicationLabel(appInfo).toString()
        }.getOrNull()
    }
}
