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
| FR-004 | Deliberately map first-party native Gallery source to the current published Stable GLAZE UI V1.3 / 1.3.0 Adaptive Resonance authority using exact source anchors and governed semantic roles. | High | Development — repository-local `GalleryGlazeContract.VERSION` maps `1.3.0` at exact Glaze authority `ff34f232f295c9dcb07e4c681f66d4104d0b9323`. A substantive native Adaptive Resonance chrome pass is in the active draft line. V1.4/V1.4.1 remain Proposed. Whole-application acceptance remains incomplete. |
| FR-005 | Complete fresh Gallery-specific V1.3 rendered, interaction, accessibility, adaptive/form-factor, representative-device/OEM/profile, performance, Human Visual Excellence, fallback, rollback, release, and production acceptance. | High | Required / not accepted |
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
| FR-016 | Resolve the stale Platform Contract 0.3 Glaze compatibility constant against the canonical published Glaze UI Stable release authority. | High | Governance blocker — current Gallery records the mismatch and remains nonconformant; no Stable claim may rely on the stale constant. |

## Current sequencing recommendation

1. Build and exact-head validate the `0.8.1-dev` V1.3 Adaptive Resonance + New Folder Move candidate.
2. Validate the V1.3 visual revamp on representative physical Android devices, including safe areas, light/dark appearance, large text, selection chrome, Move/New Folder overlays, and media-dominant browsing.
3. Validate existing-folder and New Folder Move with disposable media: success, invalid names, known collisions, cancellation/denial, mixed-source selection, mixed photo/video, permission changes, Activity recreation, post-move refresh, Favorites continuity, OEM/profile behavior, and accessibility.
4. Implement Copy as a separate authority path; do not broaden filesystem access merely to complete organization UI.
5. Close representative-device/OEM/profile correctness gaps for editor, permissions, media orientation, Recycle Bin, destructive operations, native playback, and remaining mature Gallery restoration.
6. Resolve the central Platform Contract Glaze-version mismatch and integrate applicable GoreeCloud platform authorities while preserving fail-closed evidence semantics.
7. Complete recovery, signing/provenance, Release Candidate, production, and Stable gates.

This sequencing preserves the current offline-first Android media boundary and avoids treating UI modernization, selection state, New Folder naming, or Android write consent as authority to broaden MediaStore, network, cloud, profile, mutation, or protected-media access.
