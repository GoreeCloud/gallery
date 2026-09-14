# Native Gallery Android-Authorized Move

Status: Development candidate; existing-folder Move is implemented in source and requires representative-device acceptance before promotion.

## Purpose

GoreeCloud Gallery must support useful local organization without converting selection state, album presentation metadata, or app-local state into arbitrary filesystem authority. The current Move implementation therefore remains inside Android MediaStore and operates only on media already present in the current Android-authorized Gallery snapshot.

## Current implemented flow

The first-party native Gallery now supports moving a bounded selection to an **existing local folder** discovered from authoritative MediaStore metadata.

The rendered selection flow is:

1. The user selects one or more currently presented authorized media items.
2. Gallery derives eligible destination folders only from the current authorized MediaStore snapshot.
3. The current folder is excluded when every selected item is already in that destination.
4. Gallery presents the remaining existing folders in a Gallery-controlled Glaze UI V1.4 destination surface.
5. Choosing a destination creates an Android `MediaStore.createWriteRequest(...)` for the exact bounded selected media item URIs.
6. Android owns the write-authorization confirmation surface.
7. Only after Android returns approval does Gallery update each authorized item's `MediaStore.MediaColumns.RELATIVE_PATH` to the validated destination path.
8. Gallery accounts for successful and failed provider updates separately, clears stale thumbnail presentation, and reloads the current authorized MediaStore snapshot.

Canceling or denying Android's write request does not perform the move and is reported as cancellation rather than success.

## Destination authority

Gallery does not treat album IDs or album display names as filesystem authority.

`GalleryMoveDestinationPolicy` accepts a destination only when current authorized items provide a consistent nonblank album ID, album display name, and provider-owned `RELATIVE_PATH`. Conflicting or incomplete destination metadata fails closed instead of being guessed into a folder path.

The Android adapter independently validates the final relative path. Absolute paths, URI-shaped values, traversal segments, malformed paths, and other unsupported destination forms are rejected before a write request can become pending authority.

## Item authority and bounds

Move accepts only canonical Android MediaStore image/video item URIs. Generic Files-table targets, collection-only URIs, file/network URIs, malformed items, and unsupported media identities are rejected.

A single Move request remains bounded to the adapter's accepted maximum item count. Deduplication and canonicalization occur before Android authorization is requested.

Selection itself grants no write authority. It only identifies the current bounded media scope from which a separate Android write request may be created.

## Recreation-safe pending state

While Android owns the confirmation UI, Gallery preserves only the exact already-requested canonical media item URI list and canonical destination `RELATIVE_PATH` required to reconcile the later result after ordinary Activity recreation.

Restoration revalidates that exact state. Missing, malformed, broadened, reordered-by-normalization, duplicated, or otherwise noncanonical saved state is discarded rather than converted into new Move authority.

Gallery also fails closed if destructive Trash/Delete pending state and Move pending state are both restored simultaneously.

## Partial failure behavior

After Android authorization, provider updates are attempted per item. The adapter returns explicit moved and failed counts so Gallery does not represent a partial operation as completely successful.

The current Development UI reports full success, full failure, or mixed success/failure and then reloads MediaStore. A later refinement may preserve exact failed-item selection for retry, but that is not claimed by the current candidate.

## Glaze UI V1.4 presentation

The destination picker is a Gallery-owned GLAZE UI V1.4 / Optical Intelligence surface. It uses the repository-local semantic overlay/container geometry and ordinary interaction-target floor while keeping the Android authorization confirmation platform-owned.

The picker intentionally exposes **existing authorized folders only**. It explicitly states that **New folder is not enabled in this Development build** rather than presenting an action whose creation/path semantics have not yet been accepted.

## Not implemented by this slice

This Move candidate does not establish:

- New-folder creation.
- Copy or duplicate organization.
- Arbitrary filesystem path browsing.
- Cross-profile or cross-user media movement.
- Cloud or GoreeCloud Photos movement.
- Background/silent write authority.
- A Stable or production-qualified Move implementation.

## Acceptance still required

Before Move may be promoted beyond Development, representative-device testing with disposable copied media should cover at least single-photo, multi-photo, video, mixed photo/video, cancellation/denial, same-folder exclusion, destination changes, partial provider failure where reproducible, Activity recreation while Android confirmation is open, permission changes, Android selected-media scope, OEM/profile behavior, post-move album/library refresh, Favorites continuity, and interaction/accessibility review of the V1.4 destination surface.

The application as a whole also remains subject to the broader GoreeCloud Gallery GLAZE UI V1.4, accessibility, Privacy Shield, Wardveil Security, Everkeep, signing/provenance, Release Candidate, production, and Stable qualification gates.
