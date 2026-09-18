package com.anjo.anjosite.components.widgets

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

enum class TrophyTier { BRONZE, SILVER, GOLD, PLATINUM }

data class TrophyEntry(
    val tier: TrophyTier,
    val name: String,
    val gameName: String,
    val rarityPercent: String,
    val earnedAt: String?,
)

// Literal port of docs/handoff/index.html's <div class="feed-row"> — a plain, non-interactive
// row (the mock never links these). The mock's single example row uses feed-icon--pink just to
// show the "filled in" look; here that's simply "this one has real earned data".
@Composable
fun TrophyRow(trophy: TrophyEntry) {
    Div(attrs = { classes("feed-row") }) {
        Div(attrs = { classes(buildList { add("feed-icon"); if (trophy.earnedAt != null) add("feed-icon--pink") }) })
        Div {
            Div(attrs = { classes("feed-name") }) { Text(trophy.name) }
            Div(attrs = { classes("feed-meta") }) {
                Text("${trophy.gameName} · ${trophy.rarityPercent}% · ${trophy.earnedAt ?: "locked"}")
            }
        }
    }
}
