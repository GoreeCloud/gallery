# GoreeCloud Gallery — Project Record

**Repository:** `GoreeCloud/gallery`  
**Former repository identity in the Drive source:** `GoreeCloud/goreecloud-gallery`  
**Lifecycle:** Development / non-Stable  
**Record purpose:** Significant product history, repository/native-transition decisions, migration provenance, candidate boundaries, and project-specification migration evidence  
**Migration baseline:** `1a241ddb23205f15f673b2968b56fd612014ca87`  
**Canonical authority:** This file is the repository-local project record once accepted on the default branch.

## Product lineage

GoreeCloud Gallery is an offline-first local-media Android product. Its historical experience drew interaction and information-architecture inspiration from Samsung Gallery while remaining GoreeCloud-owned. Historical screenshots and behavior are retained as migration/visual-review evidence, not permission to copy proprietary assets or implementation.

## Transitional Fossify period

The repository preserves a Fossify Gallery/Commons reconstruction and GoreeCloud patch line as provenance, regression reference, migration comparison, and historical continuity.

That line produced a historical 1.0.0 acceptance candidate, but its acceptance evidence applies only to the inherited/transitional binary. It cannot establish Stable status for the native replacement.

## Native replacement decision

The long-term architecture is the original first-party implementation under `native/`, with framework-independent domain logic separated from Android MediaStore adapters and the application shell.

Over time, accepted `main` gained native media/album models, MediaStore normalization, a compiled Android adapter, an installable application shell, local browsing, Albums, Favorites, search, viewer foundations, Android-authorized Trash/Delete, Recycle Bin, selection/bulk actions, local settings/portability, and Move foundations.

## Repository rename reconciliation

The Drive source names the repository `GoreeCloud/goreecloud-gallery`. The authoritative repository has since been renamed/reconciled as `GoreeCloud/gallery`.

The current repository identity controls new links, documentation, pull requests, and migration destination. The former name remains historical provenance only.

## Drive specification interpretation

The Drive **Project Specification — Gallery.docx** contains nine top-level sections:
1. Purpose and feature-preservation requirements.
2. Native application architecture.
3. Local media and album authority.
4. Privacy, security, and continuity boundaries.
5. Glaze UI boundary.
6. Optional GoreeCloud Photos integration.
7. Transitional Fossify reconstruction.
8. Current Development state and extensive exact-revision candidate history.
9. Stable qualification gates plus accumulated native milestone evidence.

Those sections have been reconciled into `PROJECT-SPECIFICATIONS.md`. Exact historical version pins, PR heads, CI runs, transitional-candidate statements, and older lifecycle snapshots remain source-era evidence and do not override current `main`.

## Current accepted authority

At migration baseline `1a241ddb23205f15f673b2968b56fd612014ca87`, current implementation state is governed by `IMPLEMENTED-FEATURES.md`, open obligations by `PLANNED-FEATURES.md`, and meaningful accepted chronology by `CHANGELOGS.md`.

The current first-party native line remains Development/non-Stable despite substantial accepted capability work.

## Open candidate/history boundary

The repository contains numerous open Draft pull requests and historical stacked branches. Their exact-head CI, rendered, physical-device, or feature evidence remains scoped to those revisions unless the corresponding work has been integrated into authoritative `main`.

Migration documentation must not revive stale candidate state when newer `main` evidence exists.

## 2026-09-24/25 — Project governance migration candidate

This migration:
- creates root `PROJECT-SPECIFICATIONS.md`;
- creates root `PROJECT-RECORD.md`;
- consolidates the former root `SPECIFICATIONS.md` into the mandatory canonical filename;
- reconciles the Drive source's old repository name;
- preserves Drive-era architecture/product/stability requirements without overwriting newer main state;
- updates README navigation; and
- retires the duplicate root `SPECIFICATIONS.md` on the migration branch after incorporation.

**Drive source:** Project Specification — Gallery.docx  
**Drive file ID:** `1kt0iQrPU0lvZsXw9wmRwhVOjoJ2VH0Ai`  
**Drive deletion status:** Blocked until the migration is accepted, authoritative default-branch readback succeeds, review/check gates pass, and no reconciliation discrepancy remains.

## Ongoing maintenance

Update this file for significant architecture, repository rename/split, Android authority changes, platform-system boundary changes, production/recovery events, lifecycle promotions, migration decisions, or retirement. Routine implementation chronology remains in `CHANGELOGS.md`.
