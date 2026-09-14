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
| FR-004 | Deliberately migrate the first-party native Gallery source to the current GLAZE UI V1.4 / 1.4.0 Optical Intelligence authority using exact source anchors and governed semantic/optical roles. | High | Development — repository-local `GalleryGlazeContract.VERSION` maps `1.4.0` at exact Glaze authority `ee057ce9e729296aeaeda182d01db89f52bd66f3`. Draft PR #78 carries the V1.4 source migration while retaining fail-closed application conformance. Whole-application acceptance remains incomplete. |
| FR-005 | Complete fresh Gallery-specific V1.4 rendered, interaction, accessibility, adaptive/form-factor, representative-device/OEM/profile, performance, Human Visual Excellence, optical-fallback, rollback, release, and production acceptance. | High | Required / not accepted |
| FR-006 | Complete first-party photo-editor acceptance for crop, rotate, flip, Reset, non-destructive Save copy, process recreation, orientation/output fidelity, metadata/color behavior, failure/cancellation cases, accessibility, and representative devices. | High | Development — first-party editor source and recreation-state hardening exist; rendered/device, fidelity, accessibility, OEM/profile, and release acceptance remain incomplete. |
| FR-007 | Complete Recycle Bin and destructive-operation edge-case acceptance, including permission changes, mixed-media behavior, provider failure, process recreation, OEM/profile behavior, retention/expiry refresh, and recovery correctness. | High | Development — core tested and representative-device paths exist; broader acceptance remains incomplete. |
| FR-008 | Integrate and accept applicable Privacy Shield, Wardveil Security, Everkeep, GoreeCloud Identity, GoreeCloud Mesh, and GoreeCloud Manager authorities without converting unknown/unavailable evidence into positive status. | High | Blocked / integration acceptance pending |
| FR-009 | Complete protected signing/provenance, packaging/distribution, upgrade/recovery, rollback, Release Candidate qualification, production approval, and Stable qualification for the native Android application. | High | Planned / release gates open |
| FR-010 | Provide bounded long-press + drag multi-select with edge auto-scroll while preserving current authorized/presented media scope and in-place selection rendering. | High | Development — implemented in Draft PR #78 with bounded core drag-session policy, rendered long-press/drag behavior, edge auto-scroll, and V1.4 selection treatment. Exact-head CI and representative-device/accessibility acceptance remain lifecycle gates. |
| FR-011 | Provide Android-authorized Move to an existing local folder without treating selection, album IDs, or display names as filesystem authority. | High | Development — Draft PR #78 carries provider-owned `RELATIVE_PATH`, fail-closed destination derivation, exact bounded `MediaStore.createWriteRequest(...)` authorization, recreation-safe pending state, per-item provider updates, partial-failure accounting, and a V1.4 existing-folder picker. Representative-device Move acceptance remains required. |
| FR-012 | Define and implement New folder for Move only after MediaStore path, naming, collision, rollback, cancellation, partial-failure, and visibility behavior are accepted. | High | Planned / intentionally not enabled in the current `0.8.0-dev` candidate |
| FR-013 | Implement Copy/duplicate organization through a separately authorized Android media path; do not reuse Move authority as Copy authority. | High | Planned |
| FR-014 | Complete native video playback and connect saved autoplay/loop preferences only after playback behavior is accepted. | High | Planned / poster thumbnails only today |
| FR-015 | Complete secure Private/Protected Photos using supported Android/GoreeCloud authentication and protected storage with Privacy Shield and Wardveil evidence. | High | Planned / fail-closed; fake app-local password protection prohibited |

## Current sequencing recommendation

1. Validate the `0.8.0-dev` V1.4 + drag-selection + existing-folder Move candidate on representative physical Android devices using disposable copied media.
2. Close Move correctness and lifecycle cases: cancellation/denial, same-folder exclusion, mixed photo/video, selected-media/permission changes, Activity recreation, post-move refresh, Favorites continuity, OEM/profile behavior, and accessibility.
3. Define New folder semantics and then implement Copy as a separate authority path; do not broaden filesystem access merely to complete organization UI.
4. Capture fresh V1.4 rendered and accessibility evidence across Photos, Albums, Videos, Favorites, viewer, editor, Settings, selection, Move picker, dialogs, and Recycle Bin, including Reduced Transparency and Increased Contrast fallbacks.
5. Close representative-device/OEM/profile correctness gaps for editor, permissions, media orientation, Recycle Bin, destructive operations, native playback, and remaining mature Gallery restoration.
6. Integrate applicable GoreeCloud platform authorities and preserve fail-closed evidence semantics.
7. Complete recovery, signing/provenance, Release Candidate, production, and Stable gates.

This sequencing preserves the current offline-first Android media boundary and avoids treating UI modernization, selection state, or Android write consent as authority to broaden MediaStore, network, cloud, profile, mutation, or protected-media access.
