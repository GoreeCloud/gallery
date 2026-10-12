#!/usr/bin/env python3
"""Check compiled universal NoML debug manifest. Fails closed on unknown layout."""
from pathlib import Path
from xml.etree import ElementTree as ET

build = Path("app/build/intermediates")
matches = [
    path for path in build.rglob("AndroidManifest.xml")
    if "universalNoMLDebug" in str(path)
    and ("merged_manifest" in str(path) or "merged_manifests" in str(path))
]
if not matches:
    raise SystemExit("FAIL: no merged universalNoMLDebug AndroidManifest.xml found")

android = "{http://schemas.android.com/apk/res/android}"
for path in matches:
    root = ET.parse(path).getroot()
    app = root.find("application")
    if app is None or app.get(android + "allowBackup") != "false":
        raise SystemExit(f"FAIL: automatic backup not disabled in {path}")
    denied = {
        "android.permission.INTERNET",
        "android.permission.MANAGE_EXTERNAL_STORAGE",
        "android.permission.ACCESS_LOCAL_NETWORK",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_WIFI_STATE",
        "android.permission.ACCESS_NETWORK_STATE",
        "android.permission.CHANGE_WIFI_MULTICAST_STATE",
    }
    declared = {
        item.get(android + "name")
        for item in root
        if item.tag.startswith("uses-permission")
    }
    forbidden = declared.intersection(denied)
    if forbidden:
        raise SystemExit(f"FAIL: offline manifest {path} declares {sorted(forbidden)}")
print(f"PASS: offline merged manifest verified ({len(matches)} candidate files)")
