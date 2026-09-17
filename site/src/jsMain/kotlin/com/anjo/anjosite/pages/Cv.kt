package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.cssRem
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.Modifier
import com.anjo.anjosite.CvLabel
import com.anjo.anjosite.LocalLang

// Foundation-phase placeholder (spec 002-layout-routing, FR-012) — see Projects.kt for rationale.
// File name "Cv.kt" -> route "/cv" (research.md §5: kebab-casing only inserts hyphens at
// lowercase-to-uppercase boundaries, and "Cv" has none after the first letter).
// "CV" is identical in both languages, but still routed through NavHeader.kt's shared `CvLabel`
// (analyze finding F2) for consistency with the other placeholder pages.

@InitRoute
fun initCvPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("CV"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun CvPage() {
    SpanText(CvLabel(LocalLang.current), Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(1.cssRem))
}
