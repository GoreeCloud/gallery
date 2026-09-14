# GoreeCloud Gallery — Glaze UI Conformance Contract

## Status

**Product:** GoreeCloud Gallery  
**Platform:** Native Android  
**Current Gallery conformance target:** **GLAZE UI V1.4 / 1.4.0 Stable**  
**Canonical design-system repository:** `GoreeCloud/goreecloud-glaze-ui`  
**Canonical lifecycle authority:** `registry/lifecycle.json` in the Glaze UI repository  
**Native implementation model:** Android-native semantic mapping; no copied web runtime is required  
**Permanent Glaze exceptions:** none approved

This document is the active Gallery-side Glaze UI adoption contract. Older gc.11–gc.17 notes remain useful implementation history, but they are not current design-system authority.

## Governing rule

GoreeCloud Gallery must track the latest **consumer-eligible Stable** Glaze UI release before it can be considered Stable. Adoption is repository-local: a new Stable release in `goreecloud-glaze-ui` does not automatically make Gallery conformant or Stable.

Gallery must independently prove that its Android implementation:

- targets the current Stable version;
- preserves the current Glaze semantic hierarchy and recognizable GoreeCloud identity;
- remains readable in light, dark, accessibility, and high-content-density states;
- uses platform-native Android behavior where native interaction or security authority is stronger than decorative fidelity;
- preserves reduced-motion, contrast, touch-target, and assistive-technology behavior;
- introduces no remote UI, analytics, font CDN, icon CDN, tracking, or network dependency merely to render Glaze UI; and
- does not claim Stable until current-version conformance and representative-device acceptance are verified.

## V1.4 — Optical Intelligence mapping

GLAZE UI V1.4 advances the design language with bounded optical adaptation while explicitly requiring accessibility, semantic meaning, task completion, privacy, and security authority to outrank decoration.

Gallery maps that principle natively rather than embedding the web Optical Engine. The Android mapping includes:

- **Canvas** for the application background and media browsing environment;
- **protected semantic surfaces** for navigation, selection actions, dialogs, sheets, settings rows, and critical state;
- **Raised/Overlay depth** for transient or action-priority surfaces;
- restrained tint and depth that never reduce foreground readability;
- solid/readability-first fallbacks when transparency, contrast, platform behavior, or performance makes optical treatment unsafe;
- explicit light/dark foreground treatment rather than assuming one optical treatment works in both modes;
- current Glaze rounded geometry and comfortable 48dp-or-larger actionable targets;
- local-only visual adaptation with no collection of camera, wallpaper, analytics, or remote context by the design layer.

The source authority for these native constants is `GalleryGlazeContract.kt`, whose `VERSION` must match the current consumer-eligible Stable release before Gallery may claim current-version conformance.

## Media-first composition

Gallery is a media application. Glaze UI should make GoreeCloud recognizable without reducing useful media density or putting decorative glass over every photograph.

The Photos and Videos timelines therefore keep thumbnails visually primary while structural chrome uses Glaze semantics. Album/folder collections may use stronger grouping and depth because their labels and controls are semantic content rather than the media itself.

Selection overlays must be unmistakable without obscuring the selected photo. Bottom navigation and bulk-action surfaces must remain visually separated from the grid, readable over light or dark media, reachable with one hand, and consistent with current Glaze geometry.

## Selection and bulk-action UX

Selection is a first-class Gallery workflow.

Current requirements are:

- long-press may enter selection mode;
- tap toggles individual selected items;
- touch sweep/drag across visible media must support fast multi-selection without requiring one tap per item;
- sweep application must be idempotent so pointer jitter cannot repeatedly toggle the same item;
- selected items remain bounded to the current authorized/presented scope;
- Share, Favorite/Unfavorite, Move, Delete, and More use current Glaze semantic action treatment;
- disabled actions must communicate a real platform/authority limitation rather than a missing implementation that should already exist;
- Android-owned permission or destructive confirmation UI remains Android-owned and must not be visually forged by Gallery.

Sweep selection is an enhancement, not the only interaction path. Keyboard, accessibility, tap, and long-press selection remain independently operable.

## Move to album/folder

Gallery albums are Android MediaStore buckets/folders rather than an invented app-local organization layer.

Moving selected media therefore follows these boundaries:

1. Gallery resolves the selected items only from the current authorized/presented scope.
2. The user chooses an existing album/folder discovered from the authorized MediaStore library.
3. The Android adapter resolves that bucket's authoritative `RELATIVE_PATH` locally and fail-closed.
4. Gallery requests Android write authority over the exact selected image/video item URIs.
5. Only after Android grants that request may Gallery update the items' MediaStore `RELATIVE_PATH`.
6. Selection is cleared and the MediaStore library is re-read after the move.

Gallery must never translate a UI selection into unrestricted filesystem access. A destination name alone is not authority, and a stale/foreign content URI must never enter a move plan.

## Accessibility and optical fallback

V1.4 requires semantic clarity to outrank optical effects. Gallery's Android mapping therefore follows these rules:

- text and icon contrast must remain sufficient over semantic surfaces in both light and dark appearance;
- selection indicators must not rely on color alone;
- reduced-motion behavior must preserve state communication without decorative animation;
- accessibility or platform states may force a solid surface even when the standard Glaze presentation is translucent;
- critical/destructive actions must remain visually distinguishable without making the whole interface alarm-colored;
- system-owned permission and destructive dialogs may use platform styling when Android owns the authority boundary.

A screenshot that looks attractive is not sufficient proof of accessibility or conformance. Representative-device and assistive-technology checks remain separate release evidence.

## Navigation and responsive behavior

Gallery uses Android-native adaptive sizing while preserving Glaze intent:

- Compact phone layouts prioritize media density, thumb reach, and a floating semantic navigation/action surface;
- wider layouts increase gutters and grid columns without simply scaling phone geometry;
- minimum media tile and album tile sizes are bounded by `GalleryGlazeContract`;
- bottom navigation reserves content space so it does not cover the final media row;
- selection mode replaces normal navigation with action-priority controls rather than stacking both bars.

Android `sw*dp` resource classes are platform-native equivalents of responsive intent and are not claimed to be identical to web breakpoints.

## Privacy, security, and local-first behavior

Glaze UI adoption grants no new data authority.

Gallery remains local-first and must not require a GoreeCloud account, analytics connection, remote style service, or remote optical context to render. Media access remains controlled by Android and GoreeCloud privacy/security policy. Moving, deleting, restoring, exporting, or sharing media must use the narrowest authority appropriate to the operation.

## Current conformance state

Targeting V1.4.0 is necessary but not sufficient to call GoreeCloud Gallery Stable.

The Gallery repository must still independently verify:

- native source and tests target the current Stable version;
- Photos, Albums, Videos, Settings, selection mode, dialogs, sheets, viewer chrome, and recovery surfaces use the current Glaze semantic system;
- light/dark and representative-device visual acceptance;
- touch sweep selection and bulk move behavior on real Android hardware;
- Android permission/write/delete/trash boundaries;
- accessibility and reduced-motion behavior; and
- build/release evidence tied to the exact candidate revision.

Until those checks pass, Gallery must remain Development/Candidate even when its source contract names the current Glaze Stable version.

## Historical note

Earlier Gallery implementation lines documented Glaze UI 1.0.0 and the retired repository name `GoreeCloud/glaze-ui`. Those statements are historical only. The active canonical repository is `GoreeCloud/goreecloud-glaze-ui`, and the active Gallery adoption target in this document is Glaze UI 1.4.0 Stable.
