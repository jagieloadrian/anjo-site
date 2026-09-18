package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

data class GameCoverImage(
    val imageUrl: String?,
    val alt: String,
    val name: String,
    val percentText: String,
    val muted: Boolean = false,
)

@Composable
fun GameCover(cover: GameCoverImage) {
    Div(attrs = { classes("cover") }) {
        if (cover.imageUrl != null) {
            Div(attrs = { classes("cover-img") }) { Img(cover.imageUrl, cover.alt) }
        } else {
            Div(attrs = { classes("cover-img") }) { Text("COVER") }
        }
        Div(attrs = { classes("cover-name") }) { Text(cover.name) }
        Div(attrs = { classes(buildList { add("cover-pct"); if (cover.muted) add("cover-pct--mut") }) }) {
            Text(cover.percentText)
        }
    }
}
