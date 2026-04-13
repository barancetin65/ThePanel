package com.thepanel.data.service

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.thepanel.data.model.AppInfo

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

    override fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        return resolveInfos.map {
            AppInfo(
                label = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName,
                isSystem = (it.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            )
        }.distinctBy { it.packageName }.sortedBy { it.label }
    }
}
