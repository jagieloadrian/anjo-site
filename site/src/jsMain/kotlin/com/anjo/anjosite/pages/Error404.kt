package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import com.anjo.anjosite.components.layouts.PageLayoutData
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

private const val NotFoundMessage = "Page not found"
private const val Description = "Page not found"

@InitRoute
fun init404Page(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("404", Description))
}

@Page("/404")
@Layout(".components.layouts.PageLayout")
@Composable
fun Error404Page() {
    SpanText("404", Modifier.fontFamily("JetBrains Mono", "monospace").fontSize(2.cssRem))
    SpanText(NotFoundMessage, Modifier.fontFamily("Archivo", "system-ui", "sans-serif").fontSize(1.cssRem))
}
