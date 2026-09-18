# Podsumowanie specyfikacji (zastępuje specs/001-004, usunięte po konsolidacji)

Ten plik zastępuje cztery katalogi spec-kit (001-foundation-setup, 002-layout-routing,
003-components, 004-pages — po 7 plików każdy: spec/plan/research/data-model/quickstart/tasks/
checklists), które zostały usunięte po konsolidacji. Wszystkie cztery fazy są ukończone (status
zgodny z ROADMAP.md). Poniżej tylko decyzje o trwałej wartości dla przyszłych faz — bez
checklist zadań, bez boilerplate'u spec-kit, bez mechaniki sesji clarification.

---

## Faza 001 — Foundation Setup

**Cel**: Doprowadzić projekt Kobweb (`site/`) do wizualnego stanu zgodnego z mockiem
(`docs/handoff/`) — design tokens, typografia, globalny reset, trzy warstwy dekoracyjne (scanline/
vignette/grid) — zanim powstaną jakiekolwiek realne strony czy komponenty.

**Kluczowe decyzje techniczne**

- **Tryb publikacji GitHub Pages — WAŻNE dla CI/deploy**: strona jest publikowana jako
  **user/organization root page** (`jagieloadrian.github.io`), NIE jako project page pod
  `/anjo-site/`. `site.basePath` w `.kobweb/conf.yaml` pozostaje **nieustawiony** (domyślne `""` =
  root) — świadomie, bo to jedyna poprawna wartość dla root page; jawne wpisanie `"/"` lub
  `"/anjo-site"` byłoby błędem. Eksport statyczny tego repo (`anjo-site`) **nie jest** sam w sobie
  źródłem Pages — jego output ma trafiać do osobnego repo `jagieloadrian.github.io` (mechanizm
  publikacji, np. cross-repo CI push, jest kwestią planistyczną, nierozstrzygniętą na poziomie
  specyfikacji — to zadanie dla przyszłej fazy CI/deploy).
- **Design tokens**: pięć kolorów mocka (`--bg`, `--ink`, `--pink`, `--cyan`, `--red`) przeniesione
  do `SiteTheme.kt` jako `SitePalette`/`SitePalettes`, identyczne dla `light` i `dark` (mock ma
  tylko jeden motyw — nie usunięto infrastruktury Kobweb ColorMode, tylko wskazano oba sloty na te
  same wartości). **Silk ma własną, wbudowaną paletę** (`ctx.theme.palettes.light/dark`,
  ustawianą w `@InitSilk fun initTheme`) odrębną od projektowego `SitePalette` — to ona faktycznie
  steruje tłem/tekstem strony przez `Surface(SmoothColorStyle...)`. `initTheme` musi czytać wartości
  z `SitePalettes`, inaczej strona renderuje się z domyślną biało-czarną paletą Silka mimo
  poprawnego `SitePalette`.
- **Fonty**: Archivo (400-900) + JetBrains Mono (400/500/700) ładowane z Google Fonts przez
  `kobweb.app.index.head` w `build.gradle.kts` (bez nowej zależności Gradle). Self-hosting
  fontów odłożony jako opcjonalna przyszła praca.
- **Reduced motion**: wzorzec `CSSMediaQuery.MediaFeature("prefers-reduced-motion", "reduce")` w
  `AppStyles.kt` — ukrywa tylko `.fx-scan` (scanline); vignette i grid są statyczne i renderują się
  zawsze.
- Zero nowych zależności Gradle (constitution Principle VIII) — cały ten wzorzec (fonty przez
  `head` DSL, motion przez media query, i18n przez CompositionLocal w kolejnych fazach) opiera się
  wyłącznie na już obecnych zależnościach.

**Zaimplementowane pliki**: `SiteTheme.kt` (tokeny + `initTheme`), `AppStyles.kt` (globalny reset +
3 warstwy overlay + reduced-motion rule), `AppEntry.kt` (renderowanie overlayów na poziomie
app-shell), `pages/Index.kt` (placeholder na nowym fundamencie), `components/layouts/PageLayout.kt`
(usunięto demo `SvgCobweb` z szablonu Kobweb — odkryte podczas implementacji, inaczej zostawiało
domyślny wygląd Kobweb widoczny na każdej stronie).

