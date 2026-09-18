package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

enum class TrophyTier { BRONZE, SILVER, GOLD, PLATINUM }

data class TrophyEntry(
    val tier: TrophyTier,
    val name: String,
    val gameName: String,
    val rarityPercent: String,
    val earnedAt: String?,
    val iconUrl: String? = null,
)

private val TrophyTier.cssClass: String
    get() = when (this) {
        TrophyTier.BRONZE -> "feed-icon--bronze"
        TrophyTier.SILVER -> "feed-icon--silver"
        TrophyTier.GOLD -> "feed-icon--gold"
        TrophyTier.PLATINUM -> "feed-icon--platinum"
    }

@Composable
fun TrophyRow(trophy: TrophyEntry) {
    Div(attrs = { classes("feed-row") }) {
        Div(attrs = { classes("feed-icon", trophy.tier.cssClass) }) {
            trophy.iconUrl?.let { url -> Img(url, "${trophy.name} icon") }
        }
        Div {
            Div(attrs = { classes("feed-name") }) { Text(trophy.name) }
            Div(attrs = { classes("feed-meta") }) {
                Text("${trophy.gameName} · ${trophy.rarityPercent}% · ${trophy.earnedAt ?: "locked"}")
            }
        }
    }
}
