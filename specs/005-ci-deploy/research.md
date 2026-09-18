# Research: CI/Deploy

## 1. Export invocation for CI

**Decision**: `./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC` run from the repo root.

**Rationale**: Decompiling the cached `kobweb-application` Gradle plugin jar
(`com.varabyte.kobweb.gradle:application:0.25.1`,
`KobwebApplicationPlugin.class`/`AppBlock$ExportBlock.class`) confirms the `kobwebExport` task
reads its layout via `providers.gradleProperty("kobwebExportLayout")` — i.e. the exact
`-PkobwebExportLayout=STATIC` property the `kobweb export --layout static` CLI wrapper sets under
the hood. Running the Gradle task directly means CI never needs the separate `kobweb` CLI binary
installed (it isn't available in this repo's own dev environment either — confirmed no `kobweb`
binary on `$PATH` locally) — only `./gradlew`, which is already vendored via the wrapper
(`gradle-9.6.1`).

**Alternatives considered**: Installing the `kobweb` CLI (via SDKMAN or a release download) and
running `kobweb export --layout static` — rejected, adds an install step and a second tool to keep
in sync with the Gradle plugin version, for zero behavioral difference (the CLI just invokes this
same Gradle task).

## 2. JDK toolchain for the CI runner

**Decision**: `actions/setup-java@v4` with `distribution: temurin`, `java-version: '21'`.