**Gotchas**: Placeholder Index musi być wolny od elementów interaktywnych (brak zweryfikowanego
touch-target 48px w tej fazie) — usunięto boilerplate `Button`/CTA zamiast go przeskalować.
Teksty na placeholderze to wyłącznie techniczne etykiety (nazwy tokenów/fontów) — zwolnione z
wymogu dwujęzyczności (Principle III), w przeciwieństwie do realnej prozy.

---

## Faza 002 — Layout and Routing

**Cel**: Zastąpić ręczny router mocka (`.hidden`-toggling, `app.js`) prawdziwym file-based routingiem
Kobweb, rozbudować nawigację do wszystkich sześciu miejsc docelowych mocka, wprowadzić mechanizm
przełączania języka i wygenerować on-brand `404.html`.

**Kluczowe decyzje techniczne**

- **Routing**: wyłącznie natywny `@Page`-based routing Kobweb (żaden custom router w Kotlinie).
  Nazwa pliku → route (PascalCase → kebab-case, `Index` → root katalogu). Dla eksportu statycznego
  route kończący się `/` → `index.html`, inne → `route + ".html"`.
- **404**: `pages/Error404.kt` z `@Page("/404")` — eksportuje się do `404.html` w root, dokładnie
  tam gdzie GitHub Pages go szuka. Brak dedykowanego mechanizmu "error page" w Kobweb — to zwykła
  strona z `routeOverride`.
- **i18n / Language State**: `staticCompositionLocalOf<Lang>` (`LocalLang`) + osobny
  `LocalLangSetter: CompositionLocal<(Lang) -> Unit>`, oba dostarczane raz, na poziomie
  `AppEntry.kt` — analogicznie do istniejącego wzorca `ColorMode.current` z Silka, ale osobna
  instancja. Wartość początkowa: `Lang.PL` jeśli `window.navigator.language` zaczyna się od `"pl"`,
  inaczej `Lang.EN`. Stan **wyłącznie w pamięci** (brak `localStorage`) — trwałość między stronami
  świadomie odłożona do fazy z realną treścią (Phase 3/004-pages).
  `BilingualString` (para `en`/`pl` + selekcja przez `LocalLang.current`) to wzorzec danych
  bilingual używany dalej w całym projekcie (Phase 3/4).
- **Placeholder routes**: `Projects.kt`, `Trophies.kt`, `Contact.kt`, `Cv.kt` — cztery nowe route'y
  z etykietami technicznymi (żaden dead link w nawigacji), zastąpione realną treścią w fazie 004.
  `Cv.kt` → `/cv` (nie `/c-v`, bo kebab-case nie wstawia myślnika przy "Cv").
- Panel design-notes z mocka **całkowicie pominięty** w porcie (nie ukryty flagą — po prostu nigdy
  nie przeniesiony do Kotlina).

**Zaimplementowane pliki**: `Lang.kt` (nowy — `Lang`, `LocalLang`, `LocalLangSetter`,
`BilingualString`, detekcja języka przeglądarki), `AppEntry.kt` (provider dla `LocalLang`),
`components/sections/NavHeader.kt` (marka z hover-glitch `Keyframes`, sześć linków, przełącznik
języka), `pages/Projects.kt`, `Trophies.kt`, `Contact.kt`, `Cv.kt`, `Error404.kt` (nowe).

**Gotchas**: Glitch hover marki jest wygaszany pod `prefers-reduced-motion: reduce` przez
kompozycję `CssRule.OfMedia(mediaQuery) + hover` (Silk `CssStyleScope`), a nie osobną regułę CSS —
wzorzec do powielenia przy podobnych hover-animacjach. Świadomie pominięto warstwę `::after`
cyan-ghost duplicate-text z mocka (Compose HTML nie ma `content: attr(...)` modifiera) — sam wobble
pozycji wystarcza jako "glitch".

---

## Faza 003 — Shared UI Components

**Cel**: Zbudować siedem współdzielonych komponentów Silk (`Terminal`, `ProjectCard`,
`TimelineEntry`, `StatRow`, `GameCover`, `TrophyRow`, `Tag`), z których faza 004 składa strony —
czysto prezentacyjne, bez pobierania danych, treść wyłącznie przez parametry/data class.

**Kluczowe decyzje techniczne**

