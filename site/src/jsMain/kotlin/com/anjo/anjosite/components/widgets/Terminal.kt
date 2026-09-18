package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.events.Event

enum class TerminalLineStyle { COMMAND, OUTPUT, ACCENT_PINK, ACCENT_CYAN }

data class TerminalLine(val text: String, val style: TerminalLineStyle)

private fun TerminalLineStyle.className() = when (this) {
    TerminalLineStyle.COMMAND -> "t-cmd"
    TerminalLineStyle.OUTPUT -> "t-out"
    TerminalLineStyle.ACCENT_PINK -> "t-pink"
    TerminalLineStyle.ACCENT_CYAN -> "t-cyan"
}

private const val REDUCED_MOTION_QUERY = "(prefers-reduced-motion: reduce)"

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
fun Terminal(lines: List<TerminalLine>, title: String = "adrian@d18: ~/boot") {
    val reducedMotion = rememberReducedMotion()
    var visibleLineCount by remember(lines) { mutableStateOf(0) }
    var visibleCharCount by remember(lines) { mutableStateOf(0) }

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

    Div(attrs = { classes("term") }) {
        Div(attrs = { classes("term-bar") }) {
            Span { Text(title) }
            Span(attrs = { classes("term-dots") }) { I {}; I {}; I {} }
        }
        Div(attrs = { classes("term-body") }) {
            lines.forEachIndexed { index, line ->
                when {
                    index < visibleLineCount -> P(attrs = { classes(line.style.className()) }) { Text(line.text) }
                    index == visibleLineCount && !reducedMotion ->
                        P(attrs = { classes(line.style.className()) }) { Text(line.text.take(visibleCharCount)) }
                }
            }
            Span(attrs = { classes("caret") })
        }
    }
}
