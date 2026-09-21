# GoreeCloud Gallery User Manual

## Status

This manual describes the **current first-party native Development experience**, including the physically verified `0.6.2-dev` Android Trash/selection base and the `0.7.1-dev` Recycle Bin integration candidate. It does not describe a Stable or production-approved release.

The initial `0.6.0-dev` destructive-operation build is superseded. `0.6.1-dev` corrected the MediaStore item URI path, and representative-device testing subsequently verified single-item Trash plus tested 26-item and 10-item multi-select Trash operations. `0.6.2-dev` also physically corrected the prior select/deselect screen flash.

Use **disposable copied photos and videos** when testing Restore, permanent deletion, or other unfinished destructive workflows. Do not use irreplaceable personal media as test input.

## Opening the local library

1. Launch **GoreeCloud Gallery**.
2. If media access has not been granted, choose the media-access action and use Android's permission surface to select the access scope you want to provide.
3. Gallery reads only the local MediaStore view allowed by the current Android permission scope.
4. If Android denies the read or the provider is unavailable, Gallery reports that failure instead of presenting it as an empty library.

On supported Android versions, the app may operate with selected-media access rather than broad image/video access.

## Main destinations

The current native Development experience provides direct **Photos**, **Albums**, **Videos**, and **Settings** destinations.

- Photos and Videos use dense local thumbnail grids grouped by Today, Yesterday, or calendar date.
- Albums uses Android-authorized album metadata, includes a device-local Favorites collection when Favorites exist, and on Android 11+ exposes **Recycle Bin** under a dedicated **Recovery** section.
- Search and Newest/Oldest ordering operate only over the currently authorized local snapshot.
- Long-press a media tile to enter multi-select mode.

## Viewer

Tap a visible photo or video to open the bounded full-screen viewer.

- Use Previous and Next within the current authorized/presented collection.
- Share hands the current content URI to Android with a read-only URI grant.
- Favorite/Unfavorite changes Gallery's device-local Favorites state.
- More displays available media details.
- Delete on Android 11+ routes through Android's system-owned Trash or permanent-delete confirmation according to the current setting.
- Edit remains unavailable until an approved editing workflow is implemented.

Image viewing is still a Development viewer path rather than fully accepted full-resolution viewing. Video presentation remains poster/thumbnail based; native playback is separate work.

## Selection and bulk actions

Long-press a visible media tile to enter selection mode, then tap additional items to add or remove them.

Current actions include Share, Favorite/Unfavorite, Android-authorized Delete on Android 11+, and More/Details when exactly one item is selected. Move remains disabled until an approved Android-authorized organization path exists.

The `0.6.2-dev` in-place selection renderer is physically verified on the representative device: selecting and deselecting no longer causes the previous whole-screen flash.

## Delete and Android Trash

With **Settings > Deletion & recovery > Move deleted items to Recycle Bin** enabled, Delete requests Android MediaStore Trash. Android owns the confirmation surface and final mutation. With that setting disabled, Gallery requests Android-confirmed permanent deletion.

Representative-device testing has verified the current corrected Trash path for a single item and tested 26-item and 10-item multi-select operations. Broader cancellation, permanent-delete, permission-change, OEM/profile, and post-mutation edge-case acceptance remains Development work.

Android 10 remains fail-closed for this path; no legacy direct-delete workaround is enabled.

A single mutation is bounded to at most 100 unique Android MediaStore image/video item URIs. Gallery rejects blank, malformed, file, network, non-MediaStore, generic MediaStore Files, collection-only, and nonnumeric-item targets at the mutation adapter boundary.

## Recycle Bin — 0.7.1-dev Development candidate

The `0.7.1-dev` candidate integrates the first-party **Recycle Bin** into normal Gallery navigation while keeping Android MediaStore as the authoritative Trash state.

### Opening the Recycle Bin

1. Launch **GoreeCloud Gallery**.
2. Open **Albums**.
3. Under **Recovery**, choose **Recycle Bin**.

The temporary second launcher icon used by the first `0.7.0-dev` Restore/Purge test slice has been removed. `RecycleBinActivity` is now internal to the application and normal Gallery is the sole launcher entry.

### What the Recycle Bin shows

- Android 11+ MediaStore image/video items whose authoritative Trash state is set.
- A bounded local thumbnail grid.
- An explicit notice that **Android controls Trash retention and expiration**. Gallery does not promise indefinite retention.
- Empty, unavailable, media-access-required, and provider-failure states.

Ordinary Photos/Albums/Videos media queries continue to exclude trashed items by default.

### Recycle Bin viewer

When no selection is active, tap a visible trashed-media tile to open the Recycle Bin viewer.

- Use **Previous** and **Next** across the currently loaded trashed-media collection.
- **Restore** asks Android to restore the current item from Trash.
- **Delete permanently** asks Android to permanently delete the current trashed item.
- **More** shows the available media details and explicitly identifies the item as being in Android Recycle Bin state.

The viewer uses a larger bounded local thumbnail request for inspection. It is not a claim of accepted full-resolution photo viewing or native video playback.

### Selecting trashed items

Long-press a visible Recycle Bin tile to enter selection mode, then tap additional items to toggle them. Selection updates resident tiles in place. The current action surface provides:

- **Select all** — select the currently loaded trashed items.
- **Restore** — ask Android to restore the selected items from Trash.
- **Delete permanently** — ask Android to permanently delete the selected trashed items.
- **Cancel** — clear the current selection.

### Restore

1. Put a **disposable copied** photo/video into Trash using normal Gallery Delete with Recycle Bin enabled.
2. Open **Albums > Recovery > Recycle Bin**.
3. Confirm that the trashed item appears.
4. Either open the item and choose **Restore**, or long-press/select it and choose **Restore** from the selection action surface.
5. Android should display its system-owned restore confirmation.
6. Approve only the disposable test item.
7. Gallery should refresh the Recycle Bin after Android reports success.
8. Return to the ordinary Gallery and verify the restored item returns to the authorized library.

