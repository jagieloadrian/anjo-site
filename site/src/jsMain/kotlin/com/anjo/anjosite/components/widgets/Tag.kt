package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

enum class TagColor { PLAIN, PINK, CYAN, RED }

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
