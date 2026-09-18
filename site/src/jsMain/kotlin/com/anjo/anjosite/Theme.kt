package com.anjo.anjosite

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.browser.window

enum class Theme {
    DARK,
    LIGHT;

    val other: Theme get() = if (this == DARK) LIGHT else DARK
    val attrValue: String get() = if (this == DARK) "dark" else "light"
}

val LocalTheme: ProvidableCompositionLocal<Theme> = staticCompositionLocalOf { Theme.DARK }
val LocalThemeSetter: ProvidableCompositionLocal<(Theme) -> Unit> = staticCompositionLocalOf { {} }

private const val THEME_STORAGE_KEY = "aj-theme"

fun detectInitialTheme(): Theme {
    val stored = try {
        window.localStorage.getItem(THEME_STORAGE_KEY)
    } catch (t: Throwable) {
        Log.warn("Theme", "localStorage read failed, falling back to system preference", t)
        null
    }
    val theme = when (stored) {
        "light" -> Theme.LIGHT
        "dark" -> Theme.DARK
        else -> if (window.matchMedia("(prefers-color-scheme: light)").matches) Theme.LIGHT else Theme.DARK
    }
    Log.info("Theme", "initial theme resolved to ${theme.attrValue} (stored=$stored)")
    return theme
}

fun persistTheme(theme: Theme) {
    try {
        window.localStorage.setItem(THEME_STORAGE_KEY, theme.attrValue)
    } catch (t: Throwable) {
        Log.warn("Theme", "localStorage write failed, theme won't persist across reloads", t)
    }
}
