# GoreeCloud Gallery

**Fork-To-Native · Android · Development — not Stable**

GoreeCloud Gallery is an offline-first Android photo and video gallery under independent GoreeCloud ownership. Its initial source was imported from the Apache-2.0 licensed [ReFra](https://github.com/IacobIonut01/ReFra). An upstream-source import is **not** completion of a native GoreeCloud application.

## Source and compatibility

- Upstream source: `IacobIonut01/ReFra@43facd9f5c9a44b21d07c116c23618f3e896097c` (October 12, 2026 snapshot).
- Development application ID: `com.goreecloud.gallery.ftn` (additional build-type suffixes may apply). The intended canonical ID `com.goreecloud.gallery` requires a separate accepted migration.
- Stack: Android, Kotlin, Jetpack Compose, MediaStore, inherited native codecs and optional local ML models.
- Default configuration: offline-first, no all-files access, no inherited cloud providers, and no insecure TLS.
- Android private app-data backup is disabled and Android extraction rules exclude app-owned data at source level. Compiled manifest and device validation remain required.
- Glaze 1.7 presentation, security/privacy and complete first-party feature ownership remain unfinished. No signed GoreeCloud release or Stable acceptance is claimed.

## Feature documentation

- [Planned Features and Capabilities](docs/PLANNED-FEATURES.md) — canonical 18-section **proposal**, not implemented-feature evidence.
- [Implemented Features](docs/IMPLEMENTED-FEATURES.md) — evidence-backed first-party implementation ledger; currently limited pending validation.
- [Fork-To-Native Roadmap](docs/ftn/ROADMAP.md) — source transformation milestones, blockers and release gates.
- [Upstream Provenance](UPSTREAM-PROVENANCE.md), [Apache-2.0 License](LICENSE) and [preserved upstream README](docs/upstream/README.md).

## Validation

Run `python3 scripts/verify_ftn.py` for deterministic source policy checks. GitHub Actions also attempts to compile the offline Android merged manifest and check removed permissions; review the latest CI result rather than assuming it passed.

A release requires a clean Android build, merged-manifest checks, dependency and ML-model license review, real-device and profile testing, Glaze acceptance, security/privacy controls, and persistent Development signing plus upgrade/recovery evidence.

Preserve and compare the earlier GoreeCloud Gallery lineage documented under `GoreeCloud/android-app-defaults`; this import does not establish feature parity or authorize replacing an existing Gallery installation.
