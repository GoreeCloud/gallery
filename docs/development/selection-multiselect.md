# Native Gallery Selection and Multi-Select

Status: Active development; rendered selection and drag-to-select are connected on the Glaze UI V1.4 upgrade branch. Existing-folder Move authorization backend is implemented but the destination picker is not yet connected to the rendered Move action.

## Purpose

The historical GoreeCloud Gallery product model includes selection and multi-select with contextual actions. The native replacement restores that capability without allowing selection state to create or expand media authority.

## Current implemented selection core

`GallerySelectionPolicy` operates only over a caller-supplied current authorized/presented `MediaItem` scope. It provides bounded toggle, explicit select/deselect, range selection, select-all, prune, and resolve operations. A content URI that is not present in the supplied current scope cannot become selected, and selections that become stale when the scope changes are pruned.

`GalleryDragSelectionPolicy` adds a bounded gesture session over that same scope. Long-pressing an unselected item starts select mode; long-pressing an already-selected item starts deselect mode. Items crossed by the gesture receive the same target state and revisiting the same item during one gesture is idempotent rather than toggling it repeatedly.

`GalleryBulkActionPolicy` plans the first non-destructive bulk actions over that bounded selection:

- Share preserves current presentation order and derives the narrowest safe MIME type: an exact common MIME type where possible, `image/*` or `video/*` for mixed subtypes of one media family, and `*/*` only for a mixed image/video selection.
- Favorites chooses Add unless every selected authorized item is already a Favorite, in which case the planned action is Remove.

Focused JVM tests cover scope rejection, toggle/remove behavior, explicit selection, range selection, drag select/deselect semantics, idempotent gesture revisits, select-all bounds, stale-selection pruning, presentation-order preservation, Share MIME planning, and Favorites action planning.

## Current rendered behavior

The Glaze UI V1.4 upgrade branch connects the selection contracts to `GalleryActivity`:

- long-press enters selection and begins a drag-selection gesture;
- dragging across additional media tiles selects or deselects them according to the gesture's initial mode;
- dragging near the top or bottom of the visible library performs bounded edge auto-scroll so a gesture can continue across off-screen media;
- normal tapping still toggles individual items while selection mode is active;
- selected thumbnails use a restrained Glaze accent outline/tint and compact check badge instead of a visually heavy full overlay;
- the header reports the current selection count and contextual guidance;
- the bottom contextual action surface replaces ordinary navigation during selection;
- Share, Favorite/Unfavorite, Delete/Trash, and single-item details retain their existing authority boundaries;
- exit/cancel clears the gesture/session state and restores normal navigation.

Physical-device validation remains required before these rendered interactions can be treated as production accepted.

## Move-to-folder foundation

Gallery now carries Android MediaStore `RELATIVE_PATH` metadata as provider-owned destination information. It does not turn that value, an album id, or an album name into raw filesystem authority.

`GalleryMoveDestinationPolicy` derives eligible existing-folder destinations only from complete, internally consistent album metadata in the current authorized snapshot. It excludes a destination when every selected item already resides there and rejects conflicting bucket/path metadata instead of guessing.

`AndroidMediaMoveRequests` provides the Android mutation boundary for the next rendered Move workflow:

- Android 11 or newer is required for the current implementation;
- the request is limited to the exact canonical selected MediaStore item URIs;
- destination relative paths are canonicalized and reject absolute paths, URIs, traversal segments, and malformed state;
- `MediaStore.createWriteRequest` remains responsible for the user-facing Android authorization step;
- pending move state preserves only the exact canonical item list and destination needed to reconcile activity recreation;
- after Android approval, Gallery updates `MediaStore.MediaColumns.RELATIVE_PATH` for each authorized item and reports complete versus partial failure instead of claiming success unconditionally.

The rendered **Move** action remains intentionally disabled until the Glaze V1.4 destination picker is connected to this backend and passes CI/device acceptance. New-folder creation is a separate follow-on because it needs its own destination naming, path, collision, and partial-failure semantics.

## Mutation boundary

Selection itself never authorizes destructive or organizational media writes. Delete/Trash, Restore/Purge, Move, Copy, Edit, album membership changes, and similar operations must each pass their own Android-authorized mutation path, confirmation/recovery semantics, and applicable Privacy Shield / Wardveil / Everkeep responsibilities.

No network, cloud, cross-profile, broader MediaStore, hidden-media, or raw-filesystem authority is added by selection or drag-selection state.
