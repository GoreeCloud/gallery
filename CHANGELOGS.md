# GoreeCloud Gallery Changelogs

This file is the authoritative repository change-history record under Standard — Repository Feature Tracking and Changelog Governance v1.0. It supersedes the retired singular `CHANGELOG.md` filename while preserving the existing Gallery history below.

## 2026-09-24 — Android-authorized Move foundation

### Added

- Provider-owned MediaStore `RELATIVE_PATH` projection and normalization without creating filesystem authority.
- Existing-folder and new-folder move destination policies constrained to the current authorized media scope.
- Android 11+ exact-item write authorization through `MediaStore.createWriteRequest(...)`, canonical pending-state restoration, and bounded `RELATIVE_PATH` update execution.
- JVM/adapter regression coverage for destination authority, unsafe paths/names, foreign selections, provider metadata conflicts, and canonical pending state.

### Lifecycle boundary

The user-facing Gallery Move action remains disabled in this foundation. App-layer lifecycle/UI integration, post-move refresh behavior, representative physical-device/OEM validation, GLAZE UI acceptance, Release Candidate, production, and Stable qualification remain open.

---

## 2026-09-22 — Repository feature/changelog governance migration

### Changed

- Established `IMPLEMENTED-FEATURES.md`, `PLANNED-FEATURES.md`, and `CHANGELOGS.md` as the repository-native feature/change authority.
- Retired `FEATURE-ROADMAP.md` and the singular `CHANGELOG.md` filename from the active control model.
- Removed the obsolete requirement to synchronize roadmap/changelog authority with Google Drive.

### Lifecycle boundary

This documentation/control-plane migration does not alter Gallery runtime behavior and does not establish Release Candidate, Production Acceptance, or Stable qualification.

---

## Preserved historical changelog

This changelog records material source, build, validation, release-engineering, and product-readiness changes in the dedicated `GoreeCloud/goreecloud-gallery` repository. Historical development that occurred in the temporary website-repository build carrier remains preserved in Git and in the GoreeCloud patch provenance records.

## 1.0.0 — Stable candidate

### Added

- gc.9 final package-identity patch with `VERSION_NAME=1.0.0` and Android `VERSION_CODE=10009`;
- non-secret Stable signing execution runbook;
- representative-device acceptance runbook;
- authoritative `docs/STABLE-CANDIDATE-1.0.0.md` promotion contract;
- gc.10 Settings cleanup and Android-native Privacy & permissions access;
- gc.11 native Glaze UI 1.0 semantic resources, adaptive Settings composition, practical target sizing, and fail-closed conformance checks;
- gc.12 Glaze UI browsing-surface integration for the folders screen and opened-folder media grid;
- gc.13 Glaze UI search-surface integration with shared Canvas, branded search chrome, adaptive browsing gutters, and a rounded muted empty-state surface;
- gc.14 Glaze UI media-viewer overlay treatment with muted semantic chrome and comfortable viewer action targets;
- gc.15 representative-device refinement for sorting/grouping/filter dialogs, destructive confirmations, overflow menus, Settings density/header treatment, and folder label/count hierarchy;
- gc.16 representative-device dialog-geometry refinement for content-sized sorting/grouping/filter surfaces.

### Changed

