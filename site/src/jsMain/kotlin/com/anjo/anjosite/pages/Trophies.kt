package com.anjo.anjosite.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vw
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.Lang
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.components.layouts.PageLayoutData
import com.anjo.anjosite.components.widgets.GameCover
import com.anjo.anjosite.components.widgets.GameCoverImage
import com.anjo.anjosite.components.widgets.StatItem
import com.anjo.anjosite.components.widgets.StatRow
import com.anjo.anjosite.components.widgets.TrophyEntry
import com.anjo.anjosite.components.widgets.TrophyRow
import com.anjo.anjosite.components.widgets.TrophyTier

// Real Trophies content (specs/004-pages FR-007/FR-008/FR-009/FR-016): fetches a hand-authored
// static trophies.json placeholder at runtime — no kotlinx.serialization dependency (research.md
// §3/§4), manual JSON.parse<dynamic> field mapping instead.

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

// Page-owned bilingual stat labels (analyze finding C2) — trophies.json supplies only the fetched
// value per stable key; game/trophy names stay untranslated proper nouns (research.md §8).
private val trophiesStatLabels: Map<String, BilingualString> = mapOf(
    "level" to BilingualString(en = "PSN Level", pl = "Poziom PSN"),
    "platinums" to BilingualString(en = "Platinums", pl = "Platyny"),
    "games" to BilingualString(en = "Games", pl = "Gry"),
    "completion" to BilingualString(en = "Completion", pl = "Ukończenie"),
)

private fun TrophiesStat.toStatItem(lang: Lang) = StatItem(
    label = trophiesStatLabels[key]?.invoke(lang) ?: key,
    value = value,
)

private fun parseTrophiesData(text: String): TrophiesData {
    val json = kotlin.js.JSON.parse<dynamic>(text)

    val stats = (json.stats as Array<dynamic>).map { TrophiesStat(it.key as String, it.value as String) }
    val games = (json.games as Array<dynamic>).map {
        GameCoverImage(it.imageUrl as String?, it.alt as String, it.href as String)
    }
    val trophies = (json.trophies as Array<dynamic>).map {
        TrophyEntry(
            tier = TrophyTier.valueOf(it.tier as String),
            name = it.name as String,
            earnedAt = it.earnedAt as String?,
            href = it.href as String,
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

    when (val state = fetchState) {
        is TrophiesFetchState.Loading -> {
            // Skeleton loading state (FR-016): reuse each component's existing placeholder mode
            // (null cover image) rather than a blank content area.
            Column(Modifier.gap(2.cssRem)) {
                SpanText(LoadingLabel(lang), Modifier.fontSize(1.cssRem))
                Row(Modifier.gap(1.cssRem)) {
                    repeat(4) { GameCover(GameCoverImage(null, "", "/trophies")) }
                }
            }
        }
        is TrophiesFetchState.Failed -> {
            SpanText(ErrorLabel(lang), Modifier.fontSize(1.cssRem))
        }
        is TrophiesFetchState.Loaded -> {
            Column(Modifier.gap(2.cssRem)) {
                Row(Modifier.gap(2.cssRem).flexWrap(FlexWrap.Wrap)) {
                    state.data.stats.forEach { stat -> StatRow(stat.toStatItem(lang)) }
                }
                Row(Modifier.gap(1.cssRem).flexWrap(FlexWrap.Wrap)) {
                    state.data.games.forEach { game -> GameCover(game) }
                }
                Column(Modifier.gap(0.75.cssRem)) {
                    state.data.trophies.forEach { trophy -> TrophyRow(trophy) }
                }
            }
        }
    }
}