Restore deliberately preserves Gallery Favorite URI metadata so a restored favorite can remain a favorite when Android retains the same item identity.

### Permanent purge

1. Open or select only disposable trashed media.
2. Choose **Delete permanently**.
3. Android should display its system-owned permanent-delete confirmation.
4. Approve only when you intend to permanently remove the test media.
5. Gallery should refresh the Recycle Bin and the purged item should no longer appear.

Confirmed purge removes stale Gallery Favorite URI references for those items.

### Cancel behavior

Canceling Android's Restore or permanent-delete confirmation must not be treated as success. The media should remain in the Recycle Bin unless Android or another application changed it independently. If cancellation occurs from the viewer, the viewer remains available for the current item.

### Recycle Bin acceptance boundary

The rendered `0.7.1-dev` Recycle Bin remains a Development candidate, but representative-device testing now verifies the integrated Albums entry, Trash-to-bin visibility, populated Recycle Bin browsing, stable in-place selection, Android-owned Restore and permanent-delete confirmation surfaces, denial/cancellation for both recovery mutations, successful permanent purge of 28 selected photos, the post-purge empty-bin state, and mixed photo/video Trash-to-Recycle-Bin plus Restore. In the mixed-media test, Gallery recognized the test video in Videos, Android separately confirmed moving the video and a photo to Trash, both appeared in the Recycle Bin, Android presented a `move 2 items out of trash` confirmation for the mixed selection, Gallery reported `Restored 2 items`, and the video returned to Videos while the photo returned to the ordinary library. In the 28-photo purge test, Android presented its system-owned confirmation, Gallery reported `Deleted 28 items permanently`, refreshed the Recycle Bin to 0 items, and rendered the intended `Recycle Bin is empty` state. Remaining required device testing includes mixed photo/video permanent-purge behavior, partial-media permission behavior, permission revocation, provider failure, restart/process recreation, OEM/profile behavior, and retention/expiry refresh. This evidence does not establish Stable or production acceptance.

## Settings

Current active settings include local thumbnail loading priority, included/excluded folder presentation, hidden-item visibility within Android's authorized snapshot, rounded-square thumbnails, Favorites/settings import/export, cache clearing, and the Recycle Bin versus permanent-delete choice on supported Android versions.

Playback/GIF preferences remain stored future-facing preferences until their corresponding runtime capabilities are implemented. Automatic empty-folder deletion also remains separately gated; its saved toggle does not mean that empty-folder cleanup is currently implemented.

Protected Photos/password protection is not simulated with insecure app-local credentials; it remains unavailable until supported authentication, protected storage, Privacy Shield, GoreeCloud Identity where applicable, and Wardveil requirements are implemented.

## Privacy and security

- Local browsing and the Recycle Bin do not require cloud retrieval.
- Gallery does not receive authority to read media that Android has not authorized.
- Android MediaStore remains the authoritative Trash state; Gallery does not maintain a second deleted-item database.
- Trash, Restore, and permanent-delete requests are restricted to media-specific MediaStore item URIs and Android owns the final confirmation surface.
- Selection itself never grants filesystem or media-write authority.
- Recycle Bin integration does not request `MANAGE_MEDIA`, `MANAGE_EXTERNAL_STORAGE`, network media authority, or cross-profile access.
- Optional GoreeCloud Photos integration remains a future user-controlled adapter milestone, not a dependency of the current local library.

## Current design-system authority

The current shared authority identifies **GLAZE UI V1.6 / 1.6.0** as Official Stable, with accepted release source `a7180679ea851389e0f3004515f9a25f420e716d`. Gallery's first-party native source contract pins that authority, while complete rendered, accessibility, adaptive-device, Human Visual Excellence, and production conformance remain separate acceptance gates.

## Major capability backlog

The native restoration still includes more work than the earlier rough "11 features" estimate implied. Distinct remaining capability areas include full physical Recycle Bin acceptance; full-resolution image viewing and physical orientation acceptance; native video playback plus autoplay/loop behavior; animated GIF behavior; approved photo/video editing; approved metadata editing; Move and Copy; broader selection tools where appropriate; album creation, rename, reorder and richer album actions; richer grouping/timeline modes; view-density/layout controls; slideshow and other established local presentation actions; broader contextual/overflow actions; broader export/share workflows where required; secure Private/Protected Photos; fuller hidden/sensitive-media policy; automatic empty-folder cleanup; and any additional established first-party Gallery capability verified by historical GoreeCloud Gallery evidence.

Separate release gates include GLAZE UI V1.6 application acceptance, accessibility/adaptive/OEM/profile testing, Privacy Shield/Wardveil/Everkeep/Identity/Mesh integration where applicable, long-lived signing, upgrade/recovery validation, production approval, and Stable qualification.

## Troubleshooting

**No media is shown after permission was granted:** use the available refresh/change-access path. If the provider read fails, Gallery states that the provider read failed rather than assuming there are no files.

**Only some media appears:** Android may have granted selected-media or media-type-limited access. Change Android media access if you want Gallery to see a different authorized subset.

**Delete is disabled:** the current Development path requires Android 11 or newer and a currently selected/presented authorized media item.

**The ordinary Gallery no longer shows an item after Trash:** open **Albums > Recovery > Recycle Bin** to check Android MediaStore Trash.

**The Recycle Bin says media access is required:** return to ordinary GoreeCloud Gallery and grant the Android media scope you intend Gallery to use.

**Android confirmation does not open:** Gallery must not claim success. Stop that Restore/Delete test and report the exact feedback.
