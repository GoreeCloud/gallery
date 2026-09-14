# GoreeCloud Gallery — GLAZE UI V1.4 Application Contract

## Status

**Lifecycle:** Development  
**Current Official Stable design system:** GLAZE UI V1.4 / 1.4.0 — Optical Intelligence  
**Repository-local source mapping:** 1.4.0  
**Application conformance:** Not established  
**Production eligibility:** Not established

This document defines the Gallery-specific native Android mapping and acceptance boundary for the current Official Stable GoreeCloud design system. It supplements the authoritative GLAZE UI repository and GoreeCloud application-design governance. It does not grant Gallery conformance, Release Candidate status, production acceptance, or Stable status.

## Authoritative source anchor

Gallery's current source mapping is pinned to the authoritative `GoreeCloud/goreecloud-glaze-ui` Stable V1.4 revision:

`ee057ce9e729296aeaeda182d01db89f52bd66f3`

The live Glaze lifecycle registry identifies **GLAZE UI V1.4 / 1.4.0 — Optical Intelligence** as `currentOfficial` and `currentStable`, consumer eligible, with V1.3 retained as the direct rollback baseline. The V1.4 Stable acceptance decision is dated 2026-09-13. Gallery acceptance evidence must remain bound to exact Gallery and Glaze revisions rather than a moving branch.

Human optical review, subjective polish review, manual assistive-technology qualification, representative physical-device qualification, and real-device performance validation remain V1.4.1 follow-up work in the shared Glaze authority. Their deferral does not convert those checks into passed Gallery evidence.

## Native Optical Intelligence mapping

`native/app/src/main/kotlin/com/goreecloud/gallery/GalleryGlazeContract.kt` records Gallery's exact design-system source anchor. `GalleryGlazeSurfaces.kt` maps V1.4 Optical Intelligence into bounded Android-native control, chrome, raised, and overlay surfaces.

The current mapping includes:

- semantic spacing values of 2, 4, 8, 12, 16, 24, 32, and 48 dp;
- semantic quiet/control/container/rounded/overlay/capsule geometry;
- 160/240/360/480 ms motion roles plus reduced/minimal equivalents;
- a 48 dp ordinary Android interaction target floor;
- adaptive Gallery gutters selected from governed spacing roles;
- media-first composition in which photos and videos remain visually dominant;
- bounded environmental-memory tint capped at 8 percent and explicitly prevented from overriding semantic states;
- native content-aware-frost and semantic-blur-protection responsibilities represented through readable near-opaque Android surfaces rather than requiring a web blur runtime;
- required Reduced Transparency and Increased Contrast fail-closed fallbacks;
- light and dark GoreeCloud palettes plus Android environmental-color input used only for non-semantic expression.

Gallery's media-grid and album-grid column counts remain product-specific composition decisions rather than shared Glaze layout-grid tokens.

## Current V1.4 revamp tranche

The current Development tranche makes substantive presentation changes rather than merely changing a version string:

- maps Gallery to the verified current Official Stable V1.4 / 1.4.0 authority;
- introduces native Optical Intelligence surface roles for search, header controls, permission/status surfaces, navigation chrome, settings rows, dialogs, and organizational sheets;
- preserves semantic teal selection and action states independently from environmental tint;
- increases top/header breathing room while retaining the corrected Android system-bar safe areas;
- keeps the full-screen media viewer media-first and black rather than applying decorative tint over content;
- retains the bounded bottom navigation/selection capsule while strengthening surface hierarchy and elevation;
- preserves 48 dp control floors and the existing safe-area regression coverage;
- extends the Move workflow with a bounded New Folder path while keeping Android-owned write authorization intact.

These changes materially improve Gallery's V1.4 implementation, but they are not whole-application V1.4 acceptance.

## Media-first hierarchy

Gallery is a media application. Glaze treatment must improve structure and usability without competing with the user's photos or videos.

Gallery therefore prefers media-dominant browsing, restrained interactive chrome, semantic rounded geometry, clear focus/state treatment, and solid or near-solid fallbacks when decorative expression would reduce readability, accessibility, performance, privacy, or platform consistency.

Full-screen viewer chrome and editor controls must remain subordinate to media. Destructive or organizational actions must remain explicit and must never visually imply that Android or another authority has approved an operation before that authorization actually succeeds.

## Selection and organization

