package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

enum class StatColor { PLAIN, CYAN, RED }

data class StatItem(val label: String, val value: String, val color: StatColor = StatColor.PLAIN)

// Literal port of docs/handoff/index.html's <div class="stat"> — caller wraps a list of these in
// a `<div class="stats">` (bordered) or `<div class="stats stats--bare">` (borderless) container.
@Composable
fun StatCell(item: StatItem, large: Boolean = false) {
    Div(attrs = { classes("stat") }) {
        Div(attrs = { classes("stat-key") }) { Text(item.label) }
        Div(attrs = {
            classes(buildList {
                add("stat-num")
                if (large) add("stat-num--lg")
                when (item.color) {
                    StatColor.PLAIN -> {}
                    StatColor.CYAN -> add("stat-num--cyan")
                    StatColor.RED -> add("stat-num--red")
                }
            })
        }) { Text(item.value) }
    }
}

// Literal port of docs/handoff/index.html's <div class="fact"> — caller wraps a list of these in
// a `<div class="facts">` container. Two shapes the mock uses: a labelled fact (hero: SINCE/
// EMPLOYER/BASE) and a bare one with an optional meta line underneath (About's "off the clock").
@Composable
fun Fact(value: String, label: String? = null, meta: String? = null) {
    Div(attrs = { classes("fact") }) {
        label?.let { Div(attrs = { classes("stat-key") }) { Text(it) } }
        Div(attrs = { classes("fact-val") }) { Text(value) }
        meta?.let { Div(attrs = { classes("meta") }) { Text(it) } }
    }
}
