///
/// LocalisationStorage.kt
/// Created by Claude (Prompted by Rashid): 26/12/2024
///
/// SharedPreferences-based storage for localisation keys
/// Uses flat key format: "{lang}.{key}" for O(1) access

package com.blaink.core.localisation

import android.content.Context
import android.content.SharedPreferences
import java.util.Locale

/**
 * Storage for localisation translations using SharedPreferences
 * Uses flat key format: "{lang}.{key}" for efficient O(1) access
 */
object LocalisationStorage {

    private const val PREFS_NAME = "blaink_localisation"
    private const val VERSION_KEY = "_version"
    private const val LANGUAGES_KEY = "_languages"
    private const val CURRENT_LANGUAGE_KEY = "_currentLanguage"

    private var prefs: SharedPreferences? = null

    /**
     * Initialize storage with application context
     */
    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun requirePrefs(): SharedPreferences {
        return requireNotNull(prefs) { "LocalisationStorage not initialized. Call initialize() first." }
    }

    // MARK: - Version

    /**
     * Current cached version
     */
    var version: Long
        get() = requirePrefs().getLong(VERSION_KEY, 0)
        set(value) = requirePrefs().edit().putLong(VERSION_KEY, value).apply()

    // MARK: - Languages

    /**
     * List of cached languages
     */
    var languages: List<String>
        get() = requirePrefs().getStringSet(LANGUAGES_KEY, emptySet())?.toList() ?: emptyList()
        set(value) = requirePrefs().edit().putStringSet(LANGUAGES_KEY, value.toSet()).apply()

    /**
     * Current language for translations
     */
    var currentLanguage: String
        get() = requirePrefs().getString(CURRENT_LANGUAGE_KEY, null)
            ?: Locale.getDefault().language
        set(value) = requirePrefs().edit().putString(CURRENT_LANGUAGE_KEY, value).apply()

    // MARK: - Get

    /**
     * Get translation for key in specified language
     * @param key Localisation key (e.g., "onboarding.title")
     * @param lang Language code (e.g., "tr", "en")
     * @return Translated string or null if not found
     */
    fun getString(key: String, lang: String): String? {
        return requirePrefs().getString("$lang.$key", null)
    }

    /**
     * Get all translations for a key
     * @param key Localisation key
     * @return Map of [lang: value] or empty if not found
     */
    fun getTranslations(key: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (lang in languages) {
            getString(key, lang)?.let { result[lang] = it }
        }
        return result
    }

    // MARK: - Set

    /**
     * Set translation for key in specified language
     */
    fun setString(value: String, key: String, lang: String) {
        requirePrefs().edit().putString("$lang.$key", value).apply()

        // Track language if new
        val currentLangs = languages.toMutableList()
        if (!currentLangs.contains(lang)) {
            currentLangs.add(lang)
            languages = currentLangs
        }
    }

    /**
     * Set all translations for a key
     */
    fun setTranslations(translations: Map<String, String>, key: String) {
        translations.forEach { (lang, value) ->
            setString(value, key, lang)
        }
    }

    // MARK: - Delete

    /**
     * Remove translation for key in specified language
     */
    fun remove(key: String, lang: String) {
        requirePrefs().edit().remove("$lang.$key").apply()
    }

    /**
     * Remove all translations for a key (all languages)
     */
    fun remove(key: String) {
        for (lang in languages) {
            remove(key, lang)
        }
    }

    // MARK: - Bulk Operations

    /**
     * Apply sync response from server
     * @param keys Dictionary of [key: [lang: value]]
     * @param deletedKeys Keys to remove
     * @param newVersion New version number
     */
    fun apply(keys: Map<String, Map<String, String>>, deletedKeys: List<String>?, newVersion: Long) {
        val editor = requirePrefs().edit()

        // Update/add keys
        for ((key, translations) in keys) {
            for ((lang, value) in translations) {
                editor.putString("$lang.$key", value)

                // Track language if new
                val currentLangs = languages.toMutableList()
                if (!currentLangs.contains(lang)) {
                    currentLangs.add(lang)
                    languages = currentLangs
                }
            }
        }

        // Remove deleted keys
        deletedKeys?.forEach { key ->
            for (lang in languages) {
                editor.remove("$lang.$key")
            }
        }

        // Update version
        editor.putLong(VERSION_KEY, newVersion)
        editor.apply()
    }

    // MARK: - Clear

    /**
     * Clear all cached data
     */
    fun clear() {
        requirePrefs().edit().clear().apply()
    }
}
