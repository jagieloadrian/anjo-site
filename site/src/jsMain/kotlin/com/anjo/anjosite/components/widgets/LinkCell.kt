package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

private val linkCellVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

// Literal port of docs/handoff/index.html's <a class="link-cell"> — used by Home's Contact band
// and the dedicated Contact page's channel list (the caller wraps a list of these in
// `<div class="links">` or `<div class="links links--stack">`).
@Composable
fun LinkCell(href: String, key: String, value: String) {
    Link(href, Modifier.classNames("link-cell"), variant = linkCellVariant) {
        Div(attrs = { classes("link-key") }) { Text(key) }
        Div(attrs = { classes("link-val") }) { Text(value) }
    }
}
