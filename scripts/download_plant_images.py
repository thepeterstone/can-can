#!/usr/bin/env python3
"""
Download Wikipedia thumbnails for foraging guide plant entries.

Run this once from the repo root:
    python3 scripts/download_plant_images.py

Images are saved to app/src/main/assets/reference/images/
Commit the downloaded images to include them in the APK bundle.

Requires: Pillow  (pip install Pillow)  — for WebP conversion
          requests (pip install requests)
"""

import json
import os
import time
import sys

try:
    import requests
except ImportError:
    sys.exit("pip install requests")

try:
    from PIL import Image
    import io
    HAS_PILLOW = True
except ImportError:
    HAS_PILLOW = False
    print("Warning: Pillow not found — images saved as JPEG without WebP conversion")
    print("  pip install Pillow  to enable WebP output")

WIKI_API = "https://en.wikipedia.org/api/rest_v1/page/summary/{title}"
HEADERS = {"User-Agent": "can-can/1.0 (Android food preservation app; github.com/thepeterstone/can-can)"}
OUT_DIR = os.path.join(os.path.dirname(__file__), "../app/src/main/assets/reference/images")
FORAGING_JSON = os.path.join(os.path.dirname(__file__), "../app/src/main/assets/reference/foraging_guide.json")

# Target dimensions: large enough to look good at 220dp on hdpi/xhdpi screens
TARGET_WIDTH = 640


def fetch_thumbnail_url(wiki_title: str) -> str | None:
    url = WIKI_API.format(title=wiki_title.replace(" ", "_"))
    try:
        r = requests.get(url, headers=HEADERS, timeout=10)
        r.raise_for_status()
        data = r.json()
        return data.get("originalimage", {}).get("source") or data.get("thumbnail", {}).get("source")
    except Exception as e:
        print(f"  API error for {wiki_title}: {e}")
        return None


def download_and_save(item_id: str, wiki_title: str) -> bool:
    ext = "webp" if HAS_PILLOW else "jpg"
    dest = os.path.join(OUT_DIR, f"{item_id}.{ext}")
    if os.path.exists(dest):
        print(f"  {item_id}: already downloaded, skipping")
        return True

    print(f"  {item_id}: fetching URL from Wikipedia ({wiki_title})…")
    thumb_url = fetch_thumbnail_url(wiki_title)
    if not thumb_url:
        print(f"  {item_id}: no thumbnail found")
        return False

    print(f"  {item_id}: downloading {thumb_url[:80]}…")
    try:
        r = requests.get(thumb_url, headers=HEADERS, timeout=20)
        r.raise_for_status()
        raw = r.content

        if HAS_PILLOW:
            img = Image.open(io.BytesIO(raw)).convert("RGB")
            if img.width > TARGET_WIDTH:
                ratio = TARGET_WIDTH / img.width
                img = img.resize((TARGET_WIDTH, int(img.height * ratio)), Image.LANCZOS)
            img.save(dest, "WEBP", quality=82)
            print(f"  {item_id}: saved {img.width}×{img.height} WebP ({os.path.getsize(dest)//1024}KB)")
        else:
            with open(dest, "wb") as f:
                f.write(raw)
            print(f"  {item_id}: saved JPEG ({os.path.getsize(dest)//1024}KB)")

        return True
    except Exception as e:
        print(f"  {item_id}: download failed — {e}")
        return False


def main():
    os.makedirs(OUT_DIR, exist_ok=True)

    with open(FORAGING_JSON) as f:
        items = json.load(f)["items"]

    plant_items = [i for i in items if i.get("wikipedia_title")]
    print(f"Found {len(plant_items)} plant entries with Wikipedia titles\n")

    ok, fail = 0, 0
    for item in plant_items:
        result = download_and_save(item["id"], item["wikipedia_title"])
        if result:
            ok += 1
        else:
            fail += 1
        time.sleep(0.5)  # be polite to Wikipedia

    print(f"\nDone: {ok} downloaded, {fail} failed")
    if ok > 0:
        print(f"Images in: {os.path.abspath(OUT_DIR)}")
        print("Next: git add app/src/main/assets/reference/images/ && git commit")


if __name__ == "__main__":
    main()
