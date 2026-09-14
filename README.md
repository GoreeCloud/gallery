# GoreeCloud Gallery

GoreeCloud Gallery is GoreeCloud's offline-first Android gallery for device-local photos and videos.

## Status

**Active Development — native replacement in progress. Current native candidate: `0.8.1-dev` / versionCode 13. Not Stable.**

The long-term product is an original GoreeCloud-owned Android application built natively from the ground up. The repository still preserves the earlier Fossify-based `1.0.0` acceptance-candidate reconstruction and gc patch history as **transitional provenance, continuity, regression, and migration reference**. That inherited application is not the long-term GoreeCloud Gallery architecture.

New Gallery product behavior should advance the first-party native implementation unless a narrowly documented migration or compatibility need requires work on the transitional line.

See [docs/NATIVE-MIGRATION.md](docs/NATIVE-MIGRATION.md) for the replacement boundary.

## Product boundary

GoreeCloud Gallery is intended to remain:

- offline-first and local-media focused;
- independently installable as `com.goreecloud.gallery`;
- governed by Android user/profile isolation and platform-authorized media access and mutation/write consent;
- free of advertising and unnecessary tracking;
- governed by the latest formally published Stable Glaze UI contract;
- integrated substantively with Privacy Shield, Wardveil Security, Everkeep, Manager, Mesh, Identity, and Sync where those platform responsibilities apply; and
- honest about which capabilities are source foundations, packaged, device-accepted, released, or Stable.

Optional GoreeCloud Photos integration may be added behind explicit adapters and user control. Local browsing must not depend on a GoreeCloud account, network connection, or cloud service.

## First-party native implementation

The repository contains GoreeCloud-owned native foundations under `native/`.

`native/core` provides framework-independent domain behavior for validated image/video media items, deterministic filtering and sorting, local mutation contracts, trash/recovery behavior, authoritative album metadata, deterministic album summaries, bounded selection/drag-selection policy, existing-folder Move destination policy, bounded same-source New Folder Move policy, and MediaStore row normalization.

`native/android-adapter` is a compiled Android library bridge over local `ContentResolver` / Android MediaStore. It reads bounded image/video provider rows, fails rather than fabricating an empty library when no cursor is returned, rejects malformed rows, maps accepted state into the native core model, and provides bounded Android-owned authorization bridges for Trash/Delete/Restore and Move.

`native/app` is the first-party Android application target using package ID `com.goreecloud.gallery`. The current `0.8.1-dev` Development line requires Android media authorization before provider reads, consumes the MediaStore adapter directly, provides bounded local photo/video browsing, Albums and Recycle Bin flows, Favorites and settings behavior, bounded full-screen navigation, long-press + drag selection with edge auto-scroll, Android-authorized Trash/Restore/Purge actions, Android-authorized existing-folder and same-source New Folder Move candidates, and a first-party photo-editor candidate with crop, 90-degree rotation, horizontal flip, Reset, and non-destructive Save copy semantics.

## Current Glaze UI authority

The canonical `GoreeCloud/goreecloud-glaze-ui` repository currently publishes **GLAZE UI V1.3 / 1.3.0 — Adaptive Resonance** as Stable. Gallery pins its repository-local native source mapping to exact Glaze authority:

`ff34f232f295c9dcb07e4c681f66d4104d0b9323`

The native Gallery shell now applies a bounded V1.3 Adaptive Resonance pass across search, header controls, permission/status surfaces, bottom navigation/selection chrome, Settings rows, and organizational dialogs while preserving semantic selection/destructive states and the corrected Android system-bar safe areas.

GLAZE UI V1.4 and V1.4.1 material remains proposed future work and is not represented here as the current Stable authority. This V1.3 source mapping is still **source-migration evidence, not Gallery application conformance**. Fresh whole-application rendered, interaction, accessibility, adaptive/form-factor, representative-device, performance, Human Visual Excellence, fallback, rollback, platform-system, release, and production acceptance remains required. See [docs/GLAZE-UI.md](docs/GLAZE-UI.md).

## Android-authorized Move

Gallery's Move candidate stays inside Android MediaStore and never turns a selected album name into arbitrary filesystem authority.

### Existing folder

Existing destinations come only from consistent current-snapshot album metadata plus provider-owned `MediaStore.RELATIVE_PATH`. Gallery requests Android write authorization for the exact selected media item URIs through `MediaStore.createWriteRequest(...)`, and only after Android approval updates the validated destination path.

### New folder

The current `0.8.1-dev` line also includes a bounded **New folder** candidate. It is available only when all selected items belong to one current authoritative source `RELATIVE_PATH`. Gallery accepts a validated child-folder name, rejects unsafe names and known visible collisions, then uses the same exact-item Android write-authorization flow before updating `RELATIVE_PATH`.

A mixed-source selection cannot silently choose a creation parent. Arbitrary filesystem browsing, cross-profile paths, and cloud destinations are not created by this feature. Copy remains separately gated.

Existing-folder and New Folder Move remain Development evidence pending representative-device/OEM/profile/accessibility and failure-path acceptance. See [docs/development/media-move.md](docs/development/media-move.md).

## Photo editor

