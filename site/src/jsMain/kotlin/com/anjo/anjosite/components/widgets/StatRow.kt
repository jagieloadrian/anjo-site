package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

enum class StatColor { PLAIN, CYAN, RED, GOLD, SILVER, BRONZE, PLATINUM }

data class StatItem(val label: String, val value: String, val color: StatColor = StatColor.PLAIN)

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
                    StatColor.GOLD -> add("stat-num--gold")
                    StatColor.SILVER -> add("stat-num--silver")
                    StatColor.BRONZE -> add("stat-num--bronze")
                    StatColor.PLATINUM -> add("stat-num--platinum")
                }
            })
        }) { Text(item.value) }
    }
}

@Composable
fun Fact(value: String, label: String? = null, meta: String? = null) {
    Div(attrs = { classes("fact") }) {
        label?.let { Div(attrs = { classes("stat-key") }) { Text(it) } }
        Div(attrs = { classes("fact-val") }) { Text(value) }
        meta?.let { Div(attrs = { classes("meta") }) { Text(it) } }
    }
}
