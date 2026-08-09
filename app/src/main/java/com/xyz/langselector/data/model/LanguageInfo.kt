package com.xyz.langselector.data.model

data class LanguageInfo(
    val tag: String,
    val displayName: String,
    val nativeName: String,
    val isSystemDefault: Boolean = false
) {
    companion object {
        val SYSTEM_DEFAULT = LanguageInfo(
            tag = "",
            displayName = "System Default",
            nativeName = "System Default",
            isSystemDefault = true
        )
    }
}
