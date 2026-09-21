# GoreeCloud Gallery

GoreeCloud Gallery is GoreeCloud's offline-first Android gallery for device-local photos and videos.

## Status

**Active Development — native replacement in progress. Not Stable.**

The long-term product is an original GoreeCloud-owned Android application built natively from the ground up. The repository still preserves the earlier Fossify-based `1.0.0` acceptance-candidate reconstruction and gc patch history as **transitional provenance, continuity, regression, and migration reference**. That inherited application is not the long-term GoreeCloud Gallery architecture.

New Gallery product behavior should advance the first-party native implementation unless a narrowly documented migration or compatibility need requires work on the transitional line.

See [docs/NATIVE-MIGRATION.md](docs/NATIVE-MIGRATION.md) for the replacement boundary.

## Product boundary

GoreeCloud Gallery is intended to remain:

- offline-first and local-media focused;
- independently installable as `com.goreecloud.gallery` when the native application is packaged;
- governed by Android user/profile isolation and platform-authorized media access;
- free of advertising and unnecessary tracking;
- governed by the current Stable Glaze UI contract;
- integrated substantively with Privacy Shield, Wardveil Security, and Everkeep where those platform responsibilities apply; and
- honest about which capabilities are source foundations, packaged, device-accepted, released, or Stable.

Optional GoreeCloud Photos integration may be added behind explicit adapters and user control. Local browsing must not depend on a GoreeCloud account, network connection, or cloud service.

## First-party native implementation

The repository now contains GoreeCloud-owned native foundations under `native/`.

`native/core` provides framework-independent domain behavior for validated image/video media items, deterministic filtering and sorting, local mutation contracts, trash/recovery behavior, authoritative album metadata, deterministic album summaries, and MediaStore row normalization.

`native/android-adapter` is a compiled Android library bridge over local `ContentResolver` / `MediaStore.Files`. It reads bounded image/video provider rows, fails rather than fabricating an empty library when no cursor is returned, rejects malformed rows, and maps accepted state into the native core model.

The current native application-shell development line adds `native/app`, a first-party Android application target using package ID `com.goreecloud.gallery`. It requires Android media authorization before provider reads, consumes the MediaStore adapter directly, renders a bounded recent-media list with local thumbnails, supports All / Images / Videos filtering and Newest / Oldest sorting over the already-authorized in-memory snapshot, and provides bounded local preview navigation within that presented snapshot. Filter and sort actions do not issue another MediaStore listing request.

The native shell now pins the current Official Stable Glaze UI 1.6.0 source contract at accepted release source `a7180679ea851389e0f3004515f9a25f420e716d`, maps its presentation-only authority through platform controls, preserves a 48dp target floor and adaptive gutters, and uses no network-delivered UI resources. Downstream rendered/accessibility/device acceptance remains separate.

These foundations do **not** yet constitute a released or Stable Gallery application. Image previews remain bounded, video preview is poster-only, and full-resolution viewing, playback, editing, sharing, destructive-operation acceptance, and broader album UX remain separate milestones. See [docs/native-mediastore-adapter.md](docs/native-mediastore-adapter.md) and [docs/native-android-app-shell.md](docs/native-android-app-shell.md).

## Transitional reconstruction line

The repository preserves deterministic Fossify Gallery/Commons reconstruction material and the accepted gc patch chain for historical continuity, provenance, migration comparison, and regression reference.

The pinned historical baseline is:

- Fossify Gallery 1.13.1 — `b28299dc33821eee8d108a9880ce87876cf31443`
- Fossify Commons 6.1.5 — `acfd352df1a1852d17a5f77def8b7ad6e522a5b6`

The prior `1.0.0` candidate and its acceptance evidence remain evidence for that transitional binary only. They are not evidence that the first-party native replacement is complete or Stable. Applicable upstream GPLv3 licensing and provenance obligations remain preserved for that inherited work.

## Platform requirements

Gallery must remain current with the applicable GoreeCloud platform systems:

- **Glaze UI / Design Center** — interface, interaction, accessibility, responsiveness, and design-system conformance.
- **Privacy Shield / Privacy Center** — media permissions, data minimization, privacy controls, consent, and user control.
- **Wardveil Security / Security Center** — protection, validation, safe file/media handling boundaries, diagnostics, and evidence-backed security states.
- **Everkeep / Continuity Center** — recovery, preservation, portability, continuity, and applicable local-media resilience workflows.

These are functional requirements, not decorative labels. Missing or unvalidated required integration blocks Stable qualification.

## Stable-release work

The native application still requires substantial work before Stable qualification, including mature local albums and thumbnail browsing, full viewer/playback behavior, approved editing/sharing flows, destructive-operation authorization, hidden/excluded media policy, Android user/profile acceptance, rendered accessibility and Glaze UI acceptance, Privacy Shield/Wardveil/Everkeep integration evidence, packaging/signing, upgrade/recovery validation, and representative physical-device testing.

The old Fossify-based acceptance candidate is not a shortcut around those native acceptance gates.

## Repository guidance

- [USER-MANUAL.md](USER-MANUAL.md) — current first-party native Development user guidance.
- [SPECIFICATIONS.md](SPECIFICATIONS.md) — current native architecture, authority, and acceptance boundaries.
- [FEATURES.md](FEATURES.md) — implemented Development capabilities and incomplete work.
- [BENEFITS.md](BENEFITS.md) — current and intended product benefits without Stable overclaiming.
- [COMPETITIVE-OBJECTIVES.md](COMPETITIVE-OBJECTIVES.md) — current first-party product objectives.
- [docs/NATIVE-MIGRATION.md](docs/NATIVE-MIGRATION.md) — native replacement and transitional-source boundary.
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — existing architecture/security context; portions describing the inherited application must be read as transitional until updated by native milestones.
- [docs/PLATFORM_CONFORMANCE.md](docs/PLATFORM_CONFORMANCE.md) — platform conformance requirements.
- [docs/GLAZE-UI.md](docs/GLAZE-UI.md) — Gallery-specific Glaze UI acceptance history and requirements.
- [SECURITY.md](SECURITY.md) — vulnerability and security boundary guidance.
- [NOTICE.md](NOTICE.md) — inherited-work licensing and provenance notices.

Canonical GoreeCloud application project specifications are maintained under `GoreeCloud/Projects`, and canonical GoreeCloud changelogs are maintained under `GoreeCloud/Changelogs`.
