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
import com.anjo.anjosite.ProjectsLabel

// Foundation-phase placeholder (spec 002-layout-routing, FR-012): route must exist so the nav's
// six-link set is never dead, but real content is Phase 3's job. The heading itself is real
// navigational/category text (not a technical label), so it's bilingual per constitution
// Principle III (analyze finding D1) — unlike Phase 0's Index.kt token-name swatches, which are
// genuinely exempt technical labels. Reuses NavHeader.kt's own `ProjectsLabel` (analyze finding
// F2) rather than defining a second, duplicate `BilingualString` for the same concept.

@InitRoute
fun initProjectsPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Projects"))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ProjectsPage() {
    SpanText(ProjectsLabel(LocalLang.current), Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(1.cssRem))
}
