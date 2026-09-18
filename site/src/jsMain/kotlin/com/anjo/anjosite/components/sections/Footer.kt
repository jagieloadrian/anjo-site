package com.anjo.anjosite.components.sections

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun Footer() {
    Div(attrs = { classes("footer") }) {
        Div(attrs = { classes("footer-big") }) { Text("BUILT WITH KOTLIN/JS") }
        Div(attrs = { classes("footer-meta") }) { Text("DEPLOYED ON GITHUB PAGES · 2026") }
    }
}
