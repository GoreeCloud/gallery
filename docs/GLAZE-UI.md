# GoreeCloud Gallery — GLAZE UI V1.4 Application Contract

## Status

**Lifecycle:** Development  
**Required design system:** GLAZE UI V1.4 / 1.4.0 — Optical Intelligence  
**Repository-local source mapping:** 1.4.0 on the current Development upgrade branch  
**Application conformance:** Not established  
**Production eligibility:** Not established

This document defines the Gallery-specific application mapping and acceptance boundary for the current GoreeCloud design system. It supplements the authoritative GLAZE UI repository and GoreeCloud application-design governance. It does not grant Gallery conformance, Release Candidate status, production acceptance, or Stable status.

## Authoritative source anchor

The current Gallery source mapping is pinned to the authoritative `GoreeCloud/goreecloud-glaze-ui` revision:

`ee057ce9e729296aeaeda182d01db89f52bd66f3`

That revision identifies **GLAZE UI V1.4 / 1.4.0 — Optical Intelligence** as the current Stable consumer baseline. Gallery must not silently follow a moving branch when recording application acceptance; acceptance evidence must remain bound to an exact Gallery revision and an exact Glaze authority revision.

The shared Glaze V1.4.1 hardening work assigns human/manual/physical-device validation separately from V1.4.0 Stable activation. That shared lifecycle decision does not waive Gallery's own representative-device, accessibility, interaction, performance, or visual-quality gates.

## Source-token and optical mapping

`native/app/src/main/kotlin/com/goreecloud/gallery/GalleryGlazeContract.kt` is Gallery's compact repository-local Android mapping of the shared semantic contract.

The current mapping consumes the inherited governed spatial, shape, motion, and target baselines while adding the V1.4 optical boundary:

- **Spacing:** 2, 4, 8, 12, 16, 24, 32, and 48 dp from the shared spatial scale.
- **Shape:** quiet 10 dp, control 12 dp, container/soft 20 dp, rounded 24 dp, overlay 28 dp, and capsule/full-pill geometry.
- **Motion:** micro 160 ms, standard 240 ms, connected 360 ms, spatial 480 ms, reduced-standard 180 ms, and minimal 0 ms.
- **Interaction target:** 48 dp minimum for ordinary Android application controls represented by this contract.
- **Adaptive gutters:** 16, 24, 32, and 48 dp values from the governed spatial baseline.
- **Optical Intelligence:** content-aware frost and semantic blur protection are eligible where useful, while environmental/memory tint remains bounded to the shared maximum influence.
- **Accessibility fallbacks:** reduced transparency and increased contrast must collapse or suppress decorative optical treatment rather than weakening readability.

Gallery's media-grid and album-grid item counts remain product-specific presentation decisions. They are not the same thing as the Glaze foundational layout-grid column tokens. Android width thresholds used to select Gallery composition are platform adapter heuristics rather than universal Glaze device identities.

## Current V1.4 migration tranche

The V1.4 upgrade changes real native presentation and interaction behavior rather than merely changing a version label:

- the native contract is re-pinned from V1.3.0 to V1.4.0 at an exact Stable Glaze authority revision;
- V1.4 optical and accessibility boundaries are represented in repository-local source and unit tests;
- the bottom navigation capsule receives revised height, side spacing, elevation, and reserved-space composition;
- long-press selection now transitions into drag-to-select instead of requiring one-by-one tapping for large selections;
- drag-selection supports bounded edge auto-scroll so the gesture can continue across off-screen media;
- selected thumbnails use a lighter semantic accent outline/tint and a smaller check treatment instead of the previous heavy overlay;
- rendered acceptance has been updated for the real fresh-device Android permission state rather than assuming Search/Sort controls are visible before media is authorized;
- MediaStore relative-path metadata and a bounded Android-authorized Move backend are being introduced so organizational actions can become functional without turning album ids into raw filesystem authority.

These changes materially advance Gallery, but they are **not yet a complete whole-application V1.4 acceptance**. Residual presentation values, destination-picking UI, dialogs, viewer/editor surfaces, Settings, Recycle Bin, adaptive layouts, and physical-device behavior still require deliberate review.

## Media-first hierarchy

Gallery is a media application. Glaze treatment must improve structure and usability without competing with the user's photos or videos.

Gallery should therefore prefer:

- media-dominant browsing surfaces;
- solid or readability-first content planes around primary media;
- glazed treatment for transient navigation, command, search, control, and feedback chrome only where clarity remains strong;
- restrained container and overlay treatment for dialogs, contextual actions, Settings, destination pickers, and Recycle Bin workflows;
- semantic rounded geometry rather than unrelated hand-selected radii;
- clear state and focus treatment that does not rely on shape, color, translucency, or motion as the sole signal;
- connected motion only where it preserves a meaningful relationship, such as thumbnail-to-detail transitions;
- no continuous decorative wobble, pulse, bounce, or restless animation.

Full-screen viewer chrome and editor controls should remain visually subordinate to media. Destructive or organizational actions must remain explicit and distinguishable without using aesthetic treatment to imply that Android or a GoreeCloud platform authority has approved an operation.

## Selection and organizational surfaces

Selection mode is a first-class Gallery workflow and must feel native to V1.4 rather than like an overlay bolted onto the photo grid.

Current Development behavior includes long-press entry, tap-to-toggle, drag select/deselect semantics, edge auto-scroll, contextual bulk actions, explicit exit behavior, and accessibility announcements. Selection state remains bounded to the current authorized/presented scope and cannot manufacture authority for hidden or foreign media.

