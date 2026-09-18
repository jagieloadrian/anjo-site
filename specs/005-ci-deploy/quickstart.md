# Quickstart: CI/Deploy

Validation guide for this phase — proves the two workflows actually work, not just that YAML
parses. See `data-model.md` for the exact fields each workflow uses and `research.md` for why.

## Prerequisites

- A deploy key pair generated for `jagieloadrian.github.io` (public half added there as a
  write-enabled Deploy Key; private half stored as the `DEPLOY_KEY` secret in `anjo-site` —
  fixed name, see tasks.md T003/T008).
- `NPSSO` secret added to `anjo-site` (may already exist from Phase 4/F025 work, or add a
  placeholder value now — the trophy workflow's data step is a documented no-op until F025 ships,
  research.md §7).
- GitHub Pages will not be live yet — that's expected until the one-time manual step below.

## 1. Validate the export locally first

```bash
./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC
```

Expected: succeeds, produces `site/.kobweb/site/` with one HTML file per route (including each
project slug — see `specs/SUMMARY.md`'s Faza 004 notes on `addExtraRoute`). If this fails locally,
it will fail in CI identically — fix here first.

## 2. Validate the deploy workflow end-to-end

1. Push a small, visible content change to `main` (e.g. a copy tweak).
2. Open the Actions tab — confirm a run starts automatically with no manual trigger
   (spec.md User Story 1, Acceptance Scenario 1).
3. Confirm the run succeeds and check its logs for the `peaceiris/actions-gh-pages` publish step.
4. **One-time only**: on `jagieloadrian.github.io`, enable Pages (Settings → Pages → source:
   `main` branch, root) — research.md §8. Skip this step on every subsequent run.
5. Open `https://jagieloadrian.github.io/` — confirm the pushed change is live, with no broken
   CSS/font/JS/image reference, and click through all seven routes + one project detail page
   (spec.md User Story 2, SC-002).
6. Push a deliberately broken change (e.g. a Kotlin compile error) — confirm the run fails, the
   commit shows a failed check (SC-006), and the previously published site is untouched (SC-003).

## 3. Validate the trophy refresh workflow

1. Trigger it manually via `workflow_dispatch` (don't wait for the nightly schedule).
2. Confirm it completes (successfully, or as a documented no-op if F025 hasn't shipped yet —
   spec.md User Story 3, Acceptance Scenario 2).
3. Read the run's logs end-to-end — confirm the `NPSSO` value never appears in plain text
   anywhere (SC-005).
4. If F025 has shipped and the script ran for real: confirm `trophies.json` was committed to
   `main` with updated content, and that the deploy workflow above then republished it.

## Definition of done

All six checks in steps 2–3 pass with a *real* execution (not a dry run) — matching spec.md's
Success Criteria in full.