- **Klikalność całych komponentów**: `ProjectCard`, `GameCover`, `TrophyRow`, `Tag` są zawsze
  całościowo klikalne — całość owinięta w Silk `Link` (nie `Div` + `onClick` + ręczne
  `role="link"`), bo `<a>` daje natywną fokusowalność/dostępność za darmo. Każdy wymaga
  `href` (nie ma opcjonalnego trybu non-interactive). To ustala wzorzec: żaden przyszły komponent
  nawigacyjny nie powinien wynajdywać ręcznego ARIA na `Div`.
  `GameCover` i `TrophyRow` są **niezależne** — żadnej relacji zagnieżdżenia; strona komponuje je
  sama.
- **Placeholder obrazków**: parametr URL jako `String?` — `null` renderuje CSS-owy placeholder
  (bez `<img>`), brak obsługi błędu sieciowego (broken image) — to świadomie poza zakresem.
- **Dostępność jako baseline, nie dodatek**: wymagany `alt` na każdym komponencie z obrazkiem
  (wymuszone przez typy w czasie kompilacji — non-nullable pola konstruktora, nie runtime check),
  ARIA role/etykiety na każdym elemencie interaktywnym — dostarczone w tej fazie, nie odłożone do
  późniejszego audytu a11y.
- **720px breakpoint**: surowy `CssRule.OfMedia(CSSMediaQuery.MediaFeature("max-width", 720.px))`
  zamiast wbudowanego `Breakpoint` Silka (najbliższe kroki 640/768px nie trafiają dokładnie w
  wymóg mocka) — wzorzec do powtórzenia przy kolejnych komponentach z niestandardowym breakpointem.
- **Reduced-motion z żywą reaktywnością** (`Terminal`): statyczne media queries CSS wystarczają do
  wyłączenia stylu, ale nie zatrzymają trwającej korutyny Kotlin — `Terminal` jako pierwszy
  komponent czyta `window.matchMedia(...)` + listener `"change"` po stronie JS, trzymany w
  `mutableStateOf`, kluczujący `LaunchedEffect`. To jedyne miejsce w projekcie z tym wzorcem;
  przyszłe animowane komponenty oparte o stan Kotlin (nie czysty CSS) powinny go powielić.
- Wszystkie tokeny kolorów/fontów/spacingu wyłącznie z `SiteTheme.kt` — żadnych magic values.

**Zaimplementowane pliki**: `components/widgets/Tag.kt`, `StatRow.kt`, `TimelineEntry.kt`,
`ProjectCard.kt`, `GameCover.kt`, `TrophyRow.kt`, `Terminal.kt` — wszystkie nowe, płaskie pliki w
istniejącym pakiecie `components/widgets/` (bez nowej taksonomii pakietów).

**Gotchas**: Weryfikacja wizualna każdego komponentu celowo odłożona do fazy 004 (brak scratch-page
do samego podglądu) — komponenty są kompilowalne i typowo poprawne, ale realnie renderowane
dopiero gdy strona je faktycznie użyje.

---

## Faza 004 — Pages

**Cel**: Zastąpić wszystkie placeholder route'y realną treścią z mocka, złożoną z siedmiu
komponentów z fazy 003 + mechanizmów język/motyw z faz 001/002. Dodać brakujący route `/about`.

**Kluczowe decyzje techniczne — data-sourcing (ważne dla przyszłych faz)**

- **Wzorzec bilingual dla list in-source**: komponenty z fazy 003 przyjmują wyłącznie gotowe
  `String` (bez logiki tłumaczenia). Każda nowa lista in-source (Projects, About, CV) przechowuje
  treść jako `BilingualString`/`BilingualTimelineItem`, rozwiązywaną do zwykłego `String` **w
  momencie renderu** przez mapper `toXxx(lang)`/`resolve(lang)`, kluczowany `LocalLang.current`.
  `BilingualTimelineItem` + `resolve(lang)` żyje w `Lang.kt` (współdzielone między `About.kt` i
  `Cv.kt`).
