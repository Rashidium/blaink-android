///
/// StringExtensions.kt
/// Created by Claude (Prompted by Rashid): 26/12/2024
///
/// Convenience extensions for localisation access

package com.blaink.core.localisation

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
 * Converts literal "\n" to actual newline characters
 */
private fun String.unescapeNewlines(): String {
    return replace("\\n", "\n")
}
