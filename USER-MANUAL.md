# GoreeCloud Gallery User Manual

## Status

This manual describes the **current first-party native `0.8.1-dev` Development candidate**. It includes the previously tested Android Trash/Recycle Bin foundation, first-party viewer/editor work, the current GLAZE UI V1.4 Optical Intelligence revamp, long-press + drag selection, Android-authorized existing-folder Move, and the bounded New Folder Move candidate.

It does **not** describe a Stable or production-approved release. Use **disposable copied photos and videos** when testing Move, Restore, permanent deletion, editor output, or other unfinished write/destructive workflows. Do not use irreplaceable personal media as Development test input.

## Opening the local library

1. Launch **GoreeCloud Gallery**.
2. If media access has not been granted, choose the media-access action and use Android's permission surface to select the access scope you want to provide.
3. Gallery reads only the local MediaStore view allowed by the current Android permission scope.
4. If Android denies the read or the provider is unavailable, Gallery reports that failure instead of presenting it as an empty library.

On supported Android versions, Gallery can operate with selected-media access rather than broad image/video access. Gallery must not label selected-media scope as full-library authority.

## Main destinations

The native Development experience provides direct **Photos**, **Albums**, **Videos**, and **Settings** destinations.

- **Photos** and **Videos** use dense local thumbnail grids grouped by Today, Yesterday, or calendar date.
- **Albums** uses Android-authorized album/folder metadata, includes a device-local Favorites collection when Favorites exist, and on Android 11+ exposes **Recycle Bin** under **Recovery**.
- Search and Newest/Oldest ordering operate only over the currently authorized local snapshot.
- Long-press a visible media tile to enter bounded selection mode.
- Persistent Gallery chrome is inset away from Android status, display-cutout, navigation, and gesture regions; the full-screen viewer remains intentionally edge-to-edge.

## Viewer

Tap a visible photo or video to open the bounded full-screen viewer.

- Use Previous and Next, or the supported horizontal swipe gesture, within the current authorized/presented collection.
- Share hands the current content URI to Android with a read-only URI grant.
- Favorite/Unfavorite changes Gallery's device-local Favorites state.
- More displays available media details.
- Delete on Android 11+ routes through Android's system-owned Trash or permanent-delete confirmation according to the current setting.
- Supported authorized photos can enter the current first-party photo editor candidate.

Image viewing/editor behavior remains Development and still requires broader representative-device/OEM/profile, fidelity, metadata/color, error-path, accessibility, and release acceptance. Video presentation remains poster/thumbnail based; native video playback is separate work.

## Selection and drag-to-select

Long-press a visible photo or video to start selection mode. You can then:

- tap additional visible items to select or deselect them;
- keep the long-press gesture active and drag across media tiles to select/deselect quickly;
- drag near the visible scroll edge to continue bounded selection while Gallery auto-scrolls;
- use Back to leave selection mode.

The drag session stays inside the same current authorized/presented media scope. Selection itself does not grant MediaStore write or filesystem authority.

Current contextual actions include **Share**, **Favorite/Unfavorite**, **Move** when an eligible existing folder or same-source New Folder path is available, Android-authorized **Delete** on supported Android versions, and **More/Details** when exactly one item is selected.

## Move — `0.8.1-dev` Development candidate

On Android 11+ the current candidate can move selected authorized media to an **existing local folder** or, for an eligible single-source selection, to a **New folder** created beneath that source folder.

### Move to an existing folder

1. Select one or more disposable test photos/videos.
2. Choose **Move** from the selection action surface.
3. Gallery shows a GLAZE UI V1.4 Move surface containing eligible existing authorized folders and, when allowed, a New folder action.
4. Choose an existing destination folder.
5. Android should display its system-owned write authorization surface for the exact selected media items.
6. Approve only when you intend to move the disposable test media.
7. Gallery updates the approved items through Android MediaStore and refreshes the current authorized library.

Gallery does not treat album names or album IDs as filesystem paths. Eligible existing destinations require consistent provider-owned MediaStore `RELATIVE_PATH` metadata. Invalid, absolute, URI-shaped, traversal, or malformed destination paths are rejected.

### Move to a New folder

New Folder is deliberately narrower than general folder creation.

It is available only when **every selected item belongs to the same current authoritative source folder**. For example, items selected from `Download/` may be moved into a new `Download/Trip Photos/` child folder.

1. Select disposable media that all belongs to one current folder.
2. Choose **Move**.
3. Choose **New folder**. Gallery identifies the current source folder in the subtitle.
4. Enter a folder name and choose **Create & move**.
5. Gallery validates the name and destination before requesting Android authority.
6. Android should display its write-authorization surface for the exact selected items.
7. After approval, Gallery updates MediaStore and refreshes the library. The new child path should appear as an album/folder when Android exposes it in the refreshed authorized snapshot.

