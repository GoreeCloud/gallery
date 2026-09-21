# GoreeCloud Gallery Glaze UI Contract

## Purpose

This document defines the repository-local Glaze UI implementation and release-review contract for GoreeCloud Gallery. It supplements the authoritative GoreeCloud Glaze UI Design Language and Application Branding and User Interface Design standards; it does not replace them.

GoreeCloud Gallery is a GoreeCloud-maintained Android fork. Every Gallery-controlled user-facing surface must therefore use Glaze UI unless a material technical, platform, legal, licensing, accessibility, or interoperability constraint is explicitly documented as an approved exception.

## Conformance target

- Target design system: **Glaze UI 1.6.0**
- Canonical repository: `GoreeCloud/glaze-ui`
- Accepted Stable release source: `a7180679ea851389e0f3004515f9a25f420e716d`
- Source qualification anchor: `c7509c79256b04b0aa67cb9dd0737d7588e0ae4a`
- Stable runtime entrypoint: `js/glaze-v1.6.0.mjs`
- Known-good shared rollback baseline: **1.5.1**
- Native implementation model: Android platform-native semantic mapping rather than copied web CSS
- Current first-party native source baseline: **V1.6**
- Historical transitional implementation line: `gc.16`
- Permanent Glaze UI exceptions: **none approved**

## Product identity

The user-facing product identity is **GoreeCloud Gallery**. Upstream Fossify origin, copyright, licensing, and attribution remain preserved in source and legal records, but the ordinary application experience must not present Fossify as the primary product identity.

The application ID is `com.goreecloud.gallery`. GoreeCloud-owned branding, package metadata, launcher presentation, color identity, and visible product terminology must remain consistent with that identity.

## Native semantic mapping

Gallery maps the Official Stable Glaze UI 1.6 contract into Android resources and platform conventions. The mapping is semantic and presentation-only: it preserves roles, hierarchy, interaction intent, accessibility boundaries, and authority separation without requiring Android to reproduce browser-only effects or allowing Glaze presentation to manufacture permission, authorization, privacy/security truth, or consequential execution.

The `gc.11` foundation establishes native resources for Canvas and Canvas Accent colors, Surface and muted-surface roles, primary and secondary accents, semantic text/status roles, spacing, rounded geometry, 44dp minimum and 48dp comfortable actionable targets, 90/160/220/320ms motion semantics, light/dark palettes, and Compact-first Settings composition.

The `gc.12` browsing layer extends those same semantics into the folders screen and opened-folder media screen with adaptive media-aware gutters, Canvas depth, branded menu chrome, Raised empty-state actions, comfortable action targets, and accent loading feedback.

The `gc.13` search layer extends the same browsing composition into global Gallery search, including the shared Canvas, GoreeCloud accent-gradient search/menu chrome, adaptive browsing gutters, and a rounded muted Raised empty-state surface.

The `gc.14` media-viewer layer applies a restrained muted Glaze overlay to full-screen viewer chrome and standardizes visible viewer actions on 48dp comfortable targets while deliberately leaving the photo or video itself visually dominant.

The `gc.15` transient-surface refinement applies the same semantic system to sorting, grouping, filtering, destructive confirmations, overflow menus, and Settings density after representative-device visual review. It replaces generic transient white surfaces with coordinated Glaze light/dark presentation, removes redundant Settings dividers, tightens excessive card spacing, and strengthens the Settings app-bar treatment without changing application behavior.

The `gc.16` dialog-geometry refinement responds to representative-device review of gc.15 by making sorting, grouping, and media-filter dialog ScrollViews measure to their actual content instead of taking the full available dialog height. This keeps the rounded Glaze surface attached to its controls, removes excessive empty space, and preserves scrollability when content genuinely exceeds the available viewport.

These resources are intentionally local. Gallery does not load Glaze UI from a remote runtime, remote font service, icon CDN, analytics dependency, or network-delivered style package.

## Surface hierarchy

Gallery uses the Glaze UI hierarchy according to the needs of a media application:

- **Canvas** — atmospheric page background behind Gallery-controlled structural surfaces.
- **Solid** — readability-first content surfaces and platform-native fallback behavior.
- **Raised** — settings rows, empty-state actions and messages, cards, and important grouped controls with restrained separation.
- **Glaze** — navigation/app-bar emphasis and selected layered presentation where Android can provide the treatment without harming readability or performance.
- **Overlay** — dialogs, sheets, menus, viewer controls, and other attention-priority surfaces using platform-native Android presentation.