The next organizational surface is the Move destination picker. It must use a clear V1.4 Overlay/Raised hierarchy, show only destinations derived from authoritative current MediaStore metadata, distinguish the current folder from eligible destinations, provide understandable item counts, and never present an album name alone as proof of writable filesystem authority.

New-folder creation must be treated as a separate capability with explicit naming, path validation, collision behavior, failure handling, and Android authorization semantics before it becomes active.

## Accessibility and resilience

V1.4 source mapping does not replace Gallery-specific accessibility acceptance.

Before Gallery can claim current design-system conformance, applicable testing must cover at least:

- TalkBack labels, role/state announcements, and logical traversal;
- drag-selection alternatives for users who cannot perform a continuous drag gesture;
- keyboard and switch-access operation where applicable;
- visible focus and non-color state differentiation;
- 200% text scaling and increased Android display size;
- ordinary 48 dp target floors and any applicable larger assisted-target behavior;
- right-to-left layout and localization resilience;
- increased contrast and reduced-transparency behavior;
- reduced-motion and minimal-motion semantic equivalents;
- narrow phone, representative phone, tablet/large-window, rotation, and other applicable Android form factors;
- safe-area, system-bar, keyboard-occlusion, and platform-inset behavior;
- representative-device frame pacing and interaction stability.

Accessibility reflow may take precedence over density or multi-region presentation. Gallery must not shrink interaction targets merely to preserve a desired number of grid columns or controls.

## Appearance and material boundary

Gallery must not equate conformance with a matching color palette. Application acceptance must evaluate typography, spacing, geometry, hierarchy, state, contrast, motion, input behavior, and transient surfaces as a coherent system.

The application remains offline-first. A Glaze enhancement must not add remote fonts, remote icons, analytics, advertising, tracking pixels, network-hosted style resources, or another network dependency. Gallery's current no-unnecessary-network boundary remains authoritative over decorative effects.

V1.4 optical behavior must be contextual and bounded. Solid semantic fallback is required whenever translucency, tint, blur, or contextual optical adaptation would reduce readability, accessibility, performance, privacy, or platform consistency.

## Gallery-specific product invariants

The current migration must preserve established Gallery product behavior while modernizing the interface. In particular:

- Photos, Albums, Videos, Settings, Favorites, Recovery/Recycle Bin, viewer, selection, and editor capabilities must remain discoverable according to their implemented Development scope.
- Media grids must remain media-dominant rather than turning every thumbnail into a decorative card.
- Album surfaces must preserve meaningful covers, names, counts, and Android-authorized scope.
- Drag-to-select must never expand selection outside the current visible/authorized presentation scope.
- Viewer navigation must stay within the current authorized/presented collection.
- Accessible Previous/Next alternatives must remain available alongside swipe navigation.
- Photo editing must retain the current non-destructive Save copy authority boundary unless a separately approved contract changes it.
- Trash, Restore, permanent deletion, and Move must remain distinct operations and continue to rely on the applicable Android-owned authorization path.
- Move success must reflect actual MediaStore mutation results; partial failure must not be presented as complete success.
- Unknown or unavailable Privacy Shield, Wardveil Security, Everkeep, Identity, Mesh, or Manager evidence must never be converted into a positive visual status.

## Historical design-system evidence

The repository contains historical Gallery Glaze work from earlier design-system generations, including 1.0, V1.3, and earlier transitional presentation evidence. Those records remain useful for regression analysis, provenance, restoration, and comparison, but they do not override the current required V1.4 / 1.4.0 consumer baseline.

Older screenshots and accepted Gallery presentation invariants remain valuable visual-comparison evidence. They do not make historical Glaze version labels current, and they do not authorize copying third-party proprietary assets or implementation details.

## Automated source evidence

The repository-local V1.4 source mapping must be protected by tests that verify at least:

- exact `1.4.0` source version;
- exact Glaze authority revision;
- governed semantic spacing values;
- governed semantic shape-role mapping;
- governed semantic motion values;
- 48 dp ordinary interaction floor;
- bounded V1.4 optical-memory influence and required accessibility fallback flags;
- adaptive gutter outputs;
- product-specific media/album density remains explicit rather than being confused with the shared layout grid;
- navigation reserved space remains sufficient for the bottom control surface;
- drag-selection state remains current-scope bounded;
- MediaStore move destinations and pending mutation state fail closed when malformed or outside accepted authority.

A passing source/build workflow proves only the checks executed on that exact Gallery revision. It does not establish visual quality, accessibility, device behavior, platform-system integration, recovery, signing, production readiness, or Stable qualification.

## Remaining V1.4 acceptance gates

Gallery remains globally nonconformant until applicable current-source work and acceptance evidence are complete. Remaining gates include:

- connect the Android-authorized Move backend to a V1.4 destination picker and validate existing-folder moves;
- define and validate create-folder behavior separately;
- reconcile residual Gallery-controlled hard-coded presentation values and Android resources with V1.4 semantic/optical roles;
- whole-application rendered review across browsing, albums, search, Favorites, viewer, editor, Settings, dialogs, selection, Move, and Recycle Bin;
- interaction-state and connected-motion review;
- accessibility and large-text acceptance;
- adaptive/form-factor and rotation acceptance;
- representative physical-device/OEM/profile testing;
- qualitative visual-excellence review;
- performance/frame-pacing review where affected;
- rollback evidence bound to exact source revisions;
- platform-system acceptance where applicable;
- protected signing/provenance and release evidence;
- explicit production approval and Stable qualification.

No source version string, manifest label, unit test, screenshot, APK assembly, or CI run may independently waive these gates.
