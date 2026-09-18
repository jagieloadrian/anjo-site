package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.components.layouts.PageLayoutData

// GitHub Pages serves this at any unmatched path (spec 002-layout-routing FR-003/FR-004,
// research.md §2: route "/404" exports to "404.html"). The not-found message is real
// user-facing prose, so per constitution Principle III it must be bilingual — unlike the
// technical-label-only placeholder pages (Projects.kt etc.), see spec.md Assumptions.
private val NotFoundMessage = BilingualString(en = "Page not found", pl = "Strona nie znaleziona")
private val Description = BilingualString(en = "Page not found", pl = "Strona nie znaleziona")

@InitRoute
fun init404Page(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("404", Description))
}

@Page("/404")
@Layout(".components.layouts.PageLayout")
@Composable
fun Error404Page() {
    SpanText("404", Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(2.cssRem))
    SpanText(NotFoundMessage(LocalLang.current), Modifier.fontFamily("Archivo", "system-ui", "sans-serif").fontSize(1.cssRem))
}