- **Trzy różne źródła danych, celowo nie ujednolicone**:
  - **Projects** (`projects.json`) i **Home Stack** (`stack.json`) — statyczne, ręcznie
    napisane pliki JSON w `resources/public/`, pobierane w runtime przez `window.fetch` +
    `JSON.parse<dynamic>(...)` (bez `kotlinx.serialization` — nie jest zależnością projektu).
    *Uwaga*: to odejście od pierwotnego planu fazy (który zakładał listę in-source dla Projects) —
    świadomie rozszerzone w trakcie implementacji (`/speckit-converge`), żeby użyć tego samego
    wzorca co Trophies.
  - **Trophies** (`trophies.json`) — analogiczny fetch, ale to jedyne źródło z pełnym stanem
    loading/error/success (`TrophiesFetchState`: `Loading`/`Loaded`/`Failed`), bo to jedyny content
    zależny od zewnętrznej automatyzacji (nocny PSN job — **poza zakresem tej fazy**, dopiero
    ROADMAP F025). Nazwy gier/trofeów to proper nouns z automatyzacji PSN — renderowane bez
    tłumaczenia w obu językach (świadomy wyjątek od FR-013, równoległy do wyjątku "etykiety
    techniczne" z Principle III); tylko etykiety statystyk (`trophiesStatLabels`) są bilingual.
  - **About / CV** — listy in-source (`List<data class>` literal w Kotlinie), każda ma **własny,
    celowo osobny kształt danych** (`AboutContent` vs `CvContent`) mimo częściowego pokrywania się
    faktów (historia kariery) — CV grupuje wpisy pod nagłówkami sekcji, About nie.
- **Dynamiczny routing projektów**: `/projects/{slug}` przez `@Page("{}")` w
  `pages/projects/Slug.kt` + `rememberPageContext().route.params["slug"]`. **Krytyczne dla static
  exportu**: Kobweb eksportuje wyłącznie route'y bez `{` — dynamiczny segment trzeba dodatkowo
  zarejestrować per-slug przez `kobweb.app.export.addExtraRoute(...)` w `build.gradle.kts` (publiczne
  API; samo `extraRoutes` jest `internal`), inaczej `kobweb export` **cicho pomija** te strony i
  udostępniony link 404-uje na GitHub Pages. To ogólny wzorzec dla każdej przyszłej dynamicznej
  trasy w tym projekcie. Nieznany slug → `router.navigateTo("/404")` (reużycie 404 z fazy 002,
  bez nowego mechanizmu not-found).
- **Kontakt = mailto-only, świadomie minimalne**: jedno pole tekstowe, stały temat, stały
  odbiorca — zbudowany jako `mailto:` link po stronie klienta (`ContactPrompt`, zbudowany z natywnych
  `TextInput`/`Button` Silka), zero requestów sieciowych. Usługa formularzy trzeciej strony
  (Formspree itp.) pozostaje odłożona jako opcjonalne wzbogacenie na przyszłość (YAGNI,
  Principle VIII).
- **Print CSS**: `mediaPrint` Silka (`@media print`) ukrywa nav/footer **na całej witrynie**, nie
  tylko na `/cv` — dwie jednolinijkowe reguły w `NavHeaderStyle`/`FooterStyle`.
- **Dwa nowe współdzielone komponenty** (wyjątek od reguły "tylko 7 komponentów z fazy 003"):
  `ContactPrompt` (input/send dla kontaktu) i `LinkCell` (link-row używany przez Home i Contact,
  port `<a class="link-cell">` z mocka) — oba w `components/widgets/`.
- Poza zakresem FR (dodane ad hoc w trakcie implementacji, udokumentowane retrospektywnie):
  przełącznik jasny/ciemny motyw (`Theme.kt`) i hover-glitch marki w `NavHeader.kt`.

**Zaimplementowane pliki**: `pages/Index.kt` (realny Home + fetch `stack.json`), `pages/About.kt`
(nowy route), `pages/Projects.kt` (grid z `projects.json`), `pages/projects/Slug.kt` (nowy,
dynamiczny detail), `pages/Trophies.kt` (fetch + trzy stany render), `pages/Contact.kt`,
`pages/Cv.kt`, `components/widgets/ContactPrompt.kt` (nowy), `components/widgets/LinkCell.kt`
(nowy), `Lang.kt` (+ `BilingualTimelineItem`/`resolve`), `Theme.kt` (nowy, poza FR),
`build.gradle.kts` (+ `addExtraRoute` per slug), `resources/public/projects.json`, `stack.json`,
`trophies.json` (nowe, statyczne placeholdery).

**Gotchas**: Static export musi zawierać osobny plik HTML dla każdego route'u **i** dla każdego
slugu projektu (SC-006) — sprawdzać po każdej zmianie listy projektów, że `extraRoutes` w
`build.gradle.kts` jest zsynchronizowane z listą slugów (ręczna synchronizacja, akceptowalna przy
obecnej skali kilku projektów; przy większej skali warto wygenerować listę z jednego wspólnego
źródła). Tag na widoku detali projektu zawsze linkuje z powrotem do `/projects` (brak
filtrowania po tagach — nie zbudowane w tej fazie). Wszystkie sześć route'ów wymaga ręcznej
weryfikacji na 390/430/768px (`docs/handoff/mobile-check.html`) — brak automatycznych testów w
projekcie na żadnym etapie (świadoma decyzja, konsekwentna przez wszystkie cztery fazy).

---

## Faza 007 — Polish + Automated Test Suite

**Cel**: Domknąć Fazę 6 ROADMAP.md (F030-F033: SEO/OG, pełny a11y pass, decyzja analytics/fontów)
i wprowadzić pierwszy automatyczny zestaw testów wykraczający poza `scripts/refresh-trophies`:
`kotlin.test` (jsTest) dla czystej logiki oraz Playwright dla realnego eksportu statycznego,
podpięte do CI.

**Kluczowe decyzje techniczne**

- **SEO/OG jako rozszerzenie istniejącego `PageLayout.kt`, nie nowy mechanizm**: zweryfikowano
  empirycznie, że `kobwebExport --layout static` robi realny, przeglądarkowy snapshot DOM **po**
  wykonaniu klienckiego JS/Compose — więc `LaunchedEffect`-owe mutacje `<head>` (title, meta,
  OG tagi) trafiają do finalnego wyeksportowanego HTML. `updatePageMeta(title, description,
  ogImage)` to jedna współdzielona funkcja w `PageLayout.kt`, wywoływana raz statycznie (z
  `PageLayoutData`) i — dla `/projects/{slug}`, jedynej trasy z realnie dynamiczną treścią — drugi
  raz z `Slug.kt`'s `LaunchedEffect(project)` po rozwiązaniu fetcha `projects.json`.
- **Self-hosted fonty (F033, implement-now)**: Archivo i JetBrains Mono są fontami zmiennymi po
  stronie Google — te same bajty pliku są serwowane dla każdej deklarowanej wagi w ramach jednego
  subsetu (`latin`/`latin-ext`), więc zawodowane są tylko 4 unikalne pliki `.woff2`
  (`resources/public/fonts/`), z 18 regułami `@font-face` (6 wag × 2 subsety dla Archivo, 3 × 2
  dla JetBrains Mono) wskazującymi na nie. Oba subsety (`latin` **i** `latin-ext`) są wymagane —
  polskie znaki diakrytyczne (ą/ę/ł/ń/ó/ś/ź/ż) leżą w bloku Unicode Latin Extended-A, poza
  `latin`-only. Reguły `@font-face` żyją jako surowy `<style>` wstrzyknięty przez `head.add` w
  `build.gradle.kts`, nie w `SiteTokenStyles.kt` — typowany DSL `StyleSheet()` Compose HTML nie
  wspiera `@font-face`. Analytics (F032) — odłożone (YAGNI, research.md §10).
- **Kolejność konkatenacji arkuszy stylów ma znaczenie**: `SiteStyles.kt` łączy kilkanaście
  osobnych `object : StyleSheet()` w jedną listę `cssRules` — przy równej specyficzności
  selektora (np. `.caret { animation: ... }` w `SiteGlitchStyles.kt` vs `@media
  (prefers-reduced-motion: reduce) { .caret { animation: none } }` w `SiteOverlayStyles.kt`)
  wygrywa reguła **później** w tej liście, niezależnie od warunku media query. Bug znaleziony
  dopiero przez realny test Playwright z `reducedMotion: "reduce"` — wcześniejsze audyty czysto
  wzrokowe/manualne go nie złapały. `SiteOverlayStyles.cssRules` przeniesione na koniec listy.
- **Kontrast WCAG AA — token, nie ad hoc kolor**: `axe-core` (realne uruchomienie, nie tylko
  ręczne wyliczenie ratio) znalazł błędy niewidoczne przy liczeniu kontrastu samych tokenów
  względem płaskiego `--bg` — kilka komponentów renderuje tekst na barwionych tłach (`--tint-*`,
  `--panel`), gdzie faktyczny kontrast jest niższy niż token-vs-bg. Naprawione podniesieniem
  wartości tokenów (`--faint` w obu motywach, `--red` i `--pink` w jasnym) z marginesem powyżej
  4.5:1 względem najgorszego zaobserwowanego tła, nigdy przez nowy jednorazowy kolor. Osobno:
  `Cv.kt` miał trzy miejsca z twardo wpisanym `color: #e6e4e3` (wartość `--ink-2` **ciemnego**
  motywu) zamiast `var(--ink-2)` — nigdy się nie przemotywowywały, złamane w jasnym motywie;
  naprawione na token.
- **Serwowanie eksportu dla Playwright**: `python3 -m http.server` (zero nowej zależności) nie ma
  fallbacku w stylu GitHub Pages dla nieznanych ścieżek — a `/projects/{nieznany-slug}` (tylko 4
  slugi zarejestrowane przez `addExtraRoute`) jest trasą czysto kliencką. `e2e/serve-static.py`,
  ~20-liniowy `http.server.SimpleHTTPRequestHandler`, dokłada fallback do `404.html` (wciąż zero
  nowej zależności — czysty stdlib), odzwierciedlając realne zachowanie GitHub Pages.
- **Test suite jako trzecia, niezależna warstwa**: `node:test` (`scripts/refresh-trophies`,
  bez zmian), `kotlin.test` w nowym `jsTest` source set (bez nowej zależności Gradle — dołączony
  do pluginu Kotlin), Playwright + `@axe-core/playwright` w nowym workspace `e2e/` (jedyne dwie
  nowe zależności całej fazy, uzasadnione Constitution Principle VIII — żadne istniejące
  narzędzie w repo nie potrafi sterować realną przeglądarką ani skanować a11y). Wszystkie trzy
  podpięte do `.github/workflows/ci.yml` — `site-export` rozszerzony o `:site:jsTest`, nowy job
  `e2e` konsumujący eksport przez `actions/upload-artifact`/`download-artifact`.
- **Funkcje wydzielone tylko dla testowalności**: `langForLocale(locale): Lang` z `Lang.kt`
  (`detectInitialLang()` czyta `window.navigator.language`, którego test na realnej przeglądarce
  Karma nie kontroluje) i `findProjectBySlug(entries, slug): ProjectEntry?` z `Slug.kt` (zamiast
  `.find{}` inline) — ten sam wzorzec co `parseTrophiesData` (`private` → `internal`).

**Zaimplementowane pliki**: `PageLayout.kt` (`updatePageMeta` + rozszerzone `PageLayoutData`),
siedem stron `pages/*.kt` + `pages/projects/Slug.kt` (własny `description`/`ogImage` per strona),
`Lang.kt` (+ `langForLocale`), `Trophies.kt` (`parseTrophiesData` → `internal`), `SiteTokenStyles.kt`
(poprawki kontrastu), `SiteStyles.kt` (kolejność `cssRules`), `SiteNavStyles.kt` (touch targets
48px), `Cv.kt` (token zamiast hex), `build.gradle.kts` (`jsTest` source set, self-hosted
`@font-face`), `resources/public/fonts/*.woff2` (nowe), `resources/public/og-banner.png` (nowy,
ręcznie wyrenderowany), cztery pliki `site/src/jsTest/kotlin/...` (nowe), workspace `e2e/` (nowy:
`package.json`, `playwright.config.ts`, `serve-static.py`, pięć plików `tests/*.spec.ts`),
`.github/workflows/ci.yml` (rozszerzony).

**Gotchas**: Eksportowany bundle Kobweb (`anjosite.js`) zawsze zawiera wbudowany widget
live-reload (`new EventSource("/api/kobweb-status", ...)`) niezależnie od layoutu eksportu —
na statycznym hoście ten endpoint nigdy nie istnieje, więc generuje powtarzalny
`console.error`/nieudane żądanie sieciowe co kilka sekund (nieszkodliwe wizualnie, ale blokuje
`page.waitForLoadState("networkidle")` w testach i psuje naiwny "brak console.error" check) — to
zachowanie frameworka, nie coś kontrolowane z poziomu kodu aplikacji; testy Playwright świadomie
filtrują tę jedną, konkretną wiadomość. `og:image` dla stron innych niż detale projektu to zawsze
statyczny `/og-banner.png` (fallback) — realny per-projektowy `og:image` działa tylko dla stron z
ustawionym `coverImageUrl` w `projects.json`.
