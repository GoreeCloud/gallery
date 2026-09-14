# GoreeCloud Gallery Specifications

## Status

**Active Development. Current native candidate: `0.8.0-dev` / versionCode 12. Not Stable. Production/release acceptance is not established.**

GoreeCloud Gallery is being rebuilt as an original first-party Android application. Historical Fossify reconstruction material remains transitional provenance and migration reference, not the authority for new product architecture.

## Current native architecture

- Android package target: `com.goreecloud.gallery`.
- Minimum Android API: 29; current compile/target API: 36.
- `native/core`: framework-independent media, album, trash/recovery, selection/drag-selection, Move-destination, filter, sort, mutation, and MediaStore-row domain behavior.
- `native/android-adapter`: bounded Android `ContentResolver` / MediaStore read adapter plus exact Android-owned authorization bridges for destructive mutations and existing-folder Move.
- `native/app`: first-party Android application using Android-authorized media access, local thumbnail/viewer/editor behavior, Recycle Bin, drag selection, and the current Android-authorized existing-folder Move candidate.
- No `INTERNET` permission or network-delivered UI resource is required by the local Gallery path.

## Current local-library contract

- No MediaStore read is attempted without an Android media-access scope that the Gallery policy considers readable.
- Provider reads are bounded by the native Glaze contract's rendered-row limit.
- A provider failure is not silently converted into an empty library.
- Malformed rows are rejected by the adapter/core boundary.
- Photos, Albums, Videos, Favorites, search, Newest/Oldest presentation, folder visibility controls, selection, and viewer navigation remain bounded to the current Android-authorized snapshot.
- Android 14+ selected-media scope is represented distinctly rather than masquerading as full-library authority.
- Full-screen navigation remains inside the currently presented authorized collection and rechecks load generation and permission before rendering.
- Video presentation remains poster/thumbnail only; native playback is not yet claimed.
- The first-party photo editor supports the current bounded Development transform/save-copy set, but physical-device/OEM/profile/fidelity/accessibility/release acceptance is incomplete.

## Selection and Move contract

- Selection grants no filesystem or MediaStore write authority by itself.
- Long-press starts bounded selection; drag selection remains constrained to the supplied current authorized/presented scope and supports edge auto-scroll.
- Existing Move destinations are derived only from consistent current-snapshot album metadata plus provider-owned MediaStore `RELATIVE_PATH`.
- Album IDs and display names are presentation/grouping metadata and are not treated as filesystem paths.
- Android 11+ Move authorization uses `MediaStore.createWriteRequest(...)` for the exact bounded canonical selected image/video item URIs.
- Only after Android approval does Gallery update the validated destination `RELATIVE_PATH` per item.
- Absolute paths, URI-shaped destinations, traversal segments, malformed paths, generic Files-table URIs, collection-only URIs, file/network URIs, and other unsupported targets fail closed.
- Pending Move state preserves only the exact canonical already-requested URI scope and destination path required for ordinary Activity recreation.
- Complete success, full failure, and partial provider-update failure are distinguished; Gallery does not report a partial Move as fully successful.
- New-folder creation and Copy are not enabled by the current `0.8.0-dev` Move slice and remain separately gated.

## Delete, Trash, and recovery contract

- Android 11+ Trash/Delete/Restore/Purge requests remain bounded to canonical MediaStore item URIs and use Android-owned confirmation surfaces.
- Recycle Bin is backed by authoritative Android MediaStore Trash state rather than a second Gallery trash database.
- Restore preserves Gallery Favorite URI metadata; confirmed permanent deletion removes stale Favorite URI references.
- Android 10 remains fail-closed for the current destructive/recovery mutation path; no legacy direct-delete workaround is claimed.

## GLAZE UI V1.4 contract

- Current repository-local source target: **GLAZE UI V1.4 / 1.4.0 — Optical Intelligence**.
- Exact authority revision: `ee057ce9e729296aeaeda182d01db89f52bd66f3`.
- The native mapping consumes governed semantic spacing, shape, motion, ordinary 48dp target-size, and adaptive-gutter roles while retaining Gallery-specific media-density composition.
- Optical adaptation must remain bounded and must collapse safely to solid/readable surfaces for Reduced Transparency and Increased Contrast requirements.
- The version/source mapping does not establish whole-application Glaze conformance, production eligibility, Release Candidate status, or Stable status.

## GoreeCloud Platform Contract 0.3

- Gallery declares all eight Integral Platform Systems through `goreecloud.platform.yaml` under Platform Contract `0.3`.
- The exact central validation authority pinned by the Gallery workflow recognizes GLAZE UI `1.4.0` as the current Stable target.
- A valid Platform Contract declaration is configuration/governance evidence only; it does not convert blocked integrations into accepted runtime integration.
- Gallery remains globally `nonconformant` while required platform-system and lifecycle evidence remains incomplete.

## Platform boundaries

- **Glaze UI / Design Center:** V1.4 source migration is present; whole-application rendered/accessibility/adaptive/device/performance/Human Visual Excellence/optical-fallback/release acceptance remains required.
- **Privacy Shield / Privacy Center:** Android media authorization, data minimization, purpose-limited local operations, and fail-closed authority remain current source behavior; accepted production Privacy Shield integration is not established.
- **Wardveil Security / Security Center:** bounded media/editor/mutation safeguards exist, but accepted Wardveil runtime integration and production validation are not established.
- **Everkeep / Continuity Center:** Gallery-owned settings/Favorites portability and Android-owned Trash recovery exist; accepted complete Everkeep integration and recovery evidence are not established.
- **Manager, Mesh, Identity, Sync:** applicable responsibilities remain blocked pending accepted integration evidence. Local browsing must not depend on those systems or fabricate a positive integration status. Any future cross-device Gallery-owned-state or Photos continuity path must receive separate Sync authority and acceptance.

## Stable blockers

Stable qualification remains blocked on representative-device acceptance of Move and remaining destructive/recovery cases; New folder/Copy organization; native video playback; complete viewer/editor fidelity and failure-path acceptance; secure Protected Photos/hidden-media policy; whole-application GLAZE UI V1.4 accessibility/adaptive/optical/device acceptance; Android user/profile/OEM acceptance; accepted Privacy Shield/Wardveil/Everkeep/Manager/Mesh/Identity/Sync integration where applicable; protected signing/provenance; upgrade/recovery/rollback evidence; Release Candidate qualification; production approval; and final release evidence.
