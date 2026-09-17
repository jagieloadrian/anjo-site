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
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.TrophiesLabel

// Foundation-phase placeholder (spec 002-layout-routing, FR-012) — see Projects.kt for rationale
// (heading is bilingual per analyze finding D1, reuses NavHeader.kt's `TrophiesLabel` per F2).

@InitRoute
fun initTrophiesPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Trophies"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun TrophiesPage() {
    SpanText(TrophiesLabel(LocalLang.current), Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(1.cssRem))
}
