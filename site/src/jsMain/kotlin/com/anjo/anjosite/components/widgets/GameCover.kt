package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.style.CssRule
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Img
import com.anjo.anjosite.toSitePalette

data class GameCoverImage(val imageUrl: String?, val alt: String, val href: String)

// Independent of TrophyRow (clarification: separate, standalone components). Same
// clickable/placeholder pattern as ProjectCard (research.md §3/§4).
val GameCoverStyle = CssStyle {
    base {
        Modifier.width(8.cssRem).height(10.cssRem).borderRadius(0.5.cssRem)
    }
    (CssRule.OfMedia(CSSMediaQuery.MediaFeature("max-width", 720.px))) {
        Modifier.minHeight(48.px)
    }
}

@Composable
fun GameCover(cover: GameCoverImage) {
    val sitePalette = ColorMode.current.toSitePalette()
    Link(
        cover.href,
        modifier = Modifier.display(DisplayStyle.InlineBlock),
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)
    ) {
        if (cover.imageUrl != null) {
            Img(cover.imageUrl, cover.alt, attrs = GameCoverStyle.toModifier().toAttrs())
        } else {
            Box(GameCoverStyle.toModifier().backgroundColor(sitePalette.nearBackground))
        }
    }
}