Glass is not required everywhere. Media remains visually dominant, and solid/native fallbacks are preferred whenever transparency or blur would reduce clarity, accessibility, performance, or platform consistency.

## Glaze UI design principles for Gallery

Gallery applies Glaze UI to a local-media Android experience using the following product-specific interpretation:

- clear Material-derived structure and predictable Android interaction patterns;
- softened, rounded geometry for media thumbnails, controls, dialogs, menus, and structural surfaces where technically appropriate;
- layered surfaces and restrained depth that improve hierarchy without obscuring media content;
- selective translucency or visual depth only where readability, performance, and accessibility remain strong;
- purposeful GoreeCloud gradients for identity and hierarchy rather than decoration alone;
- high-quality light and dark presentation with coherent foreground/background contrast;
- ergonomic controls and practical mobile touch targets;
- restrained motion that communicates state or relationship and respects reduced-motion behavior where applicable;
- privacy-conscious, offline-first presentation with no analytics, advertising, tracking, cloud account, or remote-content dependency.

## Settings integration

Settings is a primary GoreeCloud-owned surface and receives an explicit Glaze UI treatment beginning in `gc.11`:

- the page uses a Glaze Canvas gradient rather than an undifferentiated flat background;
- the top app bar uses the GoreeCloud primary-to-secondary accent gradient;
- setting rows use rounded Raised surfaces with a restrained line and Android ripple feedback;
- interactive rows enforce a 48dp comfortable minimum height;
- content uses a 16dp Compact-first horizontal inset, 32dp at `sw600dp`, and 64dp at `sw840dp`;
- light and dark Glaze semantic palettes are defined separately;
- the existing **Privacy & permissions** entry remains platform-native and delegates permission administration to Android;
- beginning in `gc.15`, redundant Settings dividers are hidden where Raised surfaces already establish grouping, row spacing is tightened, and the app-bar treatment is explicitly reinforced across both the app-bar container and toolbar.

The adaptive Android resource qualifiers are platform-native equivalents of Glaze UI's Compact/Medium/Expanded/Wide principle; they are not a claim that Android `sw600dp` and `sw840dp` exactly equal the web breakpoints.

## Browsing-surface integration

The folder browser and opened-folder media grid are Gallery's highest-frequency surfaces and receive an explicit Glaze UI treatment in `gc.12`:

- both pages use the same semantic Glaze Canvas as Settings, establishing application-wide visual continuity;
- the Gallery search/menu chrome uses the GoreeCloud accent gradient while preserving the existing Android search/menu implementation;
- browsing content uses a deliberately subtle 8dp horizontal gutter on Compact layouts, 16dp at `sw600dp`, and 24dp at `sw840dp`;
- grids retain media dominance and receive only restrained bottom breathing room rather than cardifying every thumbnail;
- filter/search switching and empty-state recovery actions use 48dp comfortable targets and rounded Raised/ripple treatment;
- the media loading indicator uses the semantic GoreeCloud accent;
- no blur, remote asset, web runtime, or network dependency is required for these surfaces.

Browsing insets are intentionally smaller than Settings insets. A media grid is primary content and should remain visually expansive; Glaze UI provides structure and identity without reducing useful media density.

## Search integration

Global Gallery search receives the same media-first Glaze composition in `gc.13`:

- search uses the shared Glaze Canvas and GoreeCloud accent-gradient search/menu chrome;
- results reuse the gc.12 adaptive browsing gutter contract so search density matches folder and media browsing;
- the result grid receives restrained bottom breathing room without decorative thumbnail cards;
- the no-results state uses a muted Raised surface with rounded geometry and a restrained semantic outline rather than floating unstructured text;
- the search surface remains fully local and introduces no remote UI, font, analytics, advertising, tracking, or network dependency.

This search treatment is intentionally evolutionary rather than a separate visual mode. Search should feel like part of Gallery, not like a detached utility screen.

## Media-viewer integration

The full-screen media viewer receives a restrained Overlay treatment in `gc.14`:

