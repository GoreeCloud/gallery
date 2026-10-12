#!/usr/bin/env python3
"""Dependency-free source policy checks; NOT an Android build/security audit."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

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
ANDROID = "{http://schemas.android.com/apk/res/android}"
manifest = ET.parse(ROOT / "app/src/main/AndroidManifest.xml").getroot()
app = manifest.find("application")
assert app is not None and app.get(ANDROID + "allowBackup") == "false"
legacy = ET.parse(ROOT / "app/src/main/res/xml/backup_rules.xml").getroot()
assert {("root", "."), ("external", ".")} <= {
    (entry.get("domain"), entry.get("path")) for entry in legacy.findall("exclude")
}
modern = ET.parse(ROOT / "app/src/main/res/xml/data_extraction_rules.xml").getroot()
for mode in ("cloud-backup", "device-transfer"):
    section = modern.find(mode)
    assert section is not None, mode
    excludes = {(item.get("domain"), item.get("path")) for item in section.findall("exclude")}
    assert {("root", "."), ("external", ".")} <= excludes, mode
network = ET.parse(ROOT / "app/src/main/res/xml/network_security_config.xml").getroot()
assert network.find("base-config").get("cleartextTrafficPermitted") == "false"
for permission in ("INTERNET", "MANAGE_EXTERNAL_STORAGE", "ACCESS_WIFI_STATE",
                   "ACCESS_NETWORK_STATE", "ACCESS_LOCAL_NETWORK",
                   "CHANGE_WIFI_MULTICAST_STATE", "ACCESS_FINE_LOCATION"):
    assert f'"android.permission.{permission}"' in gradle, permission
assert "if (isOffline)" in gradle and "stripPermissions.set(" in gradle
resolver = (ROOT / "app/src/main/kotlin/com/dot/gallery/cloud/core/CloudUri.kt").read_text()
assert "getByConfigId(configId)?.takeIf { it.providerType == providerType } else getAllForType(providerType).singleOrNull()" in resolver
assert 'params["cfg"]?.toLongOrNull()?.takeIf { it > 0L } ?: return null' in resolver
assert (ROOT / "LICENSE").is_file()
assert (ROOT / "UPSTREAM-PROVENANCE.md").is_file()
assert not (ROOT / ".github/workflows/stable.yml").exists()
assert not (ROOT / ".github/workflows/nightly.yml").exists()
print("PASS: FTN source checks. Build/APK/device validation is still required.")
