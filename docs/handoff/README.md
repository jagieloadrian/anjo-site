# Handoff — static HTML/CSS/JS

Trzy pliki, zero zależności i zero runtime'u poza jednym plikiem JS. To ta sama makieta, ale przepisana na czyste, czytelne klasy CSS — do przeniesienia na komponenty Kotlin/JS + React.

```
handoff/
  index.html   # markup wszystkich ekranów (Home, About, Projects, Project detail, Trophies, Contact, CV)
  styles.css   # tokeny w :root + warstwa komponentów + breakpointy
  app.js       # 3 zachowania: router, efekt pisania, prompt kontaktowy
```

Otwórz `index.html` bezpośrednio w przeglądarce — działa bez serwera. Fonty (Archivo, JetBrains Mono) lecą z Google Fonts; jeśli chcesz zero zewnętrznych zapytań, zwenduruj `.woff2` i podmień `<link>` na `@font-face`.

## Mapowanie na komponenty

| Sekcja HTML | Komponent |
| --- | --- |
| `header.nav` | `Nav(route, lang, onRoute, onLang)` |
| `main[data-screen]` | jeden komponent na trasę: `HomeScreen`, `AboutScreen`, `ProjectsScreen`, `ProjectScreen`, `TrophiesScreen`, `ContactScreen`, `CvScreen` |
| `.card` | `ProjectCard(meta: ProjectMeta)` |
| `.tl` | `TimelineEntry(entry: Job)` |
| `.stats` / `.stat` | `StatRow(stats: TrophyStats)` |
| `.cover` | `GameCover(game: Game)` |
| `.feed-row` | `TrophyRow(trophy: Trophy)` |
| `.term` | `Terminal(lines: List<Line>)` — hero i contact używają tego samego |
| `.tag` | `Tag(label, variant)` — warianty: domyślny, `tag--pink`, `tag--cyan`, `tag--red` |
| `.note` | `DesignNote(children)` — patrz niżej |

## Tokeny

Wszystkie kolory, fonty i linie siedzą w `:root` w `styles.css`. Zmiana palety = zmiana pięciu zmiennych (`--pink`, `--cyan`, `--red`, `--bg`, `--ink`). Nic nie jest zahardkodowane w markupie poza kilkoma `style=""` do jednorazowych odstępów — te możesz spokojnie zamienić na klasy modyfikujące.

## Wersja mobilna

Jeden layout, dwa breakpointy — nie ma osobnych plików na mobile:

- **860px** — siatki dwukolumnowe (`.labelled`, `.split`) zwijają się do jednej kolumny, pionowe linie zamieniają się w poziome.
- **720px** — nawigacja staje się poziomo przewijalnym paskiem (zamiast zawijać się w cztery rzędy), padding sekcji spada z 40/24px na 28/20px, komórki statystyk i linków układają się pionowo, przyciski dostają `min-height: 48px` (minimalny cel dotyku).
- Siatki kart i okładek nie mają breakpointa — `repeat(auto-fit, minmax(320px, 1fr))` sam schodzi do jednej kolumny, a linie to przerwa w siatce, więc rysują się poprawnie przy każdej liczbie kolumn.
- Typografia jest na `clamp()`, więc nagłówki skalują się płynnie; nie ma ani jednej stałej szerokości ani wysokości na bloku z tekstem.
- `prefers-reduced-motion: reduce` wyłącza scanlines, glitch, kursor i efekt pisania (terminal wypisuje się od razu w całości).

Sprawdź w DevTools na 390px i 768px — to dwa punkty, w których widać oba breakpointy.

## Co jest zaślepką

- **Liczby trofeów i okładki gier** — myślniki i ramki `IMAGE SLOT` / `COVER`. Tytuły gier są prawdziwe. Dane mają przyjść z `trophies.json`.
- **Screeny aplikacji** na ekranie projektu — pusty slot.
- **Linki do repo** celują na GitHub (`github.com/jagieloadrian`), choć obecne repozytoria są na GitLabie. Docelowo per-projekt w front matterze.

## Panel „design notes"

Przycisk `◆ design notes` w nawigacji przełącza klasę `notes-on` na `<body>` i pokazuje żółte adnotacje projektowe. To narzędzie do przeglądu makiety, nie element strony — przy wdrożeniu usuń przycisk, blok `.note` z CSS i sekcję „design notes" z `app.js`. Uzasadnienia decyzji zostają w `DECISIONS.md` w katalogu głównym.

## Routing na GitHub Pages

`app.js` przełącza sekcje przez `hidden` — to zaślepka pod prawdziwy router. Dwie opcje przy wdrożeniu:

1. **hash routing** (`/#/projects`) — działa od razu, bez konfiguracji;
2. **history routing** — ładne ścieżki, ale wymaga `404.html` będącego kopią `index.html` oraz `base` ustawionego na nazwę repo.

Ekran `project` ma w nawigacji podświetlony `Projects` (mapowanie `NAV_FOR` w `app.js`) — przy prawdziwym routerze to wypada z kodu, bo trasa `projects/:slug` jest dzieckiem `projects`.
