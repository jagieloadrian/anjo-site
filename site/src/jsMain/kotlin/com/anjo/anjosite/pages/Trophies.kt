package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.anjo.anjosite.FetchFailedException
import com.anjo.anjosite.Log
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
    "total" to "TOTAL", "gold" to "GOLD", "silver" to "SILVER", "bronze" to "BRONZE",
)
private val statColors = mapOf(
    "games" to StatColor.CYAN,
    "completion" to StatColor.RED,
    "platinums" to StatColor.PLATINUM,
    "gold" to StatColor.GOLD,
    "silver" to StatColor.SILVER,
    "bronze" to StatColor.BRONZE,
)

internal fun parseTrophiesData(text: String): TrophiesData {
    val json = JSON.parse<dynamic>(text)

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
            iconUrl = it.iconUrl as String?,
        )
    }
    return TrophiesData(stats, games, trophies)
}

private const val Description = "PlayStation trophy collection and gaming history, tracked by Adrian Jagieło."

@InitRoute
fun initTrophiesPage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData("Trophies", Description))
}

private const val LoadingLabel = "Loading trophies…"
private const val ErrorLabel = "Trophies couldn't be loaded right now."

private val navLinkVariant = UndecoratedLinkVariant.then(UncoloredLinkVariant)

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun TrophiesPage() {
    var fetchState by remember { mutableStateOf<TrophiesFetchState>(TrophiesFetchState.Loading) }

    LaunchedEffect(Unit) {
        fetchState = try {
            val response = window.fetch("/trophies.json").await()
            if (!response.ok) throw FetchFailedException("/trophies.json", response.status.toInt())
            val data = parseTrophiesData(response.text().await())
            Log.info("Trophies", "loaded ${data.trophies.size} trophies, ${data.games.size} games")
            TrophiesFetchState.Loaded(data)
        } catch (t: Throwable) {
            Log.error("Trophies", "failed to load /trophies.json", t)
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
                P(attrs = { classes("body") }) { Text(LoadingLabel) }
            }
        }
        is TrophiesFetchState.Failed -> {
            Section(attrs = { classes("band", "band--pad") }) {
                P(attrs = { classes("body") }) { Text(ErrorLabel) }
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
