# GoreeCloud Gallery Specifications

## Status

**Active Development. Not Stable. Production/release acceptance is not established.**

GoreeCloud Gallery is being rebuilt as an original first-party Android application. Historical Fossify reconstruction material remains transitional provenance and migration reference, not the authority for new product architecture.

## Current native architecture

- Android package target: `com.goreecloud.gallery`.
- `native/core`: framework-independent media, album, trash/recovery, filter, sort, mutation, and MediaStore-row domain behavior.
- `native/android-adapter`: bounded Android `ContentResolver` / `MediaStore.Files` read adapter.
- `native/app`: first-party Android application shell using Android-authorized media access and local-only thumbnail/preview reads.
- Network-delivered UI resources are not required by the current native local-library path.

## Current local-library contract

- No MediaStore read is attempted without an Android media-access scope that the Gallery policy considers readable.
- Provider reads are bounded by the native Glaze contract's rendered-row limit.
- A provider failure is not silently converted into an empty library.
- Malformed rows are rejected by the adapter/core boundary.
- Type filtering (All / Images / Videos) and Newest / Oldest sorting operate only on the already-authorized in-memory snapshot and do not trigger another provider query.
- Preview Previous/Next navigation remains inside the currently presented filtered/sorted snapshot and re-checks the current load generation and media permission before rendering.
- Video preview is poster/thumbnail only; playback is not claimed.
- Image preview is bounded; full-resolution viewing/editing is not claimed.

## Platform boundaries

- **Glaze UI / Design Center:** current native shell targets Official Stable Glaze UI V1.6 / `1.6.0` at accepted release source `a7180679ea851389e0f3004515f9a25f420e716d`, with presentation-only authority, adaptive gutters, platform light/dark presentation, and a 48dp control target floor. Rendered accessibility, device, performance, and product acceptance remain required before Gallery Stable qualification.
- **Privacy Shield / Privacy Center:** Android media authorization and data minimization are authoritative. Cloud retrieval is not required for local browsing.
- **Wardveil Security / Security Center:** future risky file/media operations must use authoritative Wardveil decisions where applicable; current local thumbnail browsing does not claim a production Wardveil acceptance milestone.
- **Everkeep / Continuity Center:** recovery/preservation workflows remain separate acceptance milestones beyond the current local browse shell.

## Stable blockers

Stable qualification remains blocked on mature album browsing, full viewer behavior, approved playback/edit/share/destructive workflows, hidden/excluded media policy, accessibility/device acceptance, required platform-integration evidence, signed packaging, upgrade/recovery validation, and representative physical-device testing.
