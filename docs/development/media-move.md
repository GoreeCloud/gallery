# Native Gallery Android-Authorized Move

Status: Development candidate; existing-folder and same-source New Folder Move are implemented in source and require representative-device acceptance before promotion.

## Purpose

GoreeCloud Gallery must support useful local organization without converting selection state, album presentation metadata, or app-local state into arbitrary filesystem authority. Move therefore remains inside Android MediaStore and operates only on media in the current Android-authorized Gallery snapshot.

## Current implemented flow

The first-party native Gallery supports two destination modes:

- **Existing folder:** chosen from eligible current MediaStore folders already represented by provider-owned metadata.
- **New folder:** a validated child folder created beneath the single provider-owned source `RELATIVE_PATH` shared by all selected items.

The rendered flow is:

1. The user selects one or more currently presented authorized media items.
2. Gallery resolves that exact selection against the current authorized/presented scope.
3. Existing destinations are derived only from consistent current-snapshot album metadata plus provider-owned `RELATIVE_PATH`.
4. If every selected item belongs to one authoritative source `RELATIVE_PATH`, Gallery also offers **New folder** beneath that source folder.
5. For New folder, Gallery validates the requested folder name and builds only a child relative path beneath the accepted parent.
6. Choosing either destination mode creates Android `MediaStore.createWriteRequest(...)` authorization for the exact bounded selected media item URIs.
7. Android owns the write-authorization confirmation surface.
8. Only after Android returns approval does Gallery update each authorized item's `MediaStore.MediaColumns.RELATIVE_PATH` to the validated destination path.
9. Gallery counts successful and failed provider updates separately, clears stale thumbnail presentation, and reloads the current authorized MediaStore snapshot.

Canceling or denying Android's write request performs no move and is reported as cancellation rather than success.

## Existing-destination authority

Gallery does not treat album IDs or album display names as filesystem authority.

`GalleryMoveDestinationPolicy` accepts an existing destination only when current authorized items provide a consistent nonblank album ID, display name, and provider-owned `RELATIVE_PATH`. Conflicting or incomplete metadata fails closed. A selection containing unresolved/foreign URIs also fails closed instead of silently broadening authority.

## New Folder authority

`GalleryNewFolderMovePolicy` deliberately provides narrower authority than arbitrary create-folder behavior.

A New Folder destination is available only when:

- every selected URI resolves to a current authorized/presented `MediaItem`;
- every selected item provides a valid provider-owned `RELATIVE_PATH`; and
- all selected items resolve to the same canonical source path.

Gallery then accepts only a folder **name**, not a raw path. The name is trimmed and must be nonblank, bounded in length, not `.` or `..`, contain no `/`, `\`, colon, NUL/control characters, and not end in a period or space. If the current authorized snapshot already exposes the same child path, Gallery rejects the known collision and directs the user to the existing destination instead.

Example: media selected from `Download/` may create `Download/Trip Photos/`. Gallery does not offer a text field for `/sdcard/...`, another root, another profile, a URI, or a cloud destination.

A mixed-source selection does not silently choose one parent. Existing-folder Move may remain available, but New Folder is withheld with an explanatory UI state.

## Android adapter validation

The Android adapter independently canonicalizes and validates the final relative path for both existing and New Folder destinations. Absolute paths, URI-shaped values, traversal segments, malformed paths, and unsupported targets are rejected before pending write authority is established.

Move accepts only canonical Android MediaStore image/video item URIs. Generic Files-table targets, collection-only URIs, file/network URIs, malformed items, and unsupported media identities are rejected.

Selection itself grants no write authority. It only identifies the bounded scope from which a separate Android write request may be created.

## Recreation-safe pending state

While Android owns the confirmation UI, Gallery preserves only the exact already-requested canonical media item URI list and canonical destination `RELATIVE_PATH` required to reconcile the later result after ordinary Activity recreation.

Restoration revalidates that exact state. Missing, malformed, broadened, reordered-by-normalization, duplicated, or otherwise noncanonical saved state is discarded rather than converted into new Move authority. Gallery also fails closed if destructive Trash/Delete pending state and Move pending state are both restored simultaneously.

## Partial failure behavior

After Android authorization, provider updates are attempted per item. The adapter returns explicit moved and failed counts so Gallery does not represent a partial operation as completely successful.

The Development UI reports full success, full failure, or mixed success/failure and reloads MediaStore. A future refinement may preserve exact failed-item selection for retry, but that is not claimed here.

## Glaze UI V1.4 presentation

The destination and New Folder naming surfaces are Gallery-owned GLAZE UI V1.4 Optical Intelligence overlays. They use the repository-local overlay/raised/control roles, bounded environmental-memory tint, and ordinary target-size floor while keeping Android's actual write-confirmation surface platform-owned. Reduced Transparency and Increased Contrast must retain readable solid fallbacks.

The New Folder action is shown only when the selection has a valid single source parent. Naming errors are surfaced on the input field rather than being converted into a generic filesystem request.

## Not implemented by this slice

This Move candidate does not establish:

- Copy or duplicate organization.
- Arbitrary filesystem path browsing or arbitrary folder creation.
- Cross-profile or cross-user media movement.
- Cloud or GoreeCloud Photos movement.
- Background/silent write authority.
- Stable or production-qualified Move behavior.

## Acceptance still required

Before Move may be promoted beyond Development, representative-device testing with disposable copied media should cover at least:

- single-photo, multi-photo, video, and mixed photo/video existing-folder moves;
- New Folder success inside an eligible source folder;
- blank, invalid, and colliding New Folder names;
- mixed-source selections where New Folder must remain unavailable;
- Android confirmation cancellation/denial;
- Activity recreation while Android confirmation is open;
- permission and selected-media-scope changes;
- partial provider failure where reproducible;
- post-move album/library refresh and Favorites continuity;
- representative OEM/profile behavior;
- TalkBack, large text, and interaction review of both destination surfaces.

The application as a whole remains subject to current Official Stable GLAZE UI V1.4, accessibility, Privacy Shield, Wardveil Security, Everkeep, signing/provenance, Release Candidate, production, and Stable qualification gates.
