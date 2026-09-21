# GoreeCloud Gallery Features

## Product identity and restoration target

GoreeCloud Gallery is an original GoreeCloud-owned native Android application whose established product experience is heavily inspired by Samsung Gallery. The native replacement must preserve the useful mature GoreeCloud Gallery feature model instead of narrowing the product into a minimal photo-grid application.

Historical GoreeCloud Gallery screenshots, prior Gallery behavior, repository history, and applicable Samsung Gallery interaction references are migration and visual-comparison inputs. They are not authorization to copy Samsung proprietary source code, assets, trademarks, or implementation details.

The target is to recover the established GoreeCloud Gallery information architecture, browsing model, album behavior, viewer interactions, contextual actions, organization patterns, and first-party feature breadth, then revamp GoreeCloud-controlled presentation under the official **GLAZE UI V1.6** authority. Gallery-specific production visual/accessibility acceptance remains separate from source adoption.

## Implemented in the first-party Development line

- Android-authorized local image/video access with fail-closed permission gating and explicit selected-media/partial-access handling.
- Bounded MediaStore image/video reads through the compiled Android adapter.
- Validated media-item and MediaStore-row domain models.
- Local thumbnails with bounded in-memory caching and no cloud dependency.
- Direct Photos / Albums / Videos / Settings navigation in the current `0.7.1-dev` candidate.
- Dense adaptive Photos and Videos grids grouped into Today / Yesterday / calendar-date sections.
- Newest / Oldest ordering over the current authorized snapshot.
- Local search over authorized display names and album names without an additional provider query.
- Dedicated Albums browsing with authoritative album covers, names, counts, adaptive cover layout, and bounded album-detail browsing.
- Device-local Favorites backed only by Gallery app-local state; favorite/unfavorite is available from the viewer and authorized Favorites appear as a dedicated collection.
- A full-screen bounded media viewer shell with Previous / Next navigation, restrained top chrome, and a bottom action surface. Viewer navigation uses the complete current authorized/presented collection rather than one date group.
- Android Share handoff for the currently authorized media content URI using read-only URI grant semantics.
- Viewer details for type, album, date, dimensions, duration, and size when available.
- **Rendered long-press selection and multi-select:** long-pressing a visible media tile enters selection mode; subsequent taps toggle items. Selected thumbnails receive an accent wash and check marker, the header shows the selected count, Back exits selection, and ordinary bottom navigation is replaced by a contextual action capsule.
- **Bulk Share:** selected authorized media can be shared using Android `ACTION_SEND` for one item or `ACTION_SEND_MULTIPLE` for multiple items, with read-only URI grants and MIME planning derived only from the bounded current selection.
- **Bulk Favorite / Unfavorite:** selection mode adds all selected authorized items to Favorites unless every selected item is already a Favorite, in which case it removes them. Favorites remain Gallery app-local state.
- **Selection Details:** the contextual More action exposes media details when exactly one item is selected.
- **Android-authorized Delete / Trash candidate:** on Android 11 and newer, Delete is enabled in the viewer and selection mode. Gallery submits only bounded current MediaStore URIs to Android's system confirmation flow. With **Move deleted items to Recycle Bin** enabled, Android receives a `MediaStore.createTrashRequest(...)`; with the setting disabled, Android receives a `MediaStore.createDeleteRequest(...)` for confirmed permanent deletion. Gallery refreshes its authorized snapshot after a successful system result. This is Development behavior pending complete physical-device destructive-operation acceptance, not Stable qualification.
- **First-party Recycle Bin candidate:** on Android 11+, Albums now exposes a dedicated **Recovery > Recycle Bin** entry backed by Android MediaStore Trash. The ordinary Gallery launcher is the sole launcher entry; the earlier temporary Recycle Bin launcher has been removed.
- **Recycle Bin browsing and viewer:** Gallery can enumerate bounded MediaStore items whose authoritative Trash state is set, render an in-place-selectable grid, open a dedicated trashed-item viewer with Previous / Next, Restore, Delete permanently, and More, and disclose that Android controls actual Trash retention/expiration.
- **Recycle Bin Restore / Purge:** both single-item viewer actions and bounded multi-select actions use Android-owned confirmation. Restore uses `MediaStore.createTrashRequest(..., false)`; permanent purge uses `MediaStore.createDeleteRequest(...)`. Restore preserves Gallery Favorite URI metadata while confirmed purge removes stale Favorite references.
- **Mutation bound:** a single Trash/Restore/Delete request is limited to 100 unique `content://media/...` image/video item URIs. Non-MediaStore, file, network, blank, generic-files, collection-only, or malformed URIs are rejected before Android mutation request creation.
- **Android 10 fail-closed boundary:** this Development slice does not add a legacy direct-delete/recovery workaround. Delete/Trash/Recycle Bin mutation remains unavailable below Android 11 until a separately approved compatibility path exists.
- **Move remains unavailable in selection:** selection state does not create move/write authority; approved Move/Copy organization remains separate work.
- Framework-independent selection policy provides toggle, select-all, prune, and resolve only against a caller-supplied current authorized/presented media scope; stale or foreign content URIs cannot become bulk-action authority.
- Framework-independent non-destructive bulk-action policy preserves presentation order and derives the narrowest safe Share MIME type while deterministically planning Favorites Add/Remove.
- Edit remains intentionally unavailable in the current Development viewer until an approved editing path is implemented and validated.
- Video items currently use authorized poster thumbnails; native playback is not yet implemented.
- Permission and load-generation re-checks before viewer rendering.
- Framework-independent album/trash/recovery/mutation foundations used by later native milestones.
- GLAZE UI V1.6 application-source mapping remains subject to full Gallery-specific visual, accessibility, adaptive-layout, and physical-device acceptance.

