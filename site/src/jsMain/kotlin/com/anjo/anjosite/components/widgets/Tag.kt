package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

enum class TagColor { PLAIN, PINK, CYAN, RED }

// Literal port of docs/handoff/index.html's <span class="tag ...">. The mock's tags are plain,
// non-interactive spans (not links) everywhere they appear (Stack groups, CV skills, project
// cards) — an earlier phase made these clickable Links, which the mock never does.
@Composable
fun Tag(text: String, color: TagColor = TagColor.PLAIN, small: Boolean = false) {
    Span(attrs = {
        classes(buildList {
            add("tag")
            if (small) add("tag--sm")
            when (color) {
                TagColor.PLAIN -> {}
                TagColor.PINK -> add("tag--pink")
                TagColor.CYAN -> add("tag--cyan")
                TagColor.RED -> add("tag--red")
            }
        })
    }) { Text(text) }
}
