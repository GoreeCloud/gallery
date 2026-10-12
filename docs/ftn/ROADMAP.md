# GoreeCloud Gallery — Fork-To-Native roadmap

**Development / imported-source stage as of 2026-10-12.**

## Foundation
- [x] Verify the authorized GoreeCloud destination and identity
- [x] Identify ReFra upstream revision and Apache-2.0 license
- [x] Import source independently without activating upstream release workflows
- [x] Isolate Android Development identity and start with offline/least-privilege settings
- [x] Disable Android app auto-backup, exclude app-owned files from Android cloud/device transfer rules, and disable cleartext connections by default at source level
- [x] Add static source-policy assertions for private-data backup and network defaults
- [ ] Confirm protection in the actual merged APK manifest on a representative Android version
- [x] Reuse the authorized GoreeCloud Android SDK via ignored local.properties; Gradle 9.5.0 :app:tasks succeeds after NDK 29.0.14206865 installation (not an APK build)
- [ ] Verify all inherited licenses including ML assets and native codecs
- [ ] Perform a clean Android build and inspect merged manifest permissions and providers
- [ ] Test upgrade/install and app profile isolation

## GoreeCloud identity and feature preservation
- [ ] Audit and replace remaining upstream branding, icons, external links and promotional components
- [ ] Implement approved Stable Glaze 1.7 native tokens, components, interactions and adaptive layouts
- [ ] Compare and preserve photos/timeline, albums, videos, favorites, trash, media viewer/editor, sharing and search
- [ ] Reconcile first-party GoreeCloud Gallery's prior Samsung Gallery-inspired product flows without copying proprietary material
- [ ] Rework onboarding and settings to match implemented, verified behavior

## Native ownership and security
- [ ] Replace inherited product-defining architecture with GoreeCloud-owned media domain and Android adapters
- [ ] Validate permissions, MediaStore mutations, user/profile isolation, vault encryption and biometric authorization
- [ ] Remove unnecessary upstream cloud/analytics/subscription dependencies and network privileges
- [ ] Integrate applicable Privacy Shield, Wardveil, Everkeep, Glaze and other integral system contracts
- [ ] Record deterministic tests, privacy/security evidence and migration exit criteria

## Release gates
- [ ] Build, lint, tests and exact-head CI with secure dependencies
- [ ] Representative physical-device, OEM/profile, accessibility and performance testing
- [ ] Protected Development signing identity; versionCode and upgrade/recovery verification
- [ ] User manual and canonical GoreeCloud documentation/inventory reconciliation
- [ ] Explicit release authorization and Stable acceptance; neither is claimed by source import

**Compatibility:** Preexisting Gallery development was separately documented under GoreeCloud/android-app-defaults. The current import must not overwrite or silently replace that lineage or an existing installation.
