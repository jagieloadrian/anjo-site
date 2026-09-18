package com.anjo.anjosite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.anjo.anjosite.styles.SiteStyles
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Style

// Visual styling for the whole site is the `com.anjo.anjosite.styles` package (mechanically
// transcribed from docs/handoff/styles.css, then split into one file per component — request:
// move CSS into Kotlin, no more mock changes planned), mounted here via plain Compose HTML
// `Style()`, applied via literal class names, not Silk CssStyle/ColorMode. Deliberately not
// Silk's `@InitSilk`/`CssStyle` machinery: Kobweb wraps that output in `@layer general-styles`,
// and CSS gives any unlayered rule priority over any layered one regardless of specificity — a
// real bug we hit with a hover style earlier this session. Plain `StyleSheet()`s mounted via
// `Style()` stay unlayered, exactly like the external stylesheet they replace. SilkApp is kept
// only because a couple of Silk widgets (TextInput/Button in ContactPrompt) still need SilkTheme
// present.
@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Style(cssRules = SiteStyles.cssRules)
        // Single app-wide LocalLang provider (spec 002-layout-routing FR-008, data-model.md's
        // Lang validation rule: exactly one provider must exist). No localStorage persistence
        // this phase — in-memory only, re-detected fresh on every page load (spec Edge Cases).
        var lang by remember { mutableStateOf(detectInitialLang()) }
        var theme by remember { mutableStateOf(detectInitialTheme()) }
        LaunchedEffect(theme) {
            document.documentElement?.setAttribute("data-theme", theme.attrValue)
        }
        CompositionLocalProvider(
            LocalLang provides lang, LocalLangSetter provides { lang = it },
            LocalTheme provides theme, LocalThemeSetter provides { theme = it; persistTheme(it) },
        ) {
            // Sticky footer (request #3): .app is already min-height:100vh (styles.css) — making
            // it a flex column and letting .screen (below) grow lets short pages still push the
            // footer to the true bottom instead of leaving a gap after it.
            Div(attrs = { classes("app"); style { property("display", "flex"); property("flex-direction", "column") } }) {
                Div(attrs = { classes("fx-scan") })
                Div(attrs = { classes("fx-vignette") })
                Div(attrs = { classes("fx-grid") })
                content()
            }
        }
    }
}