**Rationale**: No `jvmToolchain(...)` pin exists anywhere in `site/build.gradle.kts` or
`settings.gradle.kts` — the project relies on whatever JDK is on the invoking machine. Gradle
9.6.1 (this repo's wrapper version) requires JDK 17+ to run at all; JDK 21 (LTS, Temurin) is the
current safe default for a Kotlin 2.4.10 / Compose Compiler project and is what GitHub-hosted
`ubuntu-latest` runners already cache, keeping the setup step fast.

**Alternatives considered**: Pinning JDK 17 (Gradle's stated minimum) — rejected in favor of 21
since it's a newer LTS with no known incompatibility here and matches typical current Kobweb
project generator output; either would work, 21 has more runway before EOL.

## 3. `kobwebExport` needs a real (headless) browser — CI implication

**Decision**: No extra browser-install step is required; let `kobwebExport` auto-download its own
Chromium via Playwright on first run, and cache Gradle's dependency/build caches
(`~/.gradle/caches`, `~/.konan`, the project's `.gradle`/`.kobweb` output) between runs via
`actions/cache` or `gradle/actions/setup-gradle`'s built-in caching to avoid re-downloading it
every run.

**Rationale**: `KobwebExportTask.class` references `com.varabyte.kobweb.gradle.application.util.
PlaywrightCache` and ships user-facing strings like *"Clearing the browser path will go back to
auto-downloading an appropriate browser"* — static export isn't just an HTML template dump, it
actually launches a headless Chromium (via Playwright) to render each route and capture the
result. `ubuntu-latest` GitHub-hosted runners have the system libraries Playwright's Chromium
needs pre-installed for exactly this reason (it's a very common CI pattern); no `apt-get` step
should be needed, but if a run fails with a Playwright "missing dependencies" error, the fix is
adding a `playwright install --with-deps chromium`-style step, not disabling headless export.

**Alternatives considered**: Manually installing/caching a specific Chromium binary — rejected,
`kobwebExport` already manages its own Playwright-downloaded browser; fighting that mechanism
would be fragile YAGNI-violating work for no benefit (constitution Principle VIII).

## 4. Cross-repo publish mechanism (`anjo-site` → `jagieloadrian.github.io`)

**Decision**: `peaceiris/actions-gh-pages@v4` with `external_repository:
jagieloadrian/jagieloadrian.github.io`, `publish_branch: main`, `publish_dir:
site/.kobweb/site` (per `.kobweb/conf.yaml`'s `server.files.prod.siteRoot`), `keep_files: false`,
authenticated via `deploy_key` (an SSH keypair, private half stored as a secret in *this* repo,
public half added as a write-enabled Deploy Key on the destination repo) rather than a personal
access token.

**Rationale**: `peaceiris/actions-gh-pages` is the de facto standard action for exactly this
cross-repo static-publish shape and is what most Kobweb/Compose-HTML-on-GitHub-Pages writeups use.
A repo-scoped SSH deploy key is least-privilege — it can only push to
`jagieloadrian.github.io`, unlike a personal access token which (unless a fine-grained token is
carefully scoped) typically carries broader account access. `main` as the publish branch matches
how a GitHub *user/organization* page is conventionally served (root of the default branch), since
this is not a project page using a `gh-pages` branch convention.

**Alternatives considered**: `actions/deploy-pages` (the newer, artifact-based native Pages
deploy flow) — rejected for this specific case because it deploys *within the same repository*
that ran the workflow; there is no first-party way to point it at a different repository, and this
project's Phase 0 decision requires publishing from `anjo-site` to the separate
`jagieloadrian.github.io` repo. A fine-grained PAT instead of a deploy key — rejected as strictly
broader-scoped for the same job.

## 5. Concurrent-run safety

**Decision**: Add a top-level `concurrency: { group: deploy, cancel-in-progress: true }` block to
the deploy workflow.

**Rationale**: GitHub Actions runs triggered by rapid successive pushes to `main` are not
serialized by default — two runs can race to publish. `cancel-in-progress: true` cancels a
superseded in-flight run in favor of the latest push, which is simpler and cheaper than queuing
(and always converges to the newest content, never an out-of-order stale publish) — satisfies the
"concurrent push" edge case in spec.md without any custom locking logic (Principle VIII).

## 6. Surfacing deploy failures (FR-006)

**Decision**: No extra tooling — GitHub Actions already attaches a pass/fail check to the
triggering commit/push automatically, visible in the same commit view and (if repo notification
settings are left at their default) via email/GitHub notification on failure. Nothing to build.

**Rationale**: This satisfies FR-006/SC-006 with zero added code or dependency — exactly the kind
of default the constitution's YAGNI principle (VIII) asks to recognize rather than build around.

## 7. Nightly trophies workflow — scaffold shape (blocked on ROADMAP F025)

**Decision**: A separate `.github/workflows/trophies.yml`, trigger `on: schedule` (a nightly cron)
plus `workflow_dispatch` for manual testing, reading an `NPSSO` repo secret into an environment
variable (never into a logged command argument), running whatever script Phase 4/F025 will add
(not yet present in this repo — confirmed via repo-wide search, no `psn-api` reference exists
outside planning docs), then committing any changed `trophies.json` back to `main` if the script
produced output. Until F025 lands, the script step is a documented placeholder (e.g. `echo
"blocked on ROADMAP F025 — psn-api script not yet implemented" && exit 0`) so the workflow is
real, schedulable, and secret-wired without inventing a fake data-fetch script.

**Rationale**: Directly satisfies spec.md FR-010 / User Story 3's Acceptance Scenario 2 (a
documented, visibly-blocked run is an acceptable outcome while the dependency is unbuilt).
Committing the regenerated file back to `main` (rather than writing it straight into a deploy
artifact) keeps `trophies.json` reviewable in git history and reuses the exact same deploy workflow
from research §1/§4 to publish it — no second publish path to maintain.

**Alternatives considered**: Skipping this workflow file entirely until F025 ships — rejected
because spec.md explicitly scopes "prove the scheduling/secret-wiring mechanism" as this phase's
job (FR-010), independent of the data source landing.

## 8. GitHub Pages enablement on the destination repo

**Decision**: Not automated by either workflow. Documented as a one-time manual prerequisite:
after the deploy workflow's first successful run pushes content to `jagieloadrian.github.io`'s
`main` branch, the maintainer enables Pages on that repo (Settings → Pages → source: `main`
branch, root) once, by hand.

**Rationale**: Per `/speckit-clarify` session 2026-09-18, the destination repo already exists but
Pages is not yet enabled there. GitHub's Pages-enablement UI action isn't something a workflow
running in a *different* repo (`anjo-site`) can reach without additional, broader-scoped
cross-repo admin permissions — and it's a true one-time setup step, not a per-run concern, so
building automation for it would be speculative complexity for a single manual click (Principle
VIII / YAGNI).
