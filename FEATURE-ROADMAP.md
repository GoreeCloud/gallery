# GoreeCloud Gallery — Feature Roadmap

**Status:** Active roadmap control  
**As of:** 2026-09-21  
**Authoritative project record:** Project Specification — Gallery  
**Canonical repository:** GoreeCloud/gallery  
**Drive control:** `GoreeCloud/Feature Roadmap/GoreeCloud Gallery/FEATURE-ROADMAP.docx`

## Purpose

This file is the repository-side feature roadmap control for GoreeCloud Gallery. It distinguishes authoritative `main` from the active Development acceptance branch and records remaining device, Glaze, platform-system, recovery, and release gates.

## Current verified Development checkpoint

Authoritative Gallery `main` remains `76404dfb703436fc6186056946b7d7525a72660f`; the active runtime stabilization candidate remains Draft PR #80 on `fix/gallery-physical-device-acceptance-20260914`.

PR #88 corrected the reproducible Settings selected-navigation rendered failure without weakening the test. Exact head `eff77b9bd8224a94780bbc83a0df8505cec58dc3` passed Native Android Adapter `35629937833`, Native Android App `35629937933`, and Native Android Rendered Acceptance `35629937841`, then merged only into PR #80's Development branch as `b3695d4adff1eafc546bf9ea60af9153af8c2187`.

Fresh validation on that parent head is fully green: Platform Contract `35630761045`, Native Core `35630759919`, Native Android Adapter `35630759772`, Native Android App `35630759654`, and Native Android Rendered Acceptance `35630759774`. Platform Contract 0.4 and all nine systems are declared; current Stable GLAZE UI V1.6 / 1.6.0 remains migration-required because Gallery still uses historical V1.4 presentation semantics. Representative physical-device acceptance remains open under issue #81.

## Roadmap

| ID | Feature / obligation | Priority | Current state |
| --- | --- | --- | --- |
| FR-001 | Keep repository and Drive roadmap controls synchronized with verified reality. | High | Ongoing control |
| FR-002 | Preserve correct MediaStore move/copy/trash/delete authority and verify physical-device New Folder Move for photo/video/mixed selections. | High | Source/emulator improvements integrated in PR #80; physical-device acceptance open |
| FR-003 | Preserve stable navigation hierarchy, selected semantics, launcher icon/label, and system safe-area behavior. | High | Automated rendered regression restored at PR #80 head `b3695d4...`; physical-device acceptance open |
| FR-004 | Migrate Gallery's actual presentation implementation from historical GLAZE UI V1.4 semantics to current Stable V1.6 and complete application conformance/Human Visual Excellence. | High | Migration required |
| FR-005 | Complete accessibility, larger-text, light/dark, OEM/profile, viewer/editor/Recycle Bin, and representative-device validation. | High | Open |
| FR-006 | Complete all applicable nine Integral Platform System runtime integrations and acceptance. | High | Open |
| FR-007 | Complete backup/restore/recovery, long-lived production signing, upgrade/downgrade recovery evidence, and release provenance. | High | Open |
| FR-008 | Complete Release Candidate, production, stabilization, and Stable qualification. | High | Open |

## Maintenance and synchronization

This roadmap and the corresponding canonical Drive roadmap must remain materially synchronized with one another and with the authoritative project or service record. Update both copies whenever feature scope, priority, dependency, implementation status, cancellation, supersession, recommendation, or verification state materially changes.

No feature may be represented as complete or Stable solely because it appears in this roadmap. Completion and lifecycle claims require the applicable authoritative implementation, validation, review, release, production, and stabilization evidence.

## Reconciliation rule

At each material feature change, reconcile this roadmap against current authoritative repository state, the applicable platform-system requirements, and GoreeCloud Tasks Management. Missing obligations, stale status, duplicated work, roadmap drift, or undocumented disposition changes are defects to correct.