The current first-party photo-editor candidate is Development evidence only. Representative physical-device/OEM/profile crop/rotate/flip/save-copy flows, source/output orientation fidelity, image-quality and metadata/color behavior, invalid/oversized/provider-failure/cancellation handling, process recreation, accessibility, current-Stable Glaze application acceptance, Platform-System acceptance, signing, Release Candidate qualification, release approval, and Stable qualification remain open.

## Platform Contract

`goreecloud.platform.yaml` declares Gallery against **GoreeCloud Platform Contract 0.3**, including all eight Integral Platform Systems. It deliberately separates:

- the verified current Glaze V1.3 source mapping;
- incomplete whole-application design-system acceptance;
- Development lifecycle state;
- blocked/unaccepted Platform-System integrations; and
- outstanding representative-device, accessibility, recovery, signing, release, and Stable gates.

The pinned Platform Contract 0.3 validator currently contains a stale `1.4.0` Glaze compatibility constant even though the canonical Glaze repository's published Stable release is V1.3.0. Gallery records that mismatch as a blocker and remains Development/nonconformant; it does not use the stale evaluator constant to make a Stable design-system claim. A green manifest workflow is evidence of declaration validity, not acceptance of blocked integrations or release gates.

## Transitional reconstruction line

The repository preserves deterministic Fossify Gallery/Commons reconstruction material and the accepted gc patch chain for historical continuity, provenance, migration comparison, and regression reference.

The pinned historical baseline is:

- Fossify Gallery 1.13.1 — `b28299dc33821eee8d108a9880ce87876cf31443`
- Fossify Commons 6.1.5 — `acfd352df1a1852d17a5f77def8b7ad6e522a5b6`

The prior `1.0.0` candidate and its acceptance evidence remain evidence for that transitional binary only. They are not evidence that the first-party native replacement is complete or Stable. Applicable upstream GPLv3 licensing and provenance obligations remain preserved for that inherited work.

## Platform requirements

Gallery must remain current with the applicable GoreeCloud platform systems:

- **Glaze UI / Design Center** — interface, interaction, accessibility, responsiveness, material behavior, and design-system conformance.
- **Privacy Shield / Privacy Center** — media permissions, data minimization, privacy controls, consent, and user control.
- **Wardveil Security / Security Center** — protection, validation, safe file/media handling boundaries, diagnostics, and evidence-backed security states.
- **Everkeep / Continuity Center** — recovery, preservation, portability, continuity, and applicable Gallery-owned state resilience.
- **Manager** — accepted platform visibility and administrative integration where required.
- **GoreeCloud Mesh** — authenticated cross-service registration/capability publication where required.
- **GoreeCloud Identity** — any future account, device, session, Photos-account, or delegated-authority behavior.
- **GoreeCloud Sync** — any future accepted cross-device synchronization of Gallery-owned state or Photos continuity; local Gallery browsing must remain useful without it.

These are functional requirements, not decorative labels. Missing or unvalidated required integration blocks Stable qualification.

## Stable-release work

The native application still requires substantial work before Stable qualification, including representative-device acceptance of existing-folder and New Folder Move, Copy organization design, native video playback, mature media/viewer/editor behavior, destructive-operation edge cases, hidden/protected/excluded media policy, Android user/profile acceptance, rendered accessibility and current-Stable Glaze UI acceptance, applicable Privacy Shield/Wardveil/Everkeep/Manager/Mesh/Identity/Sync integration evidence, packaging/signing, upgrade/recovery validation, and representative physical-device testing.

The old Fossify-based acceptance candidate is not a shortcut around those native acceptance gates.

## Repository guidance

- [USER-MANUAL.md](USER-MANUAL.md) — current first-party native Development user guidance.
- [SPECIFICATIONS.md](SPECIFICATIONS.md) — current native architecture, authority, and acceptance boundaries.
- [FEATURES.md](FEATURES.md) — implemented Development capabilities and incomplete work.
- [FEATURE-ROADMAP.md](FEATURE-ROADMAP.md) — current Development sequencing and lifecycle obligations.
- [BENEFITS.md](BENEFITS.md) — current and intended product benefits without Stable overclaiming.
- [COMPETITIVE-OBJECTIVES.md](COMPETITIVE-OBJECTIVES.md) — current first-party product objectives.
- [goreecloud.platform.yaml](goreecloud.platform.yaml) — machine-readable Development, Platform Contract 0.3 compatibility, Platform-System, and conformance state.
- [docs/NATIVE-MIGRATION.md](docs/NATIVE-MIGRATION.md) — native replacement and transitional-source boundary.
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — architecture/security context; inherited-application sections remain transitional/historical unless superseded by native milestones.
- [docs/PLATFORM_CONFORMANCE.md](docs/PLATFORM_CONFORMANCE.md) — platform conformance requirements.
- [docs/GLAZE-UI.md](docs/GLAZE-UI.md) — current Gallery-specific V1.3 source mapping and application-acceptance boundary.
- [docs/development/media-move.md](docs/development/media-move.md) — current Android-authorized existing-folder and New Folder Move authority/acceptance boundary.
- [SECURITY.md](SECURITY.md) — vulnerability and security boundary guidance.
- [NOTICE.md](NOTICE.md) — inherited-work licensing and provenance notices.

Canonical GoreeCloud application project specifications are maintained under `GoreeCloud/Projects`, and canonical GoreeCloud changelogs are maintained under `GoreeCloud/Changelogs`.
