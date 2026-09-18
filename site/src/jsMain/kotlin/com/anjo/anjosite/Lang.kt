package com.anjo.anjosite

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.browser.window
import com.anjo.anjosite.components.widgets.TimelineItem

enum class Lang {
    EN,
    PL;

    val other: Lang get() = if (this == EN) PL else EN
}

val LocalLang: ProvidableCompositionLocal<Lang> = staticCompositionLocalOf { Lang.EN }

// Paired setter local so the nav's language switch (NavHeader.kt) can change LocalLang without
// AppEntry.kt needing to know about NavHeader — same shape as reading/writing two related locals.
val LocalLangSetter: ProvidableCompositionLocal<(Lang) -> Unit> = staticCompositionLocalOf { {} }

// Browser-language detection (spec 002-layout-routing FR-008, research.md §4): Polish if
// navigator.language starts with "pl" (case-insensitive), else English. Read once at app start;
// not re-evaluated on navigator.language changes (there are none within a single page load).
fun detectInitialLang(): Lang =
    if (window.navigator.language.startsWith("pl", ignoreCase = true)) Lang.PL else Lang.EN

class BilingualString(private val en: String, private val pl: String) {
    operator fun invoke(lang: Lang): String = if (lang == Lang.EN) en else pl
}

// Shared nav-destination labels (analyze finding F2): each is read by both NavHeader.kt (the
// link text) and the corresponding page (its heading), so it's defined once here rather than
// duplicated in both places.
val HomeLabel = BilingualString(en = "Home", pl = "Strona główna")
val AboutLabel = BilingualString(en = "About", pl = "O mnie")
val ProjectsLabel = BilingualString(en = "Projects", pl = "Projekty")
val TrophiesLabel = BilingualString(en = "Trophies", pl = "Trofea")
val ContactLabel = BilingualString(en = "Contact", pl = "Kontakt")
val CvLabel = BilingualString(en = "CV", pl = "CV")

// Shared bilingual timeline shape (specs/004-pages data-model.md, analyze finding C1): used by
// both About.kt and Cv.kt, so it lives here alongside every other cross-cutting bilingual helper
// rather than being duplicated per page.
data class BilingualTimelineItem(val date: BilingualString, val title: BilingualString, val description: BilingualString)

fun BilingualTimelineItem.resolve(lang: Lang) = TimelineItem(date(lang), title(lang), description(lang))
