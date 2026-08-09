package com.xyz.langselector.data.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
    val currentLocale: String = "",
    val isEnabled: Boolean = true
) {
    val displayLocale: String
        get() = if (currentLocale.isBlank()) "System Default" else currentLocale
}