- the viewer toolbar uses the semantic muted Glaze surface rather than a separate branded gradient so controls remain subordinate to the media;
- the bottom action area uses a rounded-top muted Glaze overlay with a restrained semantic outline;
- visible viewer actions use 48dp comfortable targets and retain their existing Android selection feedback and content descriptions;
- photos and videos remain unframed primary content; the design system is applied to chrome rather than placed over the media as decorative cards;
- delete and other destructive operations retain their existing behavior and confirmation requirements;
- no blur, remote asset, analytics, tracking, advertising, or network dependency is added.

The media viewer intentionally uses a quieter Glaze treatment than Settings or browsing navigation. GoreeCloud identity should be recognizable in the controls without competing with the user’s photo or video.

## Transient surfaces and dialogs

Representative-device review identified dialogs and popup menus as the strongest remaining visual mismatch after gc.14. The `gc.15` refinement therefore establishes the following transient-surface contract:

- sorting, grouping, and media-filter dialogs use a local rounded Glaze Surface with a restrained semantic outline;
- radio buttons and checkboxes use the GoreeCloud semantic accent and 48dp comfortable minimum targets;
- unnecessary divider rules inside these dialogs are removed where spacing and grouping already communicate structure;
- destructive confirmation dialogs retain confirmation behavior but use the semantic danger color for warning text and the same rounded Glaze surface language;
- toolbar overflow menus use coordinated Glaze light/dark surfaces with rounded geometry and a subtle accent outline instead of visually generic white/dark rectangles;
- dialog/window styling must not change sorting, grouping, filtering, destructive-operation, permission, or file-operation semantics.

The `gc.16` refinement adds a geometry rule for the same transient surfaces: dialog content must use content-driven height by default and only scroll when the content exceeds the available viewport. A Glaze dialog must not reserve large empty regions merely because its root is scrollable.

Transient surfaces are treated as Overlay-level hierarchy: visually distinct enough to command attention, but not more visually dominant than the media or the action being confirmed.

## Accepted Gallery-specific visual invariants

The following are release-significant GoreeCloud presentation decisions and must not silently regress:

- file thumbnails use rounded corners;
- folder thumbnails use the rounded GoreeCloud presentation;
- square-thumbnail selection controls removed by GoreeCloud do not return;
- thumbnail cropping remains disabled by the GoreeCloud policy;
- toolbar overflow menus remain readable in both light and dark appearance modes;
- dialogs preserve readable foreground/background contrast;
- destructive actions remain visually distinguishable and retain the intended confirmation behavior;
- the main folder view and opened media-folder view remain visibly consistent with the accepted GoreeCloud Gallery presentation;
- primary user-facing Fossify branding does not reappear during upstream synchronization;
- Settings retains the Glaze Canvas/Raised/Glaze hierarchy and practical target sizing introduced in gc.11;
- folder and media browsing retain the gc.12 Canvas, adaptive gutter, Raised empty-action, and branded menu semantics without turning thumbnails into decorative cards;
- search retains the gc.13 Canvas, branded search/menu chrome, adaptive browsing composition, and muted Raised empty-state treatment;
- the media viewer retains the gc.14 muted Glaze toolbar/action overlay and comfortable action targets without obscuring primary media or changing destructive-action behavior;
- transient surfaces retain the gc.15 rounded Glaze dialog/popup treatment, semantic control accents, compact Settings grouping, and unchanged behavioral semantics;
- sorting, grouping, and media-filter dialogs retain gc.16 content-driven height and scroll only when required by available space.

Where an invariant can be represented as pure behavior, GoreeCloud-owned automated tests should protect it. Where Android framework rendering, accessibility services, device profiles, permissions, or media operations are required, the corresponding real-device gate remains mandatory.

## Theme and appearance contract

Gallery must provide coherent light and dark presentation on supported Android configurations. Theme work must be reviewed across primary and secondary surfaces, including main folder and media-folder views, top app bars and overflow menus, search, Settings, dialogs and confirmation surfaces, empty/error states, media viewers/editing surfaces, share/open-with flows where Gallery controls presentation, recycle-bin/destructive flows, and permission-related application surfaces.

A surface is not considered Glaze UI compliant merely because its background color matches the GoreeCloud palette. Typography, spacing, geometry, hierarchy, interaction states, contrast, and feedback must remain coherent as a system.

## Accessibility and resilience contract

