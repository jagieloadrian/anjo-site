package com.anjo.anjosite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import org.jetbrains.compose.web.dom.Div

// Visual styling for the whole site comes from the mock's own stylesheet
// (docs/handoff/styles.css, served as /styles.css — see build.gradle.kts), applied via literal
// class names, not Silk CssStyle/ColorMode. SilkApp is kept only because a couple of Silk widgets
// (TextInput/Button in ContactPrompt) still need SilkTheme present; there is no light/dark toggle
// or Silk-driven palette in the mock, so none of that plumbing exists here anymore.
@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        // Single app-wide LocalLang provider (spec 002-layout-routing FR-008, data-model.md's
        // Lang validation rule: exactly one provider must exist). No localStorage persistence
        // this phase — in-memory only, re-detected fresh on every page load (spec Edge Cases).
        var lang by remember { mutableStateOf(detectInitialLang()) }
        CompositionLocalProvider(LocalLang provides lang, LocalLangSetter provides { lang = it }) {
            Div(attrs = { classes("app") }) {
                Div(attrs = { classes("fx-scan") })
                Div(attrs = { classes("fx-vignette") })
                Div(attrs = { classes("fx-grid") })
                content()
            }
        }
    }
}
