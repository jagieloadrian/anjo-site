package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toAttrs
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Div
import com.anjo.anjosite.HeadlineTextStyle
import com.anjo.anjosite.SubheadlineTextStyle
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.toSitePalette

// Foundation-phase placeholder (spec 001-foundation-setup, FR-010): demonstrates the ported design
// tokens/typefaces/overlays on a real page. No real copy, no navigation, no interactive elements —
// those land in later phases per ROADMAP.md.
@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Home"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    val sitePalette = ColorMode.current.toSitePalette()

    Column(Modifier.gap(1.cssRem)) {
        Div(HeadlineTextStyle.toAttrs()) {
            SpanText("anjo-site", Modifier.color(sitePalette.pink))
        }
        Div(SubheadlineTextStyle.toAttrs()) {
            SpanText("Foundation in place — tokens, type, and overlays are live.")
        }
    }
}