Folder names are rejected when blank, path-like, traversal-like, too long, contain `/`, `\`, colon, NUL/control characters, end in a period/space, or collide with a child destination already visible in the current authorized snapshot. Validation errors remain in the naming surface and do not create a write request.

If selected items come from different source folders, **New folder is intentionally unavailable** because Gallery will not silently choose one source folder as creation authority. Existing-folder Move may still remain available.

### Android authorization and cancellation

Existing-folder and New Folder Move use the same bounded Android authority path. Gallery requests `MediaStore.createWriteRequest(...)` only for the exact selected canonical media item URIs and updates only the validated destination `RELATIVE_PATH` after Android approval.

If Android's authorization surface is denied or canceled, Gallery reports cancellation and does not execute the Move through its pending request.

The current candidate distinguishes full Move success, full failure, and partial provider-update failure. It does not silently report partial completion as complete success.

### Move acceptance boundary

Representative-device testing is still required for single/multiple photos, videos, mixed photo/video selections, existing destinations, New Folder success, invalid/colliding names, mixed-source New Folder unavailability, cancellation/denial, same-folder exclusion, permission/selected-media changes, Activity recreation while confirmation is open, post-move album refresh, Favorites continuity, OEM/profile behavior, accessibility, and provider failure/partial failure where reproducible.

**Copy** is separate work and must not inherit Move authority merely because Android authorized a Move request.

## Delete and Android Trash

With **Settings > Deletion & recovery > Move deleted items to Recycle Bin** enabled, Delete requests Android MediaStore Trash. Android owns the confirmation surface and final mutation. With that setting disabled, Gallery requests Android-confirmed permanent deletion.

Representative-device testing in the earlier Development line verified important Trash and Recycle Bin paths, including single-item and multi-item photo operations and mixed photo/video Trash/Restore behavior. Broader permission-change, provider, process, OEM/profile, and retention edge cases remain Development work.

Android 10 remains fail-closed for the current Android-owned destructive/recovery path; no legacy direct-delete workaround is enabled.

Mutation/write adapters accept only bounded canonical Android MediaStore image/video item URIs. Gallery rejects blank, malformed, file, network, non-MediaStore, generic MediaStore Files, collection-only, and unsupported targets before requesting Android authority.

## Recycle Bin

The first-party **Recycle Bin** is integrated into normal Gallery navigation while keeping Android MediaStore as the authoritative Trash state.

### Opening the Recycle Bin

1. Launch **GoreeCloud Gallery**.
2. Open **Albums**.
3. Under **Recovery**, choose **Recycle Bin**.

Gallery remains the sole launcher entry. The Recycle Bin activity is internal to the application.

### What the Recycle Bin shows

- Android 11+ MediaStore image/video items whose authoritative Trash state is set.
- A bounded local thumbnail grid.
- An explicit notice that **Android controls Trash retention and expiration**.
- Empty, unavailable, media-access-required, and provider-failure states.

Ordinary Photos/Albums/Videos media queries continue to exclude trashed items by default.

### Recycle Bin viewer

When no selection is active, tap a visible trashed-media tile to open the Recycle Bin viewer.

- **Previous / Next** move across the currently loaded trashed-media collection.
- **Restore** asks Android to restore the current item from Trash.
- **Delete permanently** asks Android to permanently delete the current trashed item.
- **More** shows available media details and identifies Android Recycle Bin state.

### Selecting trashed items

Long-press a visible Recycle Bin tile to enter selection mode, then tap additional items to toggle them. The current action surface provides Select all, Restore, Delete permanently, and Cancel.

### Restore

Android owns the Restore confirmation. Gallery refreshes the Recycle Bin only after the returned result and preserves Gallery Favorite URI metadata for restored items when Android retains the same media identity.

### Permanent purge

Android owns permanent-delete confirmation. Confirmed purge removes stale Gallery Favorite URI references for purged items. Cancellation must not be reported as success.

### Existing representative-device evidence

Earlier `0.7.1-dev` testing verified the integrated Albums entry, Trash-to-bin visibility, populated Recycle Bin browsing, stable in-place selection, Android-owned Restore and permanent-delete confirmation surfaces, denial/cancellation for both recovery mutations, successful permanent purge of 28 selected photos plus the empty-bin state, and mixed photo/video Trash-to-Recycle-Bin plus Restore. This historical Development evidence remains useful but does not establish complete current Stable acceptance.

Remaining recovery checks include mixed-media permanent purge where still outstanding, partial-media permission behavior, permission revocation, provider failure, restart/process recreation, OEM/profile behavior, retention/expiry refresh, accessibility, and broader current-candidate regression coverage.

## First-party photo editor

For currently authorized supported photos, the Gallery viewer can open the internal editor.

Current Development transforms include:

- rotate left/right by 90 degrees;
- horizontal flip;
- bounded interactive crop;
- Original, 1:1, 4:3, and 16:9 crop presets;
- Reset;
- **Save copy**.

Save copy is non-destructive: Gallery publishes a new Android MediaStore image and leaves the original untouched. The current editor uses a bounded decoder and fail-closed input/source validation. Ordinary Activity recreation stores only validated transform scalars rather than bitmap bytes or broader media authority.

This is not yet production editing acceptance. Full metadata/EXIF/color-profile preservation, output quality/fidelity, unsupported/oversized/provider-failure/cancellation behavior, process recreation on representative devices, accessibility, OEM/profile behavior, and release qualification remain open.

## Settings

Current active settings include local thumbnail loading priority, included/excluded folder presentation, hidden-item visibility within Android's authorized snapshot, rounded-square thumbnails, Favorites/settings import/export, cache clearing, and Recycle Bin versus permanent-delete choice on supported Android versions.

Playback/GIF preferences remain stored future-facing preferences until their corresponding runtime capabilities are implemented. Automatic empty-folder deletion also remains separately gated; its saved toggle does not mean that empty-folder cleanup is currently implemented.

Protected Photos/password protection is not simulated with insecure app-local credentials; it remains unavailable until supported authentication, protected storage, Privacy Shield, GoreeCloud Identity where applicable, and Wardveil requirements are implemented and accepted.

## Privacy and security

- Local browsing, editing, Move, and Recycle Bin flows do not require cloud retrieval.
- Gallery does not receive authority to read media that Android has not authorized.
- Android MediaStore remains authoritative for device media and Trash state; Gallery does not maintain a second deleted-item database.
- Trash, Restore, permanent-delete, and Move requests remain restricted to exact bounded MediaStore item URIs and Android owns the applicable authorization/confirmation surface.
- Selection itself never grants filesystem or media-write authority.
- Existing-folder Move does not create arbitrary path browsing or cross-profile/cloud authority.
- New Folder Move creates only a validated child `RELATIVE_PATH` beneath the single authoritative current source folder shared by the selection; it does not provide arbitrary filesystem browsing.
- Recycle Bin integration does not request `MANAGE_MEDIA`, `MANAGE_EXTERNAL_STORAGE`, network media authority, or cross-profile access.
- Optional GoreeCloud Photos integration remains a future user-controlled adapter milestone, not a dependency of the local library.

## Current design-system authority

The authoritative Glaze lifecycle registry identifies **GLAZE UI V1.4 / 1.4.0 — Optical Intelligence** as the current Official Stable, consumer-eligible release at exact authority revision `ee057ce9e729296aeaeda182d01db89f52bd66f3`.

The current Gallery candidate maps that authority through native Android semantic/optical roles and a bounded Optical Intelligence chrome pass. Environmental-memory tint is capped, semantic states remain authoritative, and Reduced Transparency / Increased Contrast must retain readable fallbacks.

The repository-local mapping does not independently establish whole-application conformance. Gallery still requires fresh rendered, interaction, TalkBack/switch-access/large-text/RTL, contrast, Reduced Transparency, Increased Contrast, adaptive/form-factor, representative-device/OEM/profile, performance, Human Visual Excellence, rollback, release, and production acceptance before Stable qualification.

## Major capability backlog

Major remaining capability areas include complete existing-folder/New Folder Move device acceptance; Copy organization; native video playback plus autoplay/loop behavior; animated GIF behavior; complete photo-editor fidelity/accessibility/device acceptance and future video editing where approved; metadata editing; richer album creation/rename/reorder/actions; richer grouping/timeline modes; view-density/layout controls; slideshow and other established local presentation actions; broader contextual/overflow/export workflows; secure Private/Protected Photos; fuller hidden/sensitive-media policy; automatic empty-folder cleanup; and additional established first-party Gallery capabilities verified by historical GoreeCloud Gallery evidence.

Separate release gates include GLAZE UI V1.4 application acceptance, accessibility/adaptive/OEM/profile testing, Privacy Shield/Wardveil/Everkeep/Identity/Mesh/Manager integration where applicable, long-lived signing/provenance, upgrade/recovery/rollback validation, Release Candidate qualification, production approval, and Stable qualification.

## Troubleshooting

**No media is shown after permission was granted:** use the available refresh/change-access path. If the provider read fails, Gallery states that the provider read failed rather than assuming there are no files.

**Only some media appears:** Android may have granted selected-media or media-type-limited access. Change Android media access if you want Gallery to see a different authorized subset.

**Move is disabled:** Move requires Android 11+, a nonempty current authorized selection, and either an eligible existing authorized destination or a valid same-source New Folder parent.

**New folder does not appear in Move:** all selected items must resolve to the same current authorized source folder. Mixed-source selections intentionally do not get New Folder authority.

**A folder name is rejected:** correct the validation error shown on the field. Do not enter a path; enter only a child folder name.

**Delete is disabled:** the current Development path requires Android 11 or newer and a currently selected/presented authorized media item.

**The ordinary Gallery no longer shows an item after Trash:** open **Albums > Recovery > Recycle Bin** to check Android MediaStore Trash.

**The Recycle Bin says media access is required:** return to ordinary GoreeCloud Gallery and grant the Android media scope you intend Gallery to use.

**Android confirmation/authorization does not open:** Gallery must not claim success. Stop that Move/Restore/Delete test and report the exact feedback.
