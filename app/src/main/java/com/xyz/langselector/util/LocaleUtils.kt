package com.xyz.langselector.util

import com.xyz.langselector.data.model.LanguageInfo
import java.util.Locale

object LocaleUtils {

    /**
     * Curated list of common languages for the language picker.
     */
    val supportedLanguages: List<LanguageInfo> = buildList {
        add(LanguageInfo.SYSTEM_DEFAULT)
        add(LanguageInfo("en", "English", "English"))
        add(LanguageInfo("zh-CN", "Chinese (Simplified)", "中文（简体）"))
        add(LanguageInfo("zh-TW", "Chinese (Traditional)", "中文（繁體）"))
        add(LanguageInfo("ja", "Japanese", "日本語"))
        add(LanguageInfo("ko", "Korean", "한국어"))
        add(LanguageInfo("fr", "French", "Français"))
        add(LanguageInfo("de", "German", "Deutsch"))
        add(LanguageInfo("es", "Spanish", "Español"))
        add(LanguageInfo("ru", "Russian", "Русский"))
        add(LanguageInfo("pt", "Portuguese", "Português"))
        add(LanguageInfo("pt-BR", "Portuguese (Brazil)", "Português (Brasil)"))
        add(LanguageInfo("it", "Italian", "Italiano"))
        add(LanguageInfo("ar", "Arabic", "العربية"))
        add(LanguageInfo("hi", "Hindi", "हिन्दी"))
        add(LanguageInfo("th", "Thai", "ไทย"))
        add(LanguageInfo("vi", "Vietnamese", "Tiếng Việt"))
        add(LanguageInfo("id", "Indonesian", "Bahasa Indonesia"))
        add(LanguageInfo("tr", "Turkish", "Türkçe"))
        add(LanguageInfo("nl", "Dutch", "Nederlands"))
        add(LanguageInfo("pl", "Polish", "Polski"))
        add(LanguageInfo("uk", "Ukrainian", "Українська"))
        add(LanguageInfo("sv", "Swedish", "Svenska"))
        add(LanguageInfo("da", "Danish", "Dansk"))
        add(LanguageInfo("fi", "Finnish", "Suomi"))
        add(LanguageInfo("no", "Norwegian", "Norsk"))
        add(LanguageInfo("cs", "Czech", "Čeština"))
        add(LanguageInfo("el", "Greek", "Ελληνικά"))
        add(LanguageInfo("he", "Hebrew", "עברית"))
        add(LanguageInfo("ms", "Malay", "Bahasa Melayu"))
        add(LanguageInfo("ro", "Romanian", "Română"))
        add(LanguageInfo("hu", "Hungarian", "Magyar"))
        add(LanguageInfo("sk", "Slovak", "Slovenčina"))
        add(LanguageInfo("bg", "Bulgarian", "Български"))
        add(LanguageInfo("hr", "Croatian", "Hrvatski"))
        add(LanguageInfo("ca", "Catalan", "Català"))
    }

    /**
     * Get a human-readable display name for a locale tag.
     */
    fun getDisplayName(localeTag: String): String {
        if (localeTag.isBlank()) return "System Default"
        return try {
            val locale = Locale.forLanguageTag(localeTag)
            val name = locale.displayName(Locale.ENGLISH)
            if (name.isBlank()) localeTag else name
        } catch (e: Exception) {
            localeTag
        }
    }

    /**
     * Get the native display name for a locale tag.
     */
    fun getNativeName(localeTag: String): String {
        if (localeTag.isBlank()) return "System Default"
        return try {
            val locale = Locale.forLanguageTag(localeTag)
            val name = locale.getDisplayName(locale)
            if (name.isBlank()) localeTag else name
        } catch (e: Exception) {
            localeTag
        }
    }

    /**
     * Parse the output of `cmd locale get-application-locale` command.
     * The output format is typically a BCP 47 tag or empty for system default.
     */
    fun parseLocaleOutput(output: String): String {
        val trimmed = output.trim()
        if (trimmed.isBlank() || trimmed == "[]") return ""
        // Remove brackets if present (some Android versions wrap in [])
        return trimmed.removeSurrounding("[", "]").trim()
    }

    /**
     * Find a LanguageInfo from the supported list by tag.
     */
    fun findLanguage(tag: String): LanguageInfo? {
        return supportedLanguages.find { it.tag == tag }
    }

    /**
     * Search languages by query.
     */
    fun searchLanguages(query: String): List<LanguageInfo> {
        if (query.isBlank()) return supportedLanguages
        val lowerQuery = query.lowercase()
        return supportedLanguages.filter {
            it.displayName.lowercase().contains(lowerQuery) ||
            it.nativeName.lowercase().contains(lowerQuery) ||
            it.tag.lowercase().contains(lowerQuery)
        }
    }
}