### Settings available in the current Development candidate

The first-class Settings destination is available even before media access is granted. Settings are grouped into Performance, Library, Playback, Privacy & protection, Deletion & recovery, Appearance, Cache, Favorites, and Settings portability.

The following controls have active behavior in the current Development candidate:

- **File loading priority — Slow / Fast:** Slow uses one local thumbnail worker; Fast uses four. The selection is persisted and changes the in-process thumbnail executor without broadening MediaStore authority.
- **Manage included folders:** optionally restricts presentation to selected album/folder identities already present in the current Android-authorized MediaStore snapshot. An empty include set means All.
- **Manage excluded folders:** suppresses selected authorized album/folder identities from Gallery presentation. Exclusion takes precedence over inclusion.
- **Show hidden items:** allows Gallery to show hidden-looking names only when Android already exposes those items in the authorized MediaStore snapshot. It does not bypass Android MediaStore, `.nomedia`, profile isolation, or permission controls.
- **Clear cache:** evicts the bounded in-memory thumbnail cache only; it never deletes media files.
- **Export Favorites / Import Favorites:** writes or reads a versioned local JSON representation of Gallery's app-local favorite content-URI set through Android's document provider. Import merges favorite state and does not grant access to media Android has not authorized.
- **Export settings / Import settings:** writes or reads a versioned JSON document containing non-secret Gallery preferences, including folder visibility selections. Unknown fields are ignored and imports do not carry passwords, credentials, signing material, or media bytes.
- **Rounded-square thumbnails:** toggles GoreeCloud rounded-square clipping for current media and album thumbnails.
- **Move deleted items to Recycle Bin:** on Android 11+, controls whether the ordinary Gallery Delete action requests Android Trash/Recycling or Android-confirmed permanent deletion. It is enabled by default. Android owns the destructive confirmation surface in both modes.

The following requested settings are present and persisted now, but their behavioral effect remains gated by unfinished capability work and must not be represented as implemented playback or cleanup behavior:

- **Play videos automatically** — stored preference; applies when validated native video playback exists.
- **Loop videos** — stored preference; applies when validated native video playback exists.
- **Animate GIFs in thumbnails** — stored preference; animated thumbnail decoding is not yet enabled.
- **Delete empty folders after deleting their content** — stored preference; automatic empty-folder cleanup is not enabled by the current Android-authorized Delete/Trash slice.