- ordinary acceptance CI builds and validates `GoreeCloud-Gallery-1.0.0.apk` while retaining `acceptance-candidate` classification;
- the protected manual signing workflow defaults to `1.0.0` while retaining `signed-release-candidate` classification;
- gc.10 removes misleading fixed-thumbnail controls and upstream purchase UI while preserving meaningful Gallery settings;
- gc.11 increments Android `VERSION_CODE` to `10011` while preserving semantic version `1.0.0`;
- gc.12 increments Android `VERSION_CODE` to `10012` while preserving semantic version `1.0.0`;
- gc.13 increments Android `VERSION_CODE` to `10013` while preserving semantic version `1.0.0`;
- gc.14 increments Android `VERSION_CODE` to `10014` while preserving semantic version `1.0.0`;
- gc.15 increments Android `VERSION_CODE` to `10015` while preserving semantic version `1.0.0`;
- gc.16 increments Android `VERSION_CODE` to `10016` while preserving semantic version `1.0.0`;
- Settings uses Glaze Canvas, gradient navigation emphasis, rounded Raised rows, Android ripple feedback, 48dp comfortable interactive targets, dedicated light/dark semantic palettes, and wider native insets at `sw600dp` and `sw840dp`;
- gc.15 reduces redundant Settings divider rules and excessive card spacing while reinforcing the Glaze app-bar treatment;
- primary browsing surfaces use the Glaze Canvas, branded menu chrome, adaptive 8/16/24dp media-aware gutters, Raised empty-state actions, comfortable action targets, and semantic loading accent without cardifying every thumbnail;
- folder names use the semantic primary text role and stronger weight while item counts use the semantic muted role and smaller secondary typography;
- search reuses the same browsing composition and adds a restrained muted Raised empty state so search visually belongs to the same Glaze family as folders and media;
- the full-screen media viewer uses restrained muted Glaze chrome and a rounded bottom action overlay while keeping the media itself visually dominant;
- viewer actions use 48dp comfortable target sizing without altering the behavior of delete or other destructive operations;
- sorting, grouping, and media-filter dialogs use rounded Glaze surfaces, semantic accent controls, 48dp comfortable targets, and reduced redundant divider rules;
- gc.16 changes those dialog root ScrollViews from full-height to content-driven `wrap_content`, disables forced viewport filling, and retains overflow scrolling only when content exceeds the available viewport;
- destructive folder-deletion confirmation retains its existing confirmation semantics while using the semantic danger role for warning text;
- toolbar overflow menus use coordinated Glaze light/dark surfaces with rounded geometry and restrained accent outlines;
- Gallery records Glaze UI `1.0.0` and canonical reference revision `d6e446fd8ef251259d16368d50aad90d9287a774` as its native conformance target;
- repository/source validation requires the current Glaze patch line and fails closed if the semantic resource, Settings, browsing-surface, search, media-viewer, transient-surface, or dialog-geometry integration contract disappears;
- Stable promotion must reuse the exact accepted signed binary rather than rebuilding merely to alter release labeling.

### Validated

- gc.9 exact-head validation reconstructed pinned Fossify Gallery and Commons source, passed repository/security/source checks, executed `GoreeCloudGalleryPolicyTest`, passed Android lint, and assembled/validated the `1.0.0` FOSS acceptance APK;
- gc.10 exact-head and post-merge validation passed after Settings simplification and produced the updated acceptance APK from `main`;
- gc.11 exact-head run 31973570278 and post-merge main run 31977767072 completed successfully through repository/security/source validation, behavioral tests, Android lint, APK assembly, APK/evidence validation, and artifact upload;
- gc.12 exact-head run 31978012920 and post-merge main run 31978605284 completed successfully through every acceptance step; PR #16 was squash merged as `1e1099246fb771508be16a8423771a66f6b9055d`;
- gc.13 exact-head run 31980042523 and post-merge run 31982557178 completed successfully through every acceptance step; PR #17 was squash merged as `87f87ecdca9317d4acf3cd9a8d74a766eb5dd060`;
- gc.14 exact-head run 31983755647 completed successfully through every acceptance step, retained the `1.0.0` acceptance APK/evidence artifact, and PR #18 was squash merged as `9767bccfd5f43805f81f796c27b4168b6649f782`;
- representative-device screenshots from the gc.14 acceptance build informed the gc.15 refinement scope for transient surfaces, Settings density/header consistency, and folder typography hierarchy;
- gc.15 exact-head run 31988324362 and post-merge main run 31988927104 completed successfully through every acceptance step; PR #19 was squash merged as `418bb064c9abf72b91087966bc88cedec0bcda53`;
- representative-device gc.15 screenshots identified excessive empty vertical space in the Sort by dialog and motivated the narrow gc.16 geometry refinement;
- gc.16 requires its own exact-head acceptance run before merge because it changes dialog root geometry, versionCode, patch provenance, conformance validation, and documentation.

### Release boundary

`1.0.0` is **not yet Stable**. Long-lived signing and recovery, protected `stable-release` administration, repository-level `main` protection, representative-device permissions/file-operation/user-profile/accessibility/Glaze UI acceptance, same-signer upgrade/recovery testing, and final release evidence remain blocking.

