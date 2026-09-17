package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

data class TimelineItem(val date: String, val what: String, val where: String, val stack: String? = null)

// Literal port of docs/handoff/index.html's <div class="tl"> — shared by About's timeline (body
// paragraph content) and CV's experience list (bullet-list content); only the inner content
// differs, so it's a slot rather than two near-duplicate composables.
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
