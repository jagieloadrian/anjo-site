package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Em
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.Fact
import com.anjo.anjosite.components.widgets.GameCover
import com.anjo.anjosite.components.widgets.GameCoverImage
import com.anjo.anjosite.components.widgets.StatCell
import com.anjo.anjosite.components.widgets.StatColor
import com.anjo.anjosite.components.widgets.StatItem
import com.anjo.anjosite.components.widgets.TrophyEntry
import com.anjo.anjosite.components.widgets.TrophyRow
import com.anjo.anjosite.components.widgets.TrophyTier

// Literal port of docs/handoff/index.html's data-screen="trophies" — fetches a hand-authored
// static trophies.json placeholder at runtime — no kotlinx.serialization dependency
// (research.md §3/§4), manual JSON.parse<dynamic> field mapping instead.

data class TrophiesStat(val key: String, val value: String)
data class TrophiesData(
    val stats: List<TrophiesStat>,
    val games: List<GameCoverImage>,
    val trophies: List<TrophyEntry>,
)

sealed interface TrophiesFetchState {
    data object Loading : TrophiesFetchState
    data class Loaded(val data: TrophiesData) : TrophiesFetchState
    data object Failed : TrophiesFetchState
}

private val statLabels = mapOf(
    "level" to "PSN LEVEL", "platinums" to "PLATINUMS", "games" to "GAMES", "completion" to "COMPLETION",
)
private val statColors = mapOf("games" to StatColor.CYAN, "completion" to StatColor.RED)

private fun parseTrophiesData(text: String): TrophiesData {
    val json = kotlin.js.JSON.parse<dynamic>(text)

    val stats = (json.stats as Array<dynamic>).map { TrophiesStat(it.key as String, it.value as String) }
    val games = (json.games as Array<dynamic>).map {
        GameCoverImage(
            imageUrl = it.imageUrl as String?,
            alt = it.alt as String,
            name = it.name as String,
            percentText = it.percentText as String,
            muted = it.muted as Boolean,
        )
    }
    val trophies = (json.trophies as Array<dynamic>).map {
        TrophyEntry(
            tier = TrophyTier.valueOf(it.tier as String),
            name = it.name as String,
            gameName = it.gameName as String,
            rarityPercent = it.rarityPercent as String,
            earnedAt = it.earnedAt as String?,
        )
    }
    return TrophiesData(stats, games, trophies)
}

@InitRoute
fun initTrophiesPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Trophies"))
}

private val LoadingLabel = BilingualString(en = "Loading trophies…", pl = "Wczytywanie trofeów…")
private val ErrorLabel = BilingualString(
    en = "Trophies couldn't be loaded right now.",
    pl = "Nie udało się teraz wczytać trofeów.",
)

private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun TrophiesPage() {
    val lang = LocalLang.current
    var fetchState by remember { mutableStateOf<TrophiesFetchState>(TrophiesFetchState.Loading) }

    LaunchedEffect(Unit) {
        fetchState = try {
            val response = window.fetch("/trophies.json").await()
            if (!response.ok) throw Exception("HTTP ${response.status}")
            TrophiesFetchState.Loaded(parseTrophiesData(response.text().await()))
        } catch (t: Throwable) {
            TrophiesFetchState.Failed
        }
    }

    Section(attrs = { classes("band", "band--strong", "band--pad") }) {
        Div(attrs = { classes("kicker"); style { property("margin-bottom", "18px") } }) { Text("04 / TROPHIES · PSN SIRDIETHER18") }
        H1(attrs = { classes("display") }) {
            Text("TROPHY")
            Br()
            Em(attrs = { classes("cyan") }) { Text("CABINET") }
        }
    }

    when (val state = fetchState) {
        is TrophiesFetchState.Loading -> {
            Section(attrs = { classes("band", "band--pad") }) {
                P(attrs = { classes("body") }) { Text(LoadingLabel(lang)) }
            }
        }
        is TrophiesFetchState.Failed -> {
            Section(attrs = { classes("band", "band--pad") }) {
                P(attrs = { classes("body") }) { Text(ErrorLabel(lang)) }
            }
        }
        is TrophiesFetchState.Loaded -> {
            Section(attrs = {
                classes("band", "stats", "stats--bare")
                style { property("grid-template-columns", "repeat(auto-fit, minmax(160px, 1fr))") }
            }) {
                state.data.stats.forEach { stat ->
                    StatCell(StatItem(statLabels[stat.key] ?: stat.key, stat.value, statColors[stat.key] ?: StatColor.PLAIN), large = true)
                }
                Fact(label = "UPDATED", value = "trophies.json")
            }
            Section(attrs = { classes("band", "band--strong", "split") }) {
                Div {
                    Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "18px") } }) { Text("GAMES") }
                    Div(attrs = { classes("covers") }) {
                        state.data.games.forEach { game -> GameCover(game) }
                    }
                    P(attrs = { classes("meta"); style { property("margin", "14px 0 0") } }) {
                        Text("titles real, numbers and covers pending — covers come from the PSN API response")
                    }
                }
                Div {
                    Div(attrs = { classes("label", "label--sm"); style { property("margin-bottom", "18px") } }) {
                        Text("RECENT TROPHIES · FEED SHAPE")
                    }
                    Div(attrs = { classes("feed") }) {
                        state.data.trophies.forEach { trophy -> TrophyRow(trophy) }
                    }
                    Div(attrs = { classes("slot"); style { property("margin-top", "24px"); property("padding", "20px") } }) {
                        Div(attrs = { classes("slot-key") }) { Text("OPTIONAL · PSNPROFILES SIGNATURE") }
                        P { Text("Slot for the PSNProfiles signature banner (an image they generate and keep current).") }
                    }
                    Link(
                        "https://psnprofiles.com/Sirdiether18",
                        "full profile ↗",
                        Modifier.classNames("btn", "btn--sm", "btn--outline")
                            .styleModifier { property("margin-top", "20px") },
                        variant = navLinkVariant,
                    )
                }
            }
        }
    }
}
