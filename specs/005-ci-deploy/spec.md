# Feature Specification: CI/Deploy

**Feature Branch**: `feature-5/ci-and-deploy`

**Created**: 2026-09-18

**Status**: Draft

**Input**: User description: "Phase 5 — CI/Deploy: automate what has so far only been run by hand
— `kobweb export -PkobwebExportLayout=STATIC` from `site/` — as a GitHub Actions workflow that
builds and publishes the static output on every push to `main`, plus a separate nightly workflow
for `trophies.json`. Maps to ROADMAP.md F027–F029."

## Clarifications

### Session 2026-09-18

- Q: Does the destination repo `jagieloadrian.github.io` already exist and have GitHub Pages
  enabled? → A: The repo already exists, but GitHub Pages is not yet enabled on it.
- Q: Is there a hard latency target for how fast a push must go live? → A: No fixed SLA — only
  requirement is that it completes automatically, with zero manual steps.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Automatic publish on push (Priority: P1)

As the site maintainer, when I push a change to `main`, the site rebuilds and republishes itself
automatically — I never run an export or upload command by hand again.

**Why this priority**: This is the entire point of the phase. Every other story depends on this
pipeline existing; without it there is no CI/deploy feature at all.

**Independent Test**: Push a small, visible content change to `main` and confirm the live site
reflects it without running any local command.

**Acceptance Scenarios**:

1. **Given** a change merged to `main`, **When** the push completes, **Then** a build-and-publish
   run starts automatically with no manual trigger.
2. **Given** a build-and-publish run finishes successfully, **When** the live site is opened,
   **Then** it serves the updated content.
3. **Given** a build-and-publish run fails (e.g. a compile error), **When** the failure occurs,
   **Then** the previously published site is left untouched — visitors never see a half-updated
   or broken deploy.

---

### User Story 2 - Correct publish destination (Priority: P2)

As the site maintainer, the published site is reachable at its intended root-domain address
(`jagieloadrian.github.io`), with every page and asset resolving correctly there — not at a
repository-subpath address.

**Why this priority**: Publishing successfully but to the wrong address, or with broken asset
paths, is worse than not publishing at all — visitors hit dead links or an unstyled page. This
depends on Story 1's pipeline existing but is a distinct thing that can break independently (a
correct build published to the wrong place, or with the wrong base path baked in).

**Independent Test**: After a deploy, open the root-domain URL and click through every nav
destination and the project-detail pages.

**Acceptance Scenarios**:

1. **Given** the deployed site, **When** it is opened at the root-domain URL, **Then** the home
   page loads with no broken CSS, font, script, or image reference.
2. **Given** the deployed site, **When** navigating to any of the seven top-level routes plus a
   project detail page, **Then** each resolves without a 404.

---

### User Story 3 - Automatic trophy data refresh (Priority: P3)

As the site maintainer, trophy data refreshes on a recurring nightly schedule on its own, without
anyone manually re-running an export or handling the PlayStation credential by hand.

**Why this priority**: Lowest priority because it is independent of whether the site deploys at
all — the site works and looks correct with static placeholder trophy data. It also depends on a
PlayStation-data source that isn't built yet (ROADMAP F025, Phase 4), so this story's own scope is
partly about scaffolding a process correctly ahead of a dependency landing, not shipping a fully
live data feed today.

**Independent Test**: Trigger the scheduled workflow (or wait for its first scheduled run) and
confirm it completes and its logs contain no credential value in plain text.

**Acceptance Scenarios**:

1. **Given** the nightly schedule fires and the upstream data source is available, **When** the
   run completes successfully, **Then** `trophies.json` reflects freshly regenerated data.
2. **Given** the nightly schedule fires and the upstream data source is not yet available
   (Phase 4 not shipped), **When** the run executes, **Then** it fails or no-ops visibly and
   documented as blocked, rather than silently corrupting or blanking the existing file.
3. **Given** any run of this workflow, **When** its logs are inspected, **Then** no credential
   value appears anywhere in plain text.

---

### Edge Cases

- What happens when the build step succeeds but the publish step fails partway through? The site
  must not end up serving a partially-updated or broken deploy — it keeps serving the last
  successful version.
- What happens when a second push to `main` arrives while a previous deploy run is still in
  progress? Runs must not race and corrupt the published output — either queue in order or cancel
  the superseded run in favor of the latest.
