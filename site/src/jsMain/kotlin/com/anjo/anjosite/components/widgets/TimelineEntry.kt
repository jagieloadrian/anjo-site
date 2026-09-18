package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

data class TimelineItem(val date: String, val what: String, val where: String, val stack: String? = null)

@Composable
fun TimelineEntry(item: TimelineItem, soft: Boolean = false, content: @Composable () -> Unit) {
    Div(attrs = { classes(buildList { add("tl"); if (soft) add("tl--soft") }) }) {
        Div(attrs = { classes("tl-when") }) { Text(item.date) }
        Div(attrs = { classes("tl-what") }) { Text(item.what) }
        Div(attrs = { classes("tl-where") }) { Text(item.where) }
        content()
        item.stack?.let { stack -> Div(attrs = { classes("tl-stack") }) { Text(stack) } }
    }
}
