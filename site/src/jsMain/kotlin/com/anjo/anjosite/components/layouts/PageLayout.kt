package com.anjo.anjosite.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Main
import com.anjo.anjosite.components.sections.Footer
import com.anjo.anjosite.components.sections.NavHeader

class PageLayoutData(val title: String)

// Each page renders its own full-width <section class="band ..."> children directly (mock's
// bands touch the viewport edges with internal grid-gap dividers — a centered max-width column
// wrapper, as this file had before, doesn't match and was never in the mock).
@Composable
@Layout
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val data = ctx.data.getValue<PageLayoutData>()
    LaunchedEffect(data.title) {
        document.title = "Adrian Jagieło — ${data.title}"
    }

    NavHeader()
    Main(attrs = { classes("screen") }) {
        content()
    }
    Footer()
}