**Password protect photos** is deliberately not implemented as a fake app-local password switch. The Settings row explains that Protected Photos requires a real secure-media implementation using supported Android/GoreeCloud authentication and protected storage, Privacy Shield consent/visibility policy, GoreeCloud Identity where applicable, and Wardveil trust/security boundaries. Until that work is implemented and accepted, the setting is shown as not yet available.

## Historical screenshot restoration requirements

The historical screenshots supplied for the native migration establish the following product requirements. These are restoration targets unless a later authoritative requirement intentionally supersedes them; they must not be represented as already implemented merely because they are documented here.

### Photos / primary library

- A media-dominant Photos surface with a dense multi-column thumbnail grid.
- Fast access to search, sorting/grouping, layout/view controls, and overflow actions.
- Clear navigation among the principal Gallery areas rather than exposing implementation/debug controls as the primary UI.
- Rounded media presentation and compact chrome so photos and videos remain the dominant content.

### Albums and folder browsing

- A dedicated Albums experience using meaningful cover thumbnails, album names, and item counts.
- Album/folder browsing that supports visually rich two-column or adaptive cover layouts where appropriate.
- Search folders/albums plus contextual creation, organization, sorting, and overflow actions.
- Album actions such as create, rename, reorder where supported, move/copy organization, hide/exclude policy, and details through Android-authorized boundaries.

### Photo and media viewer

- A full media viewer rather than a dialog-only preview.
- Edge-to-edge media presentation with restrained top chrome and a bottom action surface.
- Primary actions modeled around Send/Share, Favorite, Edit, Delete/Trash, and More/contextual actions.
- Swipe/previous/next navigation within the currently authorized and presented collection.
- Full-resolution image viewing and native video playback as separate implementation milestones.

### Grouping, sorting, and timeline views

- Chronological grouping by Today/date and other useful timeline units.
- User-selectable grouping and sorting rather than only one fixed newest-first grid.
- Timeline-oriented views capable of browsing media by capture date while retaining album/context identity.
- View-density/layout controls where they improve browsing without overwhelming the primary interface.

### Favorites

- A first-party Favorites collection/surface.
- Fast favorite/unfavorite action from the viewer and selection states.
- Favorites remain device-local by default and must not require GoreeCloud Photos or network access.

### Private Photos and hidden media

- Private/hidden media is an established Gallery concept that must be restored with a current security design rather than copied literally from the historical pattern-lock UI.
- Current implementation must use appropriate Android/GoreeCloud authentication and authorization boundaries, with GoreeCloud Identity where applicable and Privacy Shield governing consent, visibility, and user control.
- Authentication methods such as device credentials/biometrics may be used only through supported secure platform mechanisms; Gallery must not invent insecure credential storage merely to mimic the historical UI.
- Wardveil Security must govern applicable protection, validation, trust, and security-response responsibilities.

### Settings and recycle/trash behavior

- Gallery settings must include meaningful privacy/security, hidden-media, visible-action, organization, and recycle/trash controls where supported.
- Recycle Bin / Trash behavior is a first-party product expectation, with restore and permanent-delete flows clearly distinguished.
- Destructive operations must remain explicit and Android-authorized; historical UI is visual/behavioral migration evidence, not authority to bypass current Android safeguards.

### Navigation model

- The historical product used clear top-level destinations for media, albums, and video-oriented browsing. The current native implementation may modernize exact labels and placement under GLAZE UI V1.6, but it must preserve similarly direct access to the major Gallery domains.
- Search and contextual actions must be reachable from the relevant browsing surface without forcing users through debug-style filter controls.

## Established Gallery capabilities to restore in the native replacement

The exact migration set is governed by historical GoreeCloud Gallery behavior and screenshots, but the restoration program includes the mature Gallery areas below wherever they were part of the established product or are required to complete the intended Samsung Gallery-inspired experience:

