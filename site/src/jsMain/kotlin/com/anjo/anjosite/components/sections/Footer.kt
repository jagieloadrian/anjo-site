package com.anjo.anjosite.components.sections

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

// Literal port of docs/handoff/index.html's <footer class="footer"> — back to the mock's own
// --red (brighter-red override reverted per user feedback).
@Composable
fun Footer() {
    Div(attrs = { classes("footer") }) {
        Div(attrs = { classes("footer-big") }) { Text("BUILT WITH KOTLIN/JS") }
        Div(attrs = { classes("footer-meta") }) { Text("DEPLOYED ON GITHUB PAGES · 2026") }
    }
}
