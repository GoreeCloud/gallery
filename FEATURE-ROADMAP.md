# GoreeCloud Gallery — Feature Roadmap

**Lifecycle:** Development  
**Canonical Drive control:** `GoreeCloud/Feature Roadmap/GoreeCloud Gallery/FEATURE-ROADMAP.docx`  
**Authoritative project record:** `Project Specification — Gallery`  
**Canonical repository:** `GoreeCloud/goreecloud-gallery`

## Control rules

This repository roadmap and the canonical Drive roadmap must remain materially synchronized with the authoritative Gallery project specification, verified repository implementation state, applicable GoreeCloud platform-system requirements, and GoreeCloud Tasks Management.

A roadmap entry does not establish implementation, acceptance, Release Candidate, production, or Stable status. Status changes require the applicable source, exact-revision validation, representative runtime/device evidence, review, release, and production evidence.

| ID | Feature / obligation | Priority | Current state |
| --- | --- | --- | --- |
| FR-001 | Reconcile and maintain every current planned or recommended GoreeCloud Gallery feature from the authoritative project record and verified repository evidence in this roadmap. | High | Ongoing control |
| FR-002 | Move actionable feature obligations into GoreeCloud Tasks Management when required, preserving priority, dependency, and lifecycle disposition. | High | Ongoing control |
| FR-003 | Do not mark features implemented, complete, cancelled, or superseded without authoritative evidence and synchronized repository/Drive roadmap updates. | High | Ongoing control |
| FR-004 | Deliberately map first-party native Gallery source to the current Official Stable GLAZE UI V1.4 / 1.4.0 Optical Intelligence authority using exact source anchors and governed semantic/optical roles. | High | Development — repository-local `GalleryGlazeContract.VERSION` maps `1.4.0` at exact Glaze authority `ee057ce9e729296aeaeda182d01db89f52bd66f3`. A substantive native Optical Intelligence chrome pass is in the active draft line. Whole-application acceptance remains incomplete. |
| FR-005 | Complete fresh Gallery-specific V1.4 rendered, interaction, accessibility, adaptive/form-factor, representative-device/OEM/profile, performance, Human Visual Excellence, optical-fallback, rollback, release, and production acceptance. | High | Required / not accepted |
| FR-006 | Complete first-party photo-editor acceptance for crop, rotate, flip, Reset, non-destructive Save copy, process recreation, orientation/output fidelity, metadata/color behavior, failure/cancellation cases, accessibility, and representative devices. | High | Development — first-party editor source and recreation-state hardening exist; rendered/device, fidelity, accessibility, OEM/profile, and release acceptance remain incomplete. |
| FR-007 | Complete Recycle Bin and destructive-operation edge-case acceptance, including permission changes, mixed-media behavior, provider failure, process recreation, OEM/profile behavior, retention/expiry refresh, and recovery correctness. | High | Development — core tested and representative-device paths exist; broader acceptance remains incomplete. |
| FR-008 | Integrate and accept applicable Privacy Shield, Wardveil Security, Everkeep, GoreeCloud Identity, GoreeCloud Mesh, and GoreeCloud Manager authorities without converting unknown/unavailable evidence into positive status. | High | Blocked / integration acceptance pending |
| FR-009 | Complete protected signing/provenance, packaging/distribution, upgrade/recovery, rollback, Release Candidate qualification, production approval, and Stable qualification for the native Android application. | High | Planned / release gates open |
| FR-010 | Provide bounded long-press + drag multi-select with edge auto-scroll while preserving current authorized/presented media scope and in-place selection rendering. | High | Development — implemented with bounded core drag-session policy, rendered long-press/drag behavior, edge auto-scroll, and semantic selection treatment. Exact-head CI and representative-device/accessibility acceptance remain lifecycle gates. |
| FR-011 | Provide Android-authorized Move to an existing local folder without treating selection, album IDs, or display names as filesystem authority. | High | Development — provider-owned `RELATIVE_PATH`, fail-closed destination derivation, exact bounded `MediaStore.createWriteRequest(...)` authorization, recreation-safe pending state, per-item provider updates, partial-failure accounting, and an existing-folder picker are implemented. Representative-device Move acceptance remains required. |
| FR-012 | Provide bounded New Folder Move without arbitrary filesystem authority, including naming, collision, same-source parent, cancellation, partial-failure, and refresh behavior. | High | Development — `0.8.1-dev` now implements New Folder only beneath the one authoritative source `RELATIVE_PATH` shared by all selected items. Unsafe names, mixed-source parent ambiguity, foreign selection state, and known visible path collisions fail closed. Representative-device acceptance is pending. |
| FR-013 | Implement Copy/duplicate organization through a separately authorized Android media path; do not reuse Move authority as Copy authority. | High | Planned |
| FR-014 | Complete native video playback and connect saved autoplay/loop preferences only after playback behavior is accepted. | High | Planned / poster thumbnails only today |
| FR-015 | Complete secure Private/Protected Photos using supported Android/GoreeCloud authentication and protected storage with Privacy Shield and Wardveil evidence. | High | Planned / fail-closed; fake app-local password protection prohibited |
| FR-016 | Keep Gallery's Glaze compatibility declaration synchronized with the canonical Glaze lifecycle registry and Platform Contract validator. | High | Current target aligned at `1.4.0`; continue fail-closed monitoring for future authority changes. |

## Current sequencing recommendation

1. Build and exact-head validate the `0.8.1-dev` V1.4 Optical Intelligence + New Folder Move candidate.
2. Validate the V1.4 visual revamp on representative physical Android devices, including safe areas, light/dark appearance, large text, selection chrome, Move/New Folder overlays, Reduced Transparency / Increased Contrast fallbacks, and media-dominant browsing.
3. Validate existing-folder and New Folder Move with disposable media: success, invalid names, known collisions, cancellation/denial, mixed-source selection, mixed photo/video, permission changes, Activity recreation, post-move refresh, Favorites continuity, OEM/profile behavior, and accessibility.
4. Implement Copy as a separate authority path; do not broaden filesystem access merely to complete organization UI.
5. Close representative-device/OEM/profile correctness gaps for editor, permissions, media orientation, Recycle Bin, destructive operations, native playback, and remaining mature Gallery restoration.
6. Integrate applicable GoreeCloud platform authorities while preserving fail-closed evidence semantics.
7. Complete recovery, signing/provenance, Release Candidate, production, and Stable gates.

This sequencing preserves the current offline-first Android media boundary and avoids treating UI modernization, selection state, New Folder naming, or Android write consent as authority to broaden MediaStore, network, cloud, profile, mutation, or protected-media access.
