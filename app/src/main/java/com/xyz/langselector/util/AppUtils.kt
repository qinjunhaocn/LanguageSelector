package com.xyz.langselector.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.xyz.langselector.data.model.AppInfo

object AppUtils {

    /**
     * Get all installed apps (user apps, excluding system apps by default).
     */
    fun getInstalledApps(
        context: Context,
        includeSystemApps: Boolean = false
    ): List<AppInfo> {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return packages
            .filter { appInfo ->
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                appInfo.enabled && (includeSystemApps || !isSystem || isUpdatedSystem)
            }
            .filter { it.packageName != context.packageName }
            .map { appInfo ->
                AppInfo(
                    packageName = appInfo.packageName,
                    name = packageManager.getApplicationLabel(appInfo).toString(),
                    icon = try {
                        packageManager.getApplicationIcon(appInfo)
                    } catch (e: Exception) {
                        null
                    },
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                    isEnabled = appInfo.enabled
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    /**
     * Get app icon by package name.
     */
    fun getAppIcon(context: Context, packageName: String): Drawable? {
        return try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get app name by package name.
     */
    fun getAppName(context: Context, packageName: String): String? {
        return try {
            val packageManager = context.packageManager
            val info = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(info).toString()
        } catch (e: Exception) {
            null
        }
    }
}
