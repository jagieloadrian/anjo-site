package com.anjo.anjosite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.loadFromLocalStorage
import com.varabyte.kobweb.silk.theme.colors.saveToLocalStorage
import com.varabyte.kobweb.silk.theme.colors.systemPreference
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.dom.Div

private const val COLOR_MODE_KEY = "anjosite:colorMode"

@InitSilk
fun initColorMode(ctx: InitSilkContext) {
    ctx.config.initialColorMode = ColorMode.loadFromLocalStorage(COLOR_MODE_KEY) ?: ColorMode.systemPreference
}

@InitSilk
fun initStyles(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        registerStyleBase("body") { Modifier.scrollBehavior(ScrollBehavior.Smooth) }
    }
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = ColorMode.current
        LaunchedEffect(colorMode) {
            colorMode.saveToLocalStorage(COLOR_MODE_KEY)
        }
        // Single app-wide LocalLang provider (spec 002-layout-routing FR-008, data-model.md's
        // Lang validation rule: exactly one provider must exist). No localStorage persistence
        // this phase — in-memory only, re-detected fresh on every page load (spec Edge Cases).
        var lang by remember { mutableStateOf(detectInitialLang()) }
        CompositionLocalProvider(LocalLang provides lang, LocalLangSetter provides { lang = it }) {
            Surface(
                SmoothColorStyle.toModifier()
                    .id("site-surface")
                    .minHeight(100.vh)
                    .position(Position.Relative)
            ) {
                // Decorative overlay chrome (docs/handoff/index.html .fx-scan/.fx-vignette/.fx-grid),
                // styled in AppStyles.kt — rendered once here so every page gets it (T006).
                Div(attrs = { classes("fx-scan") })
                Div(attrs = { classes("fx-vignette") })
                Div(attrs = { classes("fx-grid") })
                content()
            }
        }
    }
}
