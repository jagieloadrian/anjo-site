# Implementation Plan: PSN Trophies Data Automation

**Branch**: `feature-6/trophies-data` | **Date**: 2026-09-18 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/006-trophies-data/spec.md`

## Summary

Replace `trophies.yml`'s placeholder data step (Phase 5) with a real fetch: a new, standalone
Node.js script (`scripts/refresh-trophies/index.mjs`) authenticates to PSN with the `NPSSO`
secret via `psn-api`, fetches the maintainer's trophy summary, title list, and per-title trophy
detail (research.md §2–§8), and writes `site/src/jsMain/resources/public/trophies.json` in the
8-key `stats` / 6-game / 10-trophy shape the 006 clarification session settled on — exactly the
structure `Trophies.kt`'s existing parser expects, plus one small, additive label-map change in
that same file (FR-008) so the four new stat keys render with a proper label instead of falling
back to their raw key name.

## Technical Context

**Language/Version**: Node.js 20+ (ESM `.mjs`, no TypeScript — research.md §1) for the new fetch
script. The site itself stays Kotlin 2.4.10 (Kotlin/JS), Compose HTML 1.11.1, unchanged.

**Primary Dependencies**: `psn-api` (npm, zero production dependencies of its own — research.md
§1). No new Gradle/Kotlin dependency (constitution Principle VIII) — the one `Trophies.kt` change
(FR-008) is a two-line addition to an existing `Map` literal, not a new library.

**Storage**: N/A — no backend, no database. `trophies.json` (existing static file) is the only
output.

**Testing**: No automated test suite in this project (unchanged from every prior phase).
Verification is `quickstart.md`'s manual scenario checklist: a real local run against the
maintainer's own PSN account, a deliberately-invalid-token failure-path check, and a real
`kobwebExport` + browser check that the page renders without crashing.

**Target Platform**: The fetch script runs on GitHub-hosted `ubuntu-latest` Actions runners
(nightly + `workflow_dispatch`, reusing Phase 5's `trophies.yml` scaffold) and on the maintainer's
own machine for local verification (User Story 3). The site itself is unchanged: GitHub Pages
static export at `jagieloadrian.github.io`.

**Project Type**: Small standalone Node.js CLI script added to a Kobweb (Kotlin/JS) web frontend
project — no new application module, no change to the site's build graph.

**Performance Goals**: None — this is a once-nightly batch job with no latency SC (spec.md has no
performance criterion).

**Constraints**: `NPSSO` MUST never appear in a log line, the output file, or the client bundle
(FR-007, constitution Principle VI) — the script only ever reads it from `process.env` and passes
it to `psn-api`'s auth functions; any caught error logs `error.message` only, never the token
itself. A failed fetch MUST leave `trophies.json` byte-for-byte untouched (FR-005) — enforced by
writing the output file only once, at the very end, after every fetch/merge step has succeeded.

**Scale/Scope**: One new script directory (`scripts/refresh-trophies/`, its own `package.json` +
lockfile + `index.mjs`), one new step in the existing `trophies.yml` (`actions/setup-node@v4` +
`npm ci` + `node index.mjs`, replacing the placeholder `echo` step), one small additive change to
`Trophies.kt`'s existing `statLabels`/`statColors` maps (FR-008) — no new page, route, or
component.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

`.specify/memory/constitution.md` v1.0.0.

| Principle | Check | Result |
|---|---|---|
| I. Static-First, Zero Backend | The fetch script is a build-time/CI batch job, not a server — it runs, writes a static JSON file, and exits; nothing it produces is a live backend | PASS |
| II. Content as Data, Not Markup | This phase's entire purpose is populating `trophies.json` (already the site's data-not-markup source for the Trophies page) with real data instead of a placeholder — no new hardcoded content introduced | PASS |
| III. Bilingual Parity (EN/PL) | N/A — `trophies.json`'s content (game/trophy names, stat numbers) is PSN's own data, not translatable UI copy; the page chrome around it is unchanged | PASS |
| IV. Single-Sourced Design Tokens | N/A — no styling changes | PASS |
| V. Accessibility & Motion Discipline | N/A — no UI/motion changes; FR-008's `Trophies.kt` change only adds two `Map` entries, no new markup/animation | PASS |
| VI. No Secrets in the Client Bundle | `NPSSO` lives only in the GitHub Actions secret (already wired, Phase 5) and the maintainer's own local shell env for manual runs — never written to `trophies.json`, never logged, never reaches `site/.kobweb/site/` (research.md §2, §9) | PASS |
| VII. GitHub Pages Static Export as Sole Deployment Target | This phase produces static JSON consumed at export time by the existing `kobwebExport` flow (Phase 5, unchanged) — definition of done includes a real `kobwebExport` + rendered-page check (quickstart.md step 4), not just script-level tests | PASS |
| VIII. YAGNI on Dependencies | `psn-api` is the one unavoidable dependency (there is exactly one PSN trophy API and this is its maintained JS client); no TypeScript/bundler/test-framework added for a ~150-line script (research.md §1); no retry/backoff library added where a fail-fast + rely-on-next-nightly-run is sufficient (research.md §9) | PASS |

No violations — Complexity Tracking is not needed for this phase.

## Project Structure

### Documentation (this feature)

```text
specs/006-trophies-data/
├── plan.md               # This file (/speckit-plan command output)
├── research.md           # Phase 0 output (/speckit-plan command)
├── data-model.md         # Phase 1 output (/speckit-plan command)
├── quickstart.md         # Phase 1 output (/speckit-plan command)
└── tasks.md              # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

No `contracts/` directory: `trophies.json`'s shape is already fully specified in `data-model.md`
(it's a fixed, existing contract this phase populates, not a new interface this phase designs) —
same rationale as every prior phase (`specs/SUMMARY.md`; `specs/005-ci-deploy/plan.md`).

### Source Code (repository root)

```text
scripts/
└── refresh-trophies/
    ├── package.json        # NEW: declares `psn-api` as the sole dependency
    ├── package-lock.json   # NEW: generated by `npm install`
    └── index.mjs           # NEW: the fetch script (research.md §2-§9)

.github/workflows/
└── trophies.yml            # MODIFIED: placeholder step replaced with setup-node + real fetch

site/src/jsMain/kotlin/com/anjo/anjosite/pages/
└── Trophies.kt             # MODIFIED: statLabels/statColors gain 4 entries (total/gold/silver/bronze) — FR-008, no other change
```

**Structure Decision**: New top-level `scripts/` directory, isolated from `site/` (the Kotlin/JS
app) the same way `.github/workflows/` was kept isolated from it in Phase 5 — this is CI/data
tooling, not application code. Its own `package.json` keeps Node dependencies scoped to that one
directory instead of polluting the repo root (which has no Node tooling otherwise). The one
`Trophies.kt` touch is the narrowest possible diff satisfying FR-008 (two `Map` entries), not a
restructuring of the page.

## Complexity Tracking

*No violations — table intentionally omitted.*