Selection remains bounded to the currently authorized and presented media scope. Long-press, tap-to-toggle, drag selection, edge auto-scroll, contextual actions, explicit exit behavior, and accessibility announcements must not manufacture authority for hidden, stale, or foreign media.

The Move destination surface supports two Development paths:

1. **Existing folder** — destinations are derived from authoritative current-snapshot album metadata plus provider-owned `MediaStore.RELATIVE_PATH`.
2. **New folder** — enabled only when every selected item belongs to one current authoritative source `RELATIVE_PATH`. Gallery accepts a validated folder name and constructs a child relative path beneath that source folder.

New Folder rejects empty/unsafe names, path separators, traversal-like names, control characters, trailing period/space, and known visible destination collisions. Mixed-source selections cannot silently choose a creation parent. After destination validation, existing-folder and New Folder moves use the same Android-owned `MediaStore.createWriteRequest(...)` authorization for the exact selected media URIs. Only after Android approval does Gallery update `RELATIVE_PATH`.

This is a Development implementation, not representative-device acceptance. Copy remains separately gated.

## Accessibility and resilience

Source mapping does not replace Gallery-specific accessibility acceptance. Applicable testing still includes TalkBack, switch access, keyboard where applicable, visible focus, 200% text, display scaling, RTL, target sizes, Increased Contrast, Reduced Transparency, Reduced Motion, rotation, narrow and large-window layouts, system bars/cutouts/IME behavior, and representative-device/OEM/profile interaction.

Accessibility reflow may take precedence over density. Gallery must not shrink controls or hide required state merely to preserve a preferred visual composition.

## Appearance and optical boundary

V1.4 Optical Intelligence may use low-influence environmental color and contextual optical treatment for non-semantic atmosphere. Gallery's native mapping caps that contribution and keeps protected meanings—selection, destructive actions, privacy, security, permission, availability, warning, and other semantic states—under their own semantic authority.

Gallery does not require remote fonts, remote icons, network-hosted style resources, analytics, advertising, or tracking for the local interface. A visual upgrade must not weaken the offline-first product boundary.

## Gallery product invariants

The revamp must preserve implemented Gallery behavior while improving presentation:

- Photos, Albums, Videos, Settings, Favorites, Recovery/Recycle Bin, viewer, selection, Move, and editor capabilities remain discoverable according to their implemented Development scope.
- Media grids remain media-dominant rather than turning every thumbnail into a decorative card.
- Album surfaces preserve meaningful covers, names, counts, and Android-authorized scope.
- Viewer navigation remains within the current authorized/presented collection.
- Photo editing retains non-destructive Save copy semantics unless a separately approved authority changes it.
- Trash, Restore, permanent deletion, existing-folder Move, and New Folder Move remain distinct operations.
- Move success reflects actual MediaStore mutation results; partial failure is not reported as complete success.
- Unknown or unavailable GoreeCloud platform evidence is never converted into a positive visual status.

## Automated evidence boundary

Repository-local tests protect the exact V1.4 source anchor, semantic spacing/shape/motion values, bounded optical-memory tint, accessibility fallback flags, target-size floor, adaptive gutters, navigation reserved space, selection scope, New Folder naming/parent rules, and MediaStore Move path/pending-state validation.

Rendered acceptance additionally checks the main Gallery chrome against Android system-bar, navigation/gesture, and cutout safe areas. Passing CI proves only the executed checks on that exact Gallery revision; it does not establish final visual quality, accessibility, representative-device behavior, platform integration, signing, release approval, or Stable qualification.

## Remaining acceptance gates

Gallery remains globally nonconformant until applicable evidence is complete. Important remaining gates include:

- representative physical-device validation of the V1.4 visual revamp in light/dark and normal/large-text conditions;
- representative-device New Folder Move validation, including naming errors, cancellation, success, post-move refresh, and OEM/profile behavior;
- existing-folder photo/video/mixed Move edge-case acceptance;
- whole-application rendered review across browsing, Albums, Search, Favorites, viewer, editor, Settings, dialogs, selection, Move, and Recycle Bin;
- TalkBack, switch-access, RTL, scaling, contrast, reduced-transparency/motion, and adaptive/form-factor acceptance;
- performance/frame-pacing and qualitative Human Visual Excellence review;
- rollback and upgrade/recovery evidence;
- required GoreeCloud platform-system acceptance;
- protected signing/provenance, Release Candidate qualification, release approval, and Stable qualification.

No version string, manifest declaration, unit test, screenshot, APK assembly, or CI result may independently waive these gates.
