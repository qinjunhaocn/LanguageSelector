package com.xyz.langselector.data

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getPinnedApps(): Set<String> {
        return prefs.getStringSet(KEY_PINNED_APPS, emptySet()) ?: emptySet()
    }

    fun togglePinnedApp(packageName: String): Boolean {
        val current = getPinnedApps().toMutableSet()
        val added = if (packageName in current) {
            current.remove(packageName)
            false
        } else {
            current.add(packageName)
            true
        }
        prefs.edit().putStringSet(KEY_PINNED_APPS, current).apply()
        return added
    }

    fun isPinned(packageName: String): Boolean {
        return packageName in getPinnedApps()
    }

    fun setShowSystemApps(show: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_SYSTEM_APPS, show).apply()
    }

    fun getShowSystemApps(): Boolean {
        return prefs.getBoolean(KEY_SHOW_SYSTEM_APPS, false)
    }

    companion object {
        private const val PREFS_NAME = "lang_selector_prefs"
        private const val KEY_PINNED_APPS = "pinned_apps"
        private const val KEY_SHOW_SYSTEM_APPS = "show_system_apps"
    }
}