Visual quality does not override accessibility. Stable-release review must include, where applicable, TalkBack identification, meaningful labels for icon-only controls, practical touch-target sizing, readable contrast, large-font and increased-display-size behavior, usable focus order, reduced-motion behavior, and safe solid-surface presentation when advanced effects are unavailable or inappropriate.

The gc.11 Settings treatment, gc.12 browsing actions, gc.14 media-viewer controls, and gc.15 dialog controls use 48dp comfortable interactive targets. The gc.13 search empty state, gc.14 viewer overlay, and gc.15 dialogs use solid semantic surfaces rather than depending on translucency for readability. gc.16 keeps those dialog surfaces content-sized while preserving scrollability for large-font or constrained-height cases. Any Glaze UI treatment that materially weakens accessibility must be revised even if it matches the preferred aesthetic.

## Privacy and dependency boundary

Glaze UI assets and behavior must remain local to the application or its approved open-source build dependencies. Gallery must not introduce remote fonts, remote icon libraries, analytics resources, tracking pixels, advertising resources, or other network-hosted presentation dependencies.

The packaged application is expected to remain without `android.permission.INTERNET`. A visual enhancement must not weaken that offline/privacy boundary.

## Maintained-fork and upstream-sync review

Every upstream synchronization must include a user-interface regression review for newly introduced or restored upstream names/logos, launcher/application icons, colors/themes/styles/assets, settings/onboarding terminology, menus/dialogs/empty states/errors, promotional links, accessibility regressions, and presentation controls intentionally removed by GoreeCloud.

Required legal attribution must remain preserved. Rebranding does not remove license obligations.

## Automated conformance boundary

Repository validation must require this contract to remain present. Source validation must fail closed when accepted GoreeCloud presentation invariants or GoreeCloud-owned behavioral-test sources disappear.

The gc.11 source validation requires the Glaze semantic resource files, light/dark mappings, 44/48dp target contract, four motion timing resources, adaptive Settings insets, Canvas/toolbar/row drawables, application of those resources in Settings, and a local-only dependency boundary.

The gc.12 validation additionally requires adaptive browsing insets, the local Raised empty-action drawable, Canvas and menu treatment on both primary browsing screens, comfortable empty-state action targets, and semantic loading-accent application.

The gc.13 validation additionally requires the search screen to use the shared Canvas, branded toolbar, adaptive browsing insets, local rounded muted empty-state drawable, and current test-build version identity.

The gc.14 validation additionally requires the full-screen media viewer to use the muted Glaze toolbar surface, local rounded viewer-action overlay, at least eight comfortable viewer action targets, current version identity, and the local-only resource boundary.

The gc.15 validation additionally requires rounded Glaze surfaces on sorting/grouping/filter dialogs, semantic accent and comfortable targets on their controls, semantic danger presentation for destructive warnings, reduced redundant Settings dividers, reinforced Settings header treatment, coordinated light/dark popup surfaces, current version identity, and preservation of the local-only resource boundary.

The gc.16 validation additionally requires sorting, grouping, and media-filter dialog roots to use `wrap_content` height, disable forced viewport filling, retain overflow scrolling when needed, preserve the gc.15 Glaze surface, and carry the current test-build version identity.

The ordinary acceptance workflow must verify that meaningful GoreeCloud-owned JVM tests actually execute. A successful Gradle task with `NO-SOURCE`, missing XML results, zero executed tests, or failing/error tests is not acceptable behavioral-test evidence.

Automated conformance complements rather than replaces real-device Glaze UI acceptance. Android rendering, TalkBack behavior, user/profile isolation, permission handling, destructive media operations, upgrade/recovery, and final signed-build presentation remain manual or device-dependent release gates.

## Exception model

No permanent Glaze UI exception is approved for GoreeCloud Gallery at this time.

If a future material constraint prevents compliance, the exception record must identify the affected surface or behavior, the requirement that cannot be met, the reason, user-visible impact, compensating or approved alternative, owner, review condition, and condition for removal. Convenience, schedule pressure, upstream defaults, or unfinished redesign work are not production exceptions.

## Stable-release boundary

Glaze UI compliance is a blocking Stable-release requirement. Stable promotion requires both automated conformance evidence and the applicable real-device visual/accessibility acceptance in `docs/STABLE-RELEASE-CHECKLIST.md`.

A green build, successful APK assembly, or successful signing workflow alone does not establish Glaze UI production readiness.
