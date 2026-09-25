# GoreeCloud Gallery — Project Specifications

**Repository:** `GoreeCloud/gallery`  
**Former repository name recorded by the Drive source:** `GoreeCloud/goreecloud-gallery`  
**Project type:** First-party native Android gallery application  
**Repository lifecycle declaration (legacy Contract 0.4):** `development`; canonical Contract 2.0 lifecycle reclassification remains pending and must be evidence-backed  
**Repository visibility:** Public  
**Default branch:** `main`  
**Migration baseline:** `1a241ddb23205f15f673b2968b56fd612014ca87`  
**License:** GPL-3.0  
**Production package target:** `com.goreecloud.gallery`  
**Canonical authority:** This file is the authoritative project specification once accepted on the default branch.

## Migration and precedence

This file consolidates the former root `SPECIFICATIONS.md` with still-applicable requirements and historical context from Google Drive **Project Specification — Gallery.docx** (file ID `1kt0iQrPU0lvZsXw9wmRwhVOjoJ2VH0Ai`).

The Drive source uses the former repository identity `GoreeCloud/goreecloud-gallery`; live GitHub and the canonical repository inventory identify the current repository as `GoreeCloud/gallery`.

Current implementation claims are controlled by accepted repository evidence on `main`, especially `IMPLEMENTED-FEATURES.md`, `PLANNED-FEATURES.md`, `CHANGELOGS.md`, and the current source tree. Historical Drive or transitional-Fossify statements do not override newer first-party native implementation state.

## Status

**Active Development. Not Stable. Production/release acceptance is not established.**

GoreeCloud Gallery is being rebuilt as an original first-party Android application. Historical Fossify reconstruction material remains transitional provenance and migration reference, not authority for new product architecture.

## Product purpose

GoreeCloud Gallery is the first-party GoreeCloud application for offline-first browsing, organizing, viewing, and safely managing device-local photos and videos.

Local media access must remain useful without a GoreeCloud account, network connection, or cloud service.

The long-term product must remain original GoreeCloud-owned software rather than a complete third-party gallery application or fork.

## Product-experience lineage and feature preservation

Historical GoreeCloud Gallery builds and screenshots, repository history, accepted prior interface behavior, and selected Samsung Gallery interaction patterns may be used as migration and visual-comparison references for intended product breadth.

They do not authorize copying Samsung proprietary source, assets, trademarks, or implementation details.

The native replacement must preserve and rebuild useful mature Gallery capabilities rather than collapsing the product into a minimal photo grid. Glaze modernization should improve hierarchy, material, navigation, accessibility, responsiveness, motion, transient surfaces, and polish without deleting valuable established behavior merely to simplify implementation.

## Current native architecture

- `native/core`: framework-independent media, album, trash/recovery, filtering, sorting, selection, mutation, and MediaStore-row domain behavior.
- `native/android-adapter`: bounded Android `ContentResolver` / `MediaStore.Files` adapter.
- `native/app`: first-party Android application using Android-authorized local media access.
- Network-delivered UI resources are not required by the local-library path.
- Android-owned MediaStore metadata and user/profile authorization remain authoritative for local browsing and mutation.

## Local media and album authority

Gallery must not bypass Android user/profile isolation or merge media across profiles without explicit platform authority.

Album identity is accepted only when the platform supplies trustworthy grouping metadata. Media lacking authoritative grouping metadata may remain valid media without being forced into a fabricated album. Conflicting names for one authoritative album identity must fail closed.

Provider failures must not be silently represented as an empty library. Malformed rows must be rejected at the adapter/core boundary.

Filtering, sorting, search, selection, Favorites, and viewer navigation must remain constrained to the current authorized/presented media scope.

## Browsing and organization requirements

The mature target experience includes:
- direct Photos, Albums, Videos, and Settings navigation;
- dense media-first browsing;
- useful date grouping such as Today and calendar dates;
- bounded search over authorized local metadata;
- sorting/grouping controls;
- album covers, names, counts, and album-detail browsing;
- user-controlled view density/layout where appropriate;
- device-local Favorites;
- hidden/excluded-media policy;
- Recycle Bin/Trash;
- organization actions that use Android-authorized provider boundaries; and
- clear distinction between ordinary deletion, Trash, restore, and permanent deletion.

## Viewer requirements

The target viewer must evolve beyond bounded preview into a full media-viewing experience while preserving authorization.

Required direction includes:
- edge-to-edge media presentation;
- restrained top chrome;
- contextual bottom actions;
- Share/Send;
- Favorite;
- Edit;
- Delete/Trash;
- More;
- previous/next navigation and horizontal swipe within the current authorized/presented collection;
- accessible visible navigation alternatives;
- correct orientation and metadata handling;
- bounded resource use; and
- representative-device/OEM/profile validation.

Video playback, animation, full-resolution behavior, and other rich-media functionality remain acceptance-gated until verified.

## Editing requirements

Photo editing should include approved first-party basic transforms such as crop and 90-degree rotation, with additional transforms only when their semantics and safety are defined.

Any delegated Android editor path is an interim bounded integration, not automatic completion of the first-party Gallery editor gate.

Editing acceptance must define:
- save-copy versus in-place semantics;
- metadata/orientation fidelity;
- large-image resource safety;
- cancellation/failure behavior;
- accessibility; and
- representative-device/OEM/profile behavior.

## Selection, sharing, and mutation

Selection authority must remain bounded to the current authorized/presented media scope.

