package com.xyz.langselector.service

import android.os.Build
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * UserService runs inside the Shizuku privileged process (shell/root uid).
 * It executes `cmd locale` commands to set per-app language for any app,
 * replicating the Android 13 "App Languages" feature.
 *
 * This approach references the VegaBobo/Language-Selector implementation method:
 * using a Shizuku-bound privileged service to call the system locale manager,
 * but simplified to use the `cmd locale` shell interface instead of direct
 * Binder calls to ILocaleManager.
 */
class UserService : IUserService.Stub() {

    override fun setApplicationLocales(packageName: String?, localeTag: String?) {
        if (packageName.isNullOrEmpty()) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val tag = if (localeTag.isNullOrEmpty()) "default" else localeTag
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf("cmd", "locale", "set-application-locale", packageName, tag)
            )
            process.waitFor()
            process.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getApplicationLocales(packageName: String?): String {
        if (packageName.isNullOrEmpty()) return ""
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return ""

        return try {
            val process = Runtime.getRuntime().exec(
                arrayOf("cmd", "locale", "get-application-locale", packageName)
            )
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine() ?: ""
            reader.close()
            process.waitFor()
            process.destroy()
            output.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override fun forceStopPackage(packageName: String?) {
        if (packageName.isNullOrEmpty()) return
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf("am", "force-stop", packageName)
            )
            process.waitFor()
            process.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun isLocaleServiceAvailable(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        return try {
            val process = Runtime.getRuntime().exec(
                arrayOf("cmd", "locale", "list")
            )
            val exitCode = process.waitFor()
            process.destroy()
            exitCode == 0
        } catch (e: Exception) {
            false
        }
    }

    override fun destroy() {
        // Cleanup resources when service is destroyed
    }
}
