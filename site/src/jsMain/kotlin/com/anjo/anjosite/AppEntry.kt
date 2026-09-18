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

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        Style(cssRules = SiteStyles.cssRules)
        var theme by remember { mutableStateOf(detectInitialTheme()) }
        LaunchedEffect(theme) {
            document.documentElement?.setAttribute("data-theme", theme.attrValue)
        }
        CompositionLocalProvider(
            LocalTheme provides theme, LocalThemeSetter provides { theme = it; persistTheme(it) },
        ) {
            Div(attrs = { classes("app"); style { property("display", "flex"); property("flex-direction", "column") } }) {
                Div(attrs = { classes("fx-scan") })
                Div(attrs = { classes("fx-vignette") })
                Div(attrs = { classes("fx-grid") })
                content()
            }
        }
    }
}
