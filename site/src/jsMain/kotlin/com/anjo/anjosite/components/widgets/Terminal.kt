package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.functions.clamp
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.*
import org.w3c.dom.events.Event
import com.anjo.anjosite.toSitePalette

enum class TerminalLineStyle { COMMAND, OUTPUT, ACCENT_PINK, ACCENT_CYAN }

data class TerminalLine(val text: String, val style: TerminalLineStyle)

val TerminalStyle = CssStyle.base {
    Modifier
        .fontFamily("JetBrains Mono", "monospace")
        .fontSize(clamp(0.75.cssRem, 1.5.vw, 0.875.cssRem))
}

private const val REDUCED_MOTION_QUERY = "(prefers-reduced-motion: reduce)"

// Live reduced-motion reactivity (FR-003, research.md §1): CSS media queries alone can gate
// static styles but can't stop an in-flight coroutine, so this listens to matchMedia directly.
@Composable
private fun rememberReducedMotion(): Boolean {
    var reducedMotion by remember { mutableStateOf(window.matchMedia(REDUCED_MOTION_QUERY).matches) }
    DisposableEffect(Unit) {
        val mediaQueryList = window.matchMedia(REDUCED_MOTION_QUERY)
        val listener: (Event) -> Unit = { reducedMotion = mediaQueryList.matches }
        mediaQueryList.addEventListener("change", listener)
        onDispose { mediaQueryList.removeEventListener("change", listener) }
    }
    return reducedMotion
}

@Composable
fun Terminal(lines: List<TerminalLine>) {
    val reducedMotion = rememberReducedMotion()
    var visibleLineCount by remember(lines) { mutableStateOf(0) }
    var visibleCharCount by remember(lines) { mutableStateOf(0) }

    // Mock-ported timings (research.md §5, docs/handoff/app.js): 26ms/char on "$"-prefixed
    // (command) lines, 16ms/char otherwise, 240ms pause between lines.
    LaunchedEffect(lines, reducedMotion) {
        if (reducedMotion) {
            visibleLineCount = lines.size
            visibleCharCount = 0
            return@LaunchedEffect
        }
        visibleLineCount = 0
        visibleCharCount = 0
        for ((index, line) in lines.withIndex()) {
            visibleLineCount = index
            val perCharDelay = if (line.text.startsWith("$")) 26L else 16L
            for (charCount in 1..line.text.length) {
                visibleCharCount = charCount
                delay(perCharDelay)
            }
            delay(240)
        }
        visibleLineCount = lines.size
        visibleCharCount = 0
    }

    val sitePalette = ColorMode.current.toSitePalette()
    Column(TerminalStyle.toModifier()) {
        lines.forEachIndexed { index, line ->
            val color = when (line.style) {
                TerminalLineStyle.COMMAND -> sitePalette.ink
                TerminalLineStyle.OUTPUT -> sitePalette.ink.toRgb().copyf(alpha = 0.75f)
                TerminalLineStyle.ACCENT_PINK -> sitePalette.pink
                TerminalLineStyle.ACCENT_CYAN -> sitePalette.cyan
            }
            when {
                index < visibleLineCount -> SpanText(line.text, Modifier.color(color))
                index == visibleLineCount && !reducedMotion ->
                    SpanText(line.text.take(visibleCharCount), Modifier.color(color))
            }
        }
    }
}
