package com.anjo.anjosite.components.sections

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

// Literal port of docs/handoff/index.html's <footer class="footer">, with one deliberate deviation
// from the mock: request #1 wants a brighter "blood red" than the mock's --red (#ec3013). Uses
// the mock's own --red-lt dark-mode value as a literal (not the var — --red-lt flips darker in
// light mode, the wrong direction for this ask), so the footer stays equally bright either theme.
@Composable
fun Footer() {
    Div(attrs = { classes("footer"); style { property("background", "#ff6b52") } }) {
        Div(attrs = { classes("footer-big") }) { Text("BUILT WITH KOTLIN/JS") }
        Div(attrs = { classes("footer-meta") }) { Text("DEPLOYED ON GITHUB PAGES · 2026") }
    }
}