## Repository readiness hardening — gc.8

### Added

- root GNU GPL v3 license text;
- `SECURITY.md` vulnerability and security-boundary guidance;
- `CONTRIBUTING.md` contribution, patch-provenance, Glaze UI, and release guidance;
- `NOTICE.md` modified-work and exact upstream provenance record;
- `.github/CODEOWNERS` review ownership;
- `docs/ARCHITECTURE.md` runtime, source, privacy, user/profile, Glaze UI, build, and release architecture;
- `docs/GLAZE-UI.md` Gallery-specific Glaze UI implementation and release-review contract;
- `docs/REPOSITORY-READINESS.md` readiness classification and open stable-release blockers;
- `docs/RELEASE-EVIDENCE-TEMPLATE.md` comprehensive candidate/stable evidence record;
- `scripts/validate-repository-structure.sh` fail-closed repository layout/governance validation;
- `scripts/write-build-evidence.sh` machine-readable non-secret build identity and patch-line provenance;
- gc.8 GoreeCloud-owned behavioral-test foundation for enforced thumbnail presentation policy.

### Changed

- pull-request CI checks out and verifies the exact pull-request head SHA rather than relying on GitHub's synthetic merge checkout;
- push/manual acceptance validates the exact `github.sha`;
- signed-candidate CI verifies the exact dispatched revision;
- repository security guardrails reject unapproved write permissions, `pull_request_target`, automatic signed-candidate triggers, missing checkout credential hardening, mutable third-party Action refs, and common committed key/certificate container files;
- ordinary and signed-candidate CI fail closed unless JUnit XML proves the required GoreeCloud behavioral test class executed with at least three tests and zero failures/errors;
- CI retains Gradle test/lint console evidence, JUnit XML/results, packaged APK validator output, and machine-readable build evidence;
- build evidence schema v2 records the exact validated checkout commit and maintained GoreeCloud patch line separately from the packaged application version;
- build/release and stable-checklist documentation includes Android OS user/profile isolation as Gallery's application-appropriate multi-user boundary;
- the acceptance workflow uses a version-independent name so its GitHub check identity does not become stale as the maintained patch line evolves.

### Validated

- exact-head validation reconstructed pinned Fossify Gallery and Commons source plus gc.1-gc.8, passed repository/security/source checks, executed `GoreeCloudGalleryPolicyTest` with 3 tests, 0 failures, and 0 errors, completed Android lint, built and validated the `1.0.0-gc.7` FOSS acceptance APK, and retained checksum, licensing, test, lint, APK, and build-identity evidence.

## 1.0.0-gc.7 — Acceptance candidate

### Changed

- corrected the remaining toolbar overflow popup defect at its real owner path in Fossify Commons `MySearchMenu` and embedded `MaterialToolbar`;
- ensured the overflow popup receives the intended GoreeCloud light/dark presentation during construction and search-bar color refresh;
- preserved rounded Gallery media presentation and previously accepted Glaze UI refinements.

### Validated

- real-device screenshots confirmed the previously unreadable top-bar overflow menus are readable on both the main folders view and an opened media folder;
- repository CI reconstructed the exact pinned Fossify Gallery and Commons source, applied the accepted GoreeCloud patch chain, ran source invariants and Android validation, built the FOSS acceptance APK, verified package identity/version/offline boundary/signature/notice, scanned packaged DEX for removed warning text, generated SHA-256, and retained acceptance evidence.

### Known release limitations

- the acceptance APK is not the long-lived stable signing baseline;
- full stable-release real-device permission, destructive-operation, Android user/profile, accessibility, upgrade/recovery, and signing evidence is incomplete;
- repository-level `main` protection remains a separate administrative gate.

## Historical gc.1 through gc.7 transformation line

The gc.1-gc.7 development increments established the dedicated GoreeCloud package identity, branding, offline boundary, Glaze UI palette and surfaces, launcher behavior, removal of inappropriate upstream counterfeit-build messaging from the GoreeCloud build, rounded settings/dialog/media presentation, no-square-thumbnail product behavior, navigation-resource corrections, and the accepted toolbar overflow correction.
