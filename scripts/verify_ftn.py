#!/usr/bin/env python3
"""Dependency-free source policy checks; NOT an Android build/security audit."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
props = dict(
    line.split("=", 1) for line in (ROOT / "app.properties").read_text().splitlines()
    if "=" in line and not line.lstrip().startswith("#")
)
assert props.get("OFFLINE") == "true", "Offline-first must be enabled"
assert props.get("ALL_FILES_ACCESS") == "false", "All-files access must be off"
for key in ("INCLUDE_IMMICH", "INCLUDE_OWNCLOUD", "INCLUDE_NEXTCLOUD",
            "INCLUDE_WEBDAV", "INCLUDE_SMB", "INCLUDE_NFS"):
    assert props.get(key) == "false", key + " must default off"
gradle = (ROOT / "app/build.gradle.kts").read_text()
assert 'applicationId = "com.goreecloud.gallery.ftn"' in gradle
assert '"ALLOW_INSECURE_TLS", "true"' not in gradle
assert '"android.permission.INTERNET"' in gradle
assert '"android.permission.MANAGE_EXTERNAL_STORAGE"' in gradle
assert re.search(r'(?m)^\s*<string name="app_name">GoreeCloud Gallery</string>',
                 (ROOT / "app/src/main/res/values/strings.xml").read_text())
assert (ROOT / "LICENSE").is_file()
assert (ROOT / "UPSTREAM-PROVENANCE.md").is_file()
assert not (ROOT / ".github/workflows/stable.yml").exists()
assert not (ROOT / ".github/workflows/nightly.yml").exists()
print("PASS: FTN source checks. Build/APK/device validation is still required.")