- Pictures/media browsing with dense chronological thumbnail presentation and date grouping.
- Albums browsing with album covers, counts, ordering, creation, rename, move/copy organization, and appropriate album actions.
- Search across local media and albums using locally available metadata where authorized.
- Favorites and favorite filtering/collections.
- Selection and multi-select with contextual bulk actions.
- Full media viewer with swipe/previous/next navigation and appropriate viewer chrome.
- Native image viewing at useful/full resolution and native video playback.
- Share/export and approved Android handoff workflows.
- Edit entry points and approved first-party editing workflows.
- Delete/trash/recovery flows with explicit destructive-action authorization.
- Move/copy/organize actions through Android-supported media boundaries.
- Details/metadata presentation and approved metadata-editing workflows.
- Slideshow and other established local presentation actions where supported by the historical Gallery product.
- Hidden/excluded album or media controls and sensitive-media policy governed by Privacy Shield.
- Contextual overflow menus and action surfaces appropriate to the current browsing/viewer state.
- Settings/preferences needed to support Gallery behavior without surfacing meaningless controls.
- Any additional established first-party Gallery capability evidenced by the historical GoreeCloud Gallery screenshots or prior accepted product behavior.

## Development work still required

- Continue the mature Samsung Gallery-inspired restoration beyond the current Photos / Albums / Videos / Settings experience, bounded viewers, selection, Android-authorized Delete/Trash, and integrated Recycle Bin candidate.
- Physically validate the integrated Recycle Bin on representative Android devices with disposable copied media, including Albums entry, single-item viewer Restore/Purge, multi-select Restore/Purge, cancel behavior, mixed photo/video behavior, partial-media permission behavior, permission revocation, empty bin, provider failure, restart/process recreation, and retention/expiry refresh.
- Continue destructive-operation acceptance for ordinary Trash/permanent-delete mode, permission changes, post-mutation refresh, OEM/profile behavior, and other required edge cases.
- Refine multi-select from physical-device evidence and add approved contextual actions as their authorities become real; Move remains unavailable until its mutation path is implemented and validated.
- Add richer grouping modes, view-density/layout controls, album creation/rename/reorder, and approved move/copy organization.
- Complete useful/full-resolution image viewing and native video playback, then connect the saved autoplay/loop preferences to accepted playback behavior.
- Complete animated GIF thumbnail decoding before treating the saved GIF-animation preference as behaviorally active.
- Complete approved first-party editing and approved metadata-editing workflows.
- Implement slideshow and other established local presentation actions where supported by historical Gallery evidence.
- Expand contextual/overflow actions and Share/export acceptance beyond the current Android read-only share handoff where needed.
- Complete secure Private/Protected Photos, hidden/excluded media policy, and password/device-credential protection through supported platform mechanisms.
- Connect automatic empty-folder cleanup only after a safe, evidence-backed implementation exists.
- Complete Privacy Shield, Wardveil, Everkeep, GoreeCloud Identity, and GoreeCloud Mesh integration where applicable and evidence-backed.
- Complete GLAZE UI V1.6 conformance, TalkBack, switch access, large-text, contrast, reduced-motion/transparency, adaptive-layout, tablet/foldable, and representative-device acceptance.
- Complete signed release packaging, upgrade/recovery acceptance, and Stable qualification.

## GLAZE UI V1.6 modernization requirement

GLAZE UI V1.6 modernization must improve hierarchy, navigation, material, responsive behavior, motion, accessibility, transient surfaces, and visual polish without deleting established Gallery capabilities merely to simplify the interface. Media remains dominant content; interaction chrome may use Glaze material selectively and must preserve Android-native behavior, performance, readability, and accessibility.

A visually polished replacement that omits mature Gallery capabilities is not a successful migration.

## Product direction, not current implementation claims

- Optional user-controlled GoreeCloud Photos integration behind explicit adapters.
- Richer local organization/search experiences that remain device-local by default.
- Continuity and recovery features governed by Everkeep.
- Security-sensitive media workflows governed by Wardveil.

The preserved Fossify reconstruction line remains transitional provenance and regression reference; it is not the long-term implementation authority for the native GoreeCloud Gallery product. Historical GoreeCloud Gallery behavior remains important migration evidence for intended product capabilities and interaction expectations.
