///
/// StringExtensions.kt
/// Created by Claude (Prompted by Rashid): 26/12/2024
///
/// Convenience extensions for localisation access

package com.blaink.core.localisation

import java.util.Locale

/**
 * Get localized string for this key using current language
 *
 * Usage:
 * ```
 * val title = "onboarding.title".localized
 * ```
 *
 * @return Translated string or the key itself if not found
 */
val String.localized: String
    get() = (LocalisationStorage.getString(this, LocalisationStorage.currentLanguage) ?: this).unescapeNewlines()

/**
 * Get localized string for this key in specified language
 *
 * Usage:
 * ```
 * val titleTR = "onboarding.title".localized("tr")
 * ```
 *
 * @param lang Language code (e.g., "tr", "en")
 * @return Translated string or the key itself if not found
 */
fun String.localized(lang: String): String {
    return (LocalisationStorage.getString(this, lang) ?: this).unescapeNewlines()
}

/**
 * Get localized string or null if not found
 *
 * Usage:
 * ```
 * val title = "onboarding.title".localizedOrNull ?: "Default"
 * ```
 */
val String.localizedOrNull: String?
    get() = LocalisationStorage.getString(this, LocalisationStorage.currentLanguage)?.unescapeNewlines()

/**
 * Returns localized text in uppercase with the current locale
 *
 * Usage:
 * ```
 * val title = "onboarding.title".localizedWithUppercase // "HELLO WORLD"
 * ```
 */
val String.localizedWithUppercase: String
    get() = localized.uppercase(Locale.getDefault())

/**
 * Returns localized text in lowercase with the current locale
 *
 * Usage:
 * ```
 * val title = "onboarding.title".localizedWithLowercase // "hello world"
 * ```
 */
val String.localizedWithLowercase: String
    get() = localized.lowercase(Locale.getDefault())

/**
 * Returns localized text with first letter of each word capitalized
 *
 * Usage:
 * ```
 * val title = "onboarding.title".localizedWithCapitalization // "Hello World"
 * ```
 */
val String.localizedWithCapitalization: String
    get() = localized.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

/**
 * Returns localized text with placeholder arguments replaced
 * Uses {1}, {2}, etc. as placeholders
 *
 * Usage:
 * ```
 * // If "greeting" = "Hello {1}, welcome to {2}!"
 * val text = "greeting".localized("John", "Blaink")
 * // Result: "Hello John, welcome to Blaink!"
 * ```
 *
 * @param arguments Arguments to replace placeholders
 */
fun String.localized(vararg arguments: String): String {
    var result = localized
    arguments.forEachIndexed { index, arg ->
        result = result.replace("{${index + 1}}", arg)
    }
    return result
}

/**
 * Converts literal "\n" to actual newline characters
 */
private fun String.unescapeNewlines(): String {
    return replace("\\n", "\n")
}
