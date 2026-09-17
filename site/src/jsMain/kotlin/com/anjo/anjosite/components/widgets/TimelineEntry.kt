package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import com.anjo.anjosite.toSitePalette

data class TimelineItem(val date: String, val title: String, val description: String)

val TimelineDateStyle = CssStyle.base {
    Modifier
        .fontFamily("JetBrains Mono", "monospace")
        .fontSize(clamp(0.625.cssRem, 1.vw, 0.75.cssRem))
        .letterSpacing(0.1.em)
        .textTransform(TextTransform.Uppercase)
}

val TimelineTitleStyle = CssStyle.base {
    Modifier
        .fontFamily("Archivo", "system-ui", "sans-serif")
        .fontWeight(700)
        .fontSize(clamp(1.cssRem, 2.vw, 1.25.cssRem))
}

val TimelineDescriptionStyle = CssStyle.base {
    Modifier.fontSize(clamp(0.875.cssRem, 1.5.vw, 1.cssRem))
}

@Composable
fun TimelineEntry(item: TimelineItem) {
    val sitePalette = ColorMode.current.toSitePalette()
    Column(Modifier.fillMaxWidth().gap(0.375.cssRem)) {
        SpanText(item.date, TimelineDateStyle.toModifier().color(sitePalette.cyan))
        SpanText(item.title, TimelineTitleStyle.toModifier())
        SpanText(item.description, TimelineDescriptionStyle.toModifier().color(sitePalette.ink.toRgb().copyf(alpha = 0.8f)))
    }
}