- What happens when the nightly trophy-refresh run fails (upstream API down, credential invalid,
  or the data source doesn't exist yet)? The site keeps serving the last-known-good
  `trophies.json`, never a blank or broken trophies page.
- What happens when the trophy-refresh credential is invalid or expired? The failure must be
  visible and actionable to the maintainer without the credential's value itself ever appearing in
  a log, an error message, or the published bundle.
- What happens the first time the publish step runs, before GitHub Pages has ever been enabled on
  the destination repo? The publish step may succeed at pushing content, but the site will not
  actually be live until Pages is enabled on that repo — this is a one-time manual prerequisite,
  not a workflow failure to engineer around.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST rebuild and publish the site automatically whenever a change is
  pushed to the designated deploy branch (`main`), with no manual command required.
- **FR-002**: The build step MUST produce a static, backend-free export as the sole artifact that
  gets published (constitution Principle I/VII).
- **FR-003**: The publish step MUST make the exported site reachable at the project's intended
  root-domain URL (`jagieloadrian.github.io`), not a repository-subpath URL.
- **FR-004**: The system MUST support publishing from this repository (`anjo-site`) to the
  separate destination that actually serves the root-domain URL, since this repository is not
  itself that destination.
- **FR-005**: A failed build or publish attempt MUST leave the previously published site
  unaffected — visitors must never see a partially-updated or broken deploy.
- **FR-006**: The system MUST surface deploy failures to the maintainer through the same
  mechanism they already use to track pushes (e.g. a failed status check on the commit), without
  requiring them to proactively dig through raw logs to notice a failure occurred.
- **FR-007**: A separate, independently scheduled process MUST attempt to regenerate trophy data
  on a recurring nightly cadence.
- **FR-008**: The trophy-data refresh process MUST read its required credential exclusively from a
  secret store and MUST NOT expose it in logs, in the published site bundle, or in git history
  (constitution Principle VI).
- **FR-009**: The trophy-data refresh process MUST fail safely: if it cannot complete, the
  previously published trophy data MUST remain what visitors are served.
- **FR-010**: The trophy-data refresh process MUST be verifiable as a working scheduling/secret
  mechanism independently of whether its upstream data-source integration (ROADMAP F025) already
  exists — a documented, visibly-blocked run is an acceptable outcome until that dependency ships.

### Key Entities

- **Deploy Workflow**: The automated process that turns a push to `main` into a live, published
  site. Inputs: repository source at a given commit. Output: a static export reachable at the
  root-domain URL.
- **Trophy Refresh Workflow**: The automated, independently-scheduled process that regenerates
  trophy data. Inputs: a stored credential, a scheduled trigger. Output: an updated `trophies.json`
  (or, while its upstream dependency is unbuilt, a visibly-blocked run).
- **Publish Destination**: The root-domain address the site is actually served from, distinct from
  the repository that builds it.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A push to `main` results in the live site reflecting that change with zero manual
  steps taken by the maintainer — no fixed latency target, only that the process completes on its
  own.
- **SC-002**: 100% of the site's routes — every top-level page plus every project detail page —
  load at the published root-domain URL with no broken asset or link.
- **SC-003**: A failed deploy attempt never leaves the live site unreachable or showing broken or
  partial content; the previously working version keeps serving in every observed failure case.
- **SC-004**: Once its upstream data source is available, trophy data refreshes at least once
  every 24 hours with zero manual intervention.
- **SC-005**: Across all recorded workflow runs, the trophy-refresh credential appears zero times
  in a log, in git history, or in the published site bundle.
- **SC-006**: A deploy failure is visible to the maintainer within the same place they already
  look after pushing (e.g. commit status), with no separate manual check required to discover it.

## Assumptions

- The deploy trigger branch is `main` — confirmed as this repository's actual default branch.
- Publishing this repository's static export to the root-domain address requires a cross-repository
  publish step (this repo is `anjo-site`, the root-domain address is served by a separate
  `jagieloadrian.github.io` repository per the Phase 0 decision) — the exact credential/mechanism
  for that cross-repo push (deploy key, PAT, or another approach) is a planning-level decision, not
  a scope question, and is left to `/speckit-plan`.
- ROADMAP F025 (the nightly PSN-API job that produces real trophy data) has not shipped yet as of
  this spec. This phase builds and proves the scheduling/secret-wiring shape of the trophy-refresh
  workflow; wiring it to a real PSN data source happens when F025 lands, not as part of this phase.
- The destination repo `jagieloadrian.github.io` already exists but does not yet have GitHub
  Pages enabled. Enabling Pages on that repo (pointing it at whatever branch/source the publish
  step writes to) is a required one-time manual prerequisite the maintainer must complete —
  it is not something the deploy workflow itself can be expected to do as part of every run.
- Final end-to-end verification (an actual live deploy, an actual `NPSSO` secret) requires the
  maintainer to take one-time manual setup actions outside this repository's code (adding a repo
  secret, enabling Pages on the destination repo as above) — this is expected and out of scope to
  automate away.
- No automated test suite exists in this project (unchanged from every prior phase); verification
  of this feature is manual — a real push, a real scheduled run, and a real visit to the live URL.
