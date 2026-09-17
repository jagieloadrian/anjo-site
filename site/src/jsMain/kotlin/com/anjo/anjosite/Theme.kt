package com.anjo.anjosite

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.browser.window

// Port of docs/handoff/app.js's theme toggle: one `data-theme` attribute on <html> flips every
// token in styles.css (see docs/handoff/README.md "Light mode"). Same shape as Lang.kt's
// LocalLang/LocalLangSetter pair.
enum class Theme {
    DARK,
    LIGHT;

    val other: Theme get() = if (this == DARK) LIGHT else DARK
    val attrValue: String get() = if (this == DARK) "dark" else "light"
}

val LocalTheme: ProvidableCompositionLocal<Theme> = staticCompositionLocalOf { Theme.DARK }
val LocalThemeSetter: ProvidableCompositionLocal<(Theme) -> Unit> = staticCompositionLocalOf { {} }

private const val THEME_STORAGE_KEY = "aj-theme"

// Stored choice wins; otherwise follow the OS setting — same precedence as app.js's applyTheme.
fun detectInitialTheme(): Theme {
    val stored = try {
        window.localStorage.getItem(THEME_STORAGE_KEY)
    } catch (t: Throwable) {
        null
    }
    return when (stored) {
        "light" -> Theme.LIGHT
        "dark" -> Theme.DARK
        else -> if (window.matchMedia("(prefers-color-scheme: light)").matches) Theme.LIGHT else Theme.DARK
    }
}

fun persistTheme(theme: Theme) {
    try {
        window.localStorage.setItem(THEME_STORAGE_KEY, theme.attrValue)
    } catch (t: Throwable) {
        // ponytail: localStorage can throw in private-browsing contexts; theme still applies for the session.
    }
}
