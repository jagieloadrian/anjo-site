package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Div
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.toSitePalette
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.LocalLang

// Foundation-phase placeholder (spec 001-foundation-setup, FR-010): proves the ported tokens and
// typefaces render, using only technical labels (token/font names), never narrative prose — this
// keeps it exempt from bilingual parity per constitution Principle III ("Technical labels, stack
// names, and tags stay English in both languages"). A descriptive sentence would NOT be exempt;
// see specs/001-foundation-setup/tasks.md T007/D1 for why this shape was chosen over one.
@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Home"))
}

@Composable
private fun Swatch(color: Color, label: String) {
    Column(Modifier.gap(0.5.cssRem), horizontalAlignment = Alignment.CenterHorizontally) {
        Div(
            Modifier
                .width(3.cssRem)
                .height(3.cssRem)
                .backgroundColor(color)
                .borderRadius(0.5.cssRem)
                .toAttrs()
        )
        SpanText(label, Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(0.75.cssRem))
    }
}

// Demonstrates the language-state mechanism end-to-end (spec 002-layout-routing FR-008/FR-009):
// one Bilingual String, selected by LocalLang.current — proves the mechanism without introducing
// real page copy (Phase 3's job).
private val LangDemoString = BilingualString(en = "Language demo", pl = "Demo języka")

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    val sitePalette = ColorMode.current.toSitePalette()

    Column(Modifier.gap(2.cssRem)) {
        Row(Modifier.gap(1.cssRem)) {
            Swatch(sitePalette.background, "bg")
            Swatch(sitePalette.ink, "ink")
            Swatch(sitePalette.pink, "pink")
            Swatch(sitePalette.cyan, "cyan")
            Swatch(sitePalette.red, "red")
        }
        SpanText("Archivo", Modifier.fontFamily("Archivo", "system-ui", "sans-serif").fontSize(2.cssRem))
        SpanText("JetBrains Mono", Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(1.cssRem))
        SpanText(LangDemoString(LocalLang.current), Modifier.fontFamily("Archivo", "system-ui", "sans-serif").fontSize(1.cssRem))
    }
}
