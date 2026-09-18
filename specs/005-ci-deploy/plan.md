# Implementation Plan: CI/Deploy

**Branch**: `feature-5/ci-and-deploy` | **Date**: 2026-09-18 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/005-ci-deploy/spec.md`

## Summary

Replace the fully manual `kobweb export --layout static` + hand-copy publish flow with two GitHub
Actions workflows: a deploy workflow (FR-001–FR-006) that runs `./gradlew :site:kobwebExport
-PkobwebExportLayout=STATIC` on every push to `main` and publishes the static output to the
separate `jagieloadrian.github.io` repo via an SSH deploy key (research.md §1/§4), and a nightly
trophy-refresh workflow (FR-007–FR-010) that is fully wired (schedule, secret, commit-back) but
whose actual PSN data-fetch step is a documented placeholder until ROADMAP F025 ships. GitHub
Pages must still be enabled on the destination repo by hand, once (research.md §8, clarified
2026-09-18) — not something either workflow can do for itself.

## Technical Context

**Language/Version**: No new application code — this phase is GitHub Actions YAML plus repo/org
configuration (deploy key, secrets, Pages settings). The site itself stays Kotlin 2.4.10
(Kotlin/JS), Compose HTML 1.11.1, unchanged from Phase 0–3.

**Primary Dependencies**: `actions/checkout@v4`, `actions/setup-java@v4` (Temurin 21,
research.md §2), `gradle/actions/setup-gradle` (official Gradle build-cache action — avoids
hand-rolling `actions/cache` for `~/.gradle`), `peaceiris/actions-gh-pages@v4` for the cross-repo
publish (research.md §4). No new Gradle/Kotlin/JS dependency (constitution Principle VIII) —
`kobwebExport`'s own Playwright-managed headless Chromium (research.md §3) needs no extra install
step on `ubuntu-latest`.

**Storage**: N/A — no backend, no database. `trophies.json` (existing, from Phase 3/004-pages)
is the only file either workflow writes, and only the nightly workflow ever touches it.

**Testing**: No automated test suite exists in this project (unchanged from every prior phase).
Verification is `quickstart.md`'s manual scenario checklist — a *real* push, a *real*
`workflow_dispatch` run, and a *real* visit to the live URL, not just green YAML syntax
(constitution Principle VII; spec.md Assumptions).

**Target Platform**: GitHub-hosted `ubuntu-latest` Actions runners, publishing to GitHub Pages
serving `jagieloadrian.github.io` at the root domain (spec.md FR-003, Phase 0's user-page
decision).

**Project Type**: CI/CD addition to the existing single-module (`:site`) Kobweb web frontend — no
new application module.

**Performance Goals**: None — spec.md's clarification session confirmed no fixed deploy-latency
SLA, only that the process completes with zero manual steps (spec.md SC-001).

**Constraints**: Zero new Gradle/JS dependencies (Principle VIII) — all new tooling lives at the
CI-workflow layer, outside the app's dependency graph. The cross-repo publish credential (a
deploy key, distinct from the `NPSSO` credential) and `NPSSO` itself MUST live only in GitHub
Actions secrets, never in a log, the published site bundle, or git history (Principle VI;
FR-008; SC-005). A failed build/publish MUST leave the previously published site untouched
(FR-005/SC-003) — enforced by `peaceiris/actions-gh-pages` only running its publish step after a
successful export, plus `concurrency: cancel-in-progress` to prevent two runs racing
(research.md §5).

**Scale/Scope**: Two new workflow files (`.github/workflows/deploy.yml`,
`.github/workflows/trophies.yml`), one new SSH deploy key pair, confirmation that the `NPSSO`
secret exists (may already, from Phase 4/F025 work), one one-time manual GitHub Pages
enablement on the destination repo — no changes to `site/` application code, no new page/route/
component.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

`.specify/memory/constitution.md` v1.0.0.

| Principle | Check | Result |
|---|---|---|
| I. Static-First, Zero Backend | Both workflows only build/publish a static export or commit a static JSON file — no server process introduced | PASS |
| II. Content as Data, Not Markup | N/A — no content/component changes this phase | PASS |
| III. Bilingual Parity (EN/PL) | N/A — no user-facing content changes this phase | PASS |
| IV. Single-Sourced Design Tokens | N/A — no styling changes this phase | PASS |
| V. Accessibility & Motion Discipline | N/A — no UI changes this phase | PASS |
| VI. No Secrets in the Client Bundle | Deploy key and `NPSSO` live exclusively as GitHub Actions secrets, read into env vars, never logged or written into `site/.kobweb/site/` (FR-008, research.md §4/§7) | PASS |
| VII. GitHub Pages Static Export as Sole Deployment Target | This phase's entire purpose is making `kobweb export --layout static` (via `kobwebExport`, research.md §1) the actual automated deployment path, with a real live-URL verification as definition of done (quickstart.md), not just a passing workflow syntax check | PASS |
| VIII. YAGNI on Dependencies | No new Gradle/JS dependency; only well-established, minimal Actions-marketplace steps added at the CI layer (research.md §1–§5); no concurrency/locking code hand-rolled where a one-line `concurrency:` block suffices (research.md §5); no notification tooling built where GitHub's default commit-status check already satisfies FR-006 (research.md §6) | PASS |

No violations — Complexity Tracking is not needed for this phase.

## Project Structure

### Documentation (this feature)

```text
specs/005-ci-deploy/
├── plan.md               # This file (/speckit-plan command output)
├── research.md           # Phase 0 output (/speckit-plan command)
├── data-model.md         # Phase 1 output (/speckit-plan command)
├── quickstart.md         # Phase 1 output (/speckit-plan command)
└── tasks.md              # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

No `contracts/` directory: this phase adds internal CI tooling (GitHub Actions workflows), not an
API/CLI/service boundary exposed to users or other systems — same rationale as every prior phase
(`specs/SUMMARY.md`, Faza 001–004: "No `contracts/` directory" in each). Workflow/secret shapes
are documented in `data-model.md` instead.

### Source Code (repository root)

```text
.github/
└── workflows/
    ├── deploy.yml                                        # NEW: build + publish on push to main
    └── trophies.yml                                       # NEW: nightly trophy-refresh scaffold
```

**Structure Decision**: One new top-level directory, `.github/workflows/`, which doesn't exist yet
in this repo — the standard, only location GitHub Actions discovers workflow files from. No
change anywhere under `site/`; this phase is CI configuration only, deliberately isolated from
the application source tree it builds.

## Complexity Tracking

*No violations — table intentionally omitted.*
