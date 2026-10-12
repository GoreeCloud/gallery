# GoreeCloud Gallery

**Fork-To-Native • Android • Development (not Stable)**

GoreeCloud Gallery is being established as a GoreeCloud-owned, offline-first media gallery for Android. Its first source foundation imports [ReFra](https://github.com/IacobIonut01/ReFra), licensed under Apache-2.0. This is an independent source import into GoreeCloud/gallery, not an upstream GitHub fork, and not yet a finished native product.

## Verified foundation

- Upstream source commit: 43facd9f5c9a44b21d07c116c23618f3e896097c (October 12, 2026 snapshot)
- Development package: com.goreecloud.gallery.ftn (plus applicable build type suffixes)
- Intended final package: com.goreecloud.gallery, only after explicit migration and validation
- Stack: Kotlin, Jetpack Compose, Android/MediaStore, native codecs, optional on-device ML assets
- Defaults: offline-first, no all-files access, external cloud providers disabled, insecure TLS not allowed
- The imported source has not yet passed a GoreeCloud Android build or device acceptance
- Glaze 1.7 migration remains required; imported upstream Compose styling is not accepted Glaze conformance
- No public or Stable GoreeCloud build has been authorized or signed

## Source verification

    python3 scripts/verify_ftn.py

For Android builds, use Android SDK 37, Java 17 and the pinned Android NDK/CMake plus dependencies declared in the source tree. Upstream ABI and ML variants are retained for migration analysis, not release approval.

Do not publish a build from this initial import without reviewing merged permissions, third-party model and codec licensing, the Android signing/update identity, and representative-device behavior.

## Migration and attribution

- [Fork-To-Native roadmap](docs/ftn/ROADMAP.md)
- [Upstream provenance](UPSTREAM-PROVENANCE.md)
- [Apache License 2.0](LICENSE)
- [Preserved upstream README](docs/upstream/README.md)

ReFra's upstream feature set is a useful starting point, not proof those features work or are accepted in GoreeCloud Gallery. Existing first-party GoreeCloud Gallery work in other authorized repositories must be compared before migration; it has not been overwritten.
