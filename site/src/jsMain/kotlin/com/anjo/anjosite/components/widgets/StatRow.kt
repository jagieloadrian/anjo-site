package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import com.anjo.anjosite.toSitePalette

data class StatItem(val label: String, val value: String)

val StatLabelStyle = CssStyle.base {
    Modifier
        .fontFamily("JetBrains Mono", "monospace")
        .fontSize(clamp(0.625.cssRem, 1.vw, 0.75.cssRem))
        .letterSpacing(0.1.em)
        .textTransform(TextTransform.Uppercase)
}

val StatValueStyle = CssStyle.base {
    Modifier
        .fontFamily("Archivo", "system-ui", "sans-serif")
        .fontWeight(900)
        .fontSize(clamp(1.25.cssRem, 3.vw, 1.75.cssRem))
}

@Composable
fun StatRow(item: StatItem) {
    val sitePalette = ColorMode.current.toSitePalette()
    Row(Modifier.fillMaxWidth().gap(0.75.cssRem), verticalAlignment = Alignment.CenterVertically) {
        SpanText(item.label, StatLabelStyle.toModifier().color(sitePalette.ink.toRgb().copyf(alpha = 0.7f)))
        SpanText(item.value, StatValueStyle.toModifier().color(sitePalette.pink))
    }
}