Bulk Share and Favorites must resolve only current authorized items and preserve deterministic behavior.

Destructive operations and Move must use Android-supported user-authorized MediaStore mechanisms. Stale, foreign, malformed, collection-only, non-MediaStore, file, or network mutation targets must fail closed.

Android 10 and earlier must not gain unapproved legacy direct-delete workarounds merely to mimic newer MediaStore mutation APIs.

## Recycle Bin and recovery

Gallery may use Android MediaStore Trash as the authoritative underlying local-media Trash mechanism where supported.

Recycle Bin browsing, Restore, purge/permanent-delete, and multi-select recovery actions must remain explicitly authorized, bounded, and validated across process recreation, permission revocation, provider failures, OEM differences, profile boundaries, and retention/expiry behavior.

## Settings and local portability

Settings may control presentation and approved local behavior such as thumbnail loading priority, included/excluded folder presentation, hidden-item presentation, cache clearing, thumbnail shape, and deletion/Recycle Bin preferences.

Persisted future-facing preferences do not prove the associated behavior is implemented.

Gallery may provide versioned local import/export for Gallery-owned settings or Favorites through Android's document provider. Such exports are not a substitute for complete backup/recovery acceptance.

## Optional GoreeCloud Photos integration

GoreeCloud Photos integration must remain optional and adapter-based.

Local browsing must not require Photos, a GoreeCloud account, or network access. Device-local media must not be uploaded, indexed remotely, or shared without required authorization, privacy controls, and clear user action.

## Privacy, security, identity, and continuity

- **Privacy Shield** governs media permissions, consent, minimization, hidden/excluded media, remote integration, and user-control boundaries.
- **Wardveil Security** governs applicable safe media/file handling, validation, protection, diagnostics, trust, and evidence-backed security states.
- **Everkeep** governs applicable recovery, preservation, portability, backup/restore coordination, and continuity.
- **GoreeCloud Identity** governs future account/device/credential/session/delegated authority where applicable.
- **GoreeCloud Mesh** governs authenticated cross-service coordination where applicable.

Gallery must display provider-owned truth rather than manufacture provider authorization, security, privacy, or recovery state.

## Glaze UI boundary

Gallery must use the latest accepted Stable Glaze UI contract applicable to the product at candidate acceptance time.

Current accepted `main` maps the native Development line to Glaze UI V1.6 / `1.6.0` at the accepted source recorded in repository evidence. This is a source mapping, not whole-application conformance.

Gallery-specific conformance requires fresh rendered, interaction, accessibility, adaptive/form-factor, performance, Human Visual Excellence, representative-device/OEM/profile, fallback/resilience, rollback, release, and production evidence.

Source mappings, labels, or automated tests alone do not establish full conformance.

## Transitional Fossify reconstruction

The repository preserves the earlier Fossify Gallery/Commons reconstruction, gc patch history, licensing notices, and acceptance artifacts as provenance, continuity, migration comparison, regression reference, and historical evidence.

The inherited application is not the long-term architecture. Prior acceptance evidence applies only to that transitional binary and does not establish acceptance of the first-party native replacement.

Applicable GPLv3 and upstream attribution/provenance obligations remain preserved.

## Current accepted implementation boundary

Authoritative `main` currently includes the first-party native Development capabilities recorded in `IMPLEMENTED-FEATURES.md`, including:
- Android-authorized local image/video access;
- bounded MediaStore reads;
- local thumbnail browsing;
- direct Photos/Albums/Videos/Settings navigation;
- adaptive date-grouped grids;
- album browsing;
- local search;
- device-local Favorites;
- bounded full-screen viewer shell;
- read-only Android Share;
- details;
- multi-select and non-destructive bulk actions;
- Android-authorized Delete/Trash and Recycle Bin foundations;
- Restore/Purge foundations;
- Android-authorized Move foundation;
- local settings and portability controls; and
- Glaze UI V1.6 source mapping.

These capabilities remain Development and retain the acceptance gates stated in the lifecycle records.

## Stable blockers

Stable qualification remains blocked until the original native application has accepted evidence for mature browsing/albums, complete viewer/playback behavior, approved editing/sharing, destructive-operation authorization, hidden/excluded-media policy, Android user/profile behavior, accessibility, Glaze UI, applicable platform integrations, signing/provenance, upgrade/recovery, representative physical-device testing, and explicit release approval.

The transitional Fossify-based candidate cannot substitute for native application acceptance gates.

## Maintenance and retirement

Significant changes to product scope, Android media authority, repository identity, native/transitional architecture, platform-system boundaries, Glaze requirements, release state, migration provenance, or product retirement must update this specification and `PROJECT-RECORD.md`.

## Related repository documentation

- [README.md](README.md)
- [PROJECT-RECORD.md](PROJECT-RECORD.md)
- [IMPLEMENTED-FEATURES.md](IMPLEMENTED-FEATURES.md)
- [PLANNED-FEATURES.md](PLANNED-FEATURES.md)
- [CHANGELOGS.md](CHANGELOGS.md)
- [FEATURES.md](FEATURES.md)
- [docs/NATIVE-MIGRATION.md](docs/NATIVE-MIGRATION.md)
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- [docs/PLATFORM_CONFORMANCE.md](docs/PLATFORM_CONFORMANCE.md)
- [docs/GLAZE-UI.md](docs/GLAZE-UI.md)
- [SECURITY.md](SECURITY.md)
- [LICENSE](LICENSE)
