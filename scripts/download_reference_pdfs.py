#!/usr/bin/env python3
"""
Download reference PDFs for the can-can app.

Usage:
    pip install requests
    python3 scripts/download_reference_pdfs.py

PDFs are saved to docs/reference_pdfs/<category>/<filename>.pdf
Run from the repo root. Already-downloaded files are skipped.

Sources:
  - USDA Complete Guide to Home Canning (public domain, NCHFP/UGA)
  - NCHFP fact sheets (public domain)
  - University of Hawaii CTAHR publications (open access)
  - Hawaii DLNR DAR fishing guides and regulations (open access)
  - UC ANR, UAF, Virginia Tech extension publications (open access)
  - Various foraging guides (open access / educational)
"""

import os
import sys
import time

try:
    import requests
except ImportError:
    sys.exit("pip install requests")

OUT_ROOT = os.path.join(os.path.dirname(__file__), "../docs/reference_pdfs")

HEADERS = {
    "User-Agent": (
        "can-can/1.0 (food preservation reference app; "
        "github.com/thepeterstone/can-can)"
    )
}

# ---------------------------------------------------------------------------
# PDF manifest
# Each entry: (category_subdir, filename, url, description)
# ---------------------------------------------------------------------------
PDFS = [
    # ---- USDA Complete Guide to Home Canning (public domain) --------------
    (
        "usda_canning_guide",
        "INTRO_principles.pdf",
        "https://nchfp.uga.edu/papers/guide/INTRO_HomeCanrev0715.pdf",
        "USDA Guide: Introduction — Principles of Home Canning",
    ),
    (
        "usda_canning_guide",
        "GUIDE01_principles.pdf",
        "https://nchfp.uga.edu/papers/guide/GUIDE01_HomeCan_rev0715.pdf",
        "USDA Guide 1: Principles of Home Canning",
    ),
    (
        "usda_canning_guide",
        "GUIDE02_fruits.pdf",
        "https://nchfp.uga.edu/papers/guide/GUIDE02_HomeCan_rev0715.pdf",
        "USDA Guide 2: Selecting, Preparing, and Canning Fruit and Fruit Products",
    ),
    (
        "usda_canning_guide",
        "GUIDE03_tomatoes.pdf",
        "https://nchfp.uga.edu/papers/guide/GUIDE03_HomeCan_rev0715.pdf",
        "USDA Guide 3: Selecting, Preparing, and Canning Tomatoes",
    ),
    (
        "usda_canning_guide",
        "GUIDE04_vegetables.pdf",
        "https://nchfp.uga.edu/papers/guide/GUIDE04_HomeCan_rev0715.pdf",
        "USDA Guide 4: Selecting, Preparing, and Canning Vegetables",
    ),
    (
        "usda_canning_guide",
        "GUIDE05_meats.pdf",
        "https://nchfp.uga.edu/papers/guide/GUIDE05_HomeCan_rev0715.pdf",
        "USDA Guide 5: Preparing and Canning Poultry, Red Meats, and Seafoods",
    ),
    (
        "usda_canning_guide",
        "GUIDE06_fermented_pickles.pdf",
        "https://nchfp.uga.edu/papers/guide/GUIDE06_HomeCan_rev0715.pdf",
        "USDA Guide 6: Preparing and Canning Fermented Foods and Pickled Vegetables",
    ),
    (
        "usda_canning_guide",
        "GUIDE07_jams_jellies.pdf",
        "https://nchfp.uga.edu/papers/guide/GUIDE07_HomeCan_rev0715.pdf",
        "USDA Guide 7: Preparing and Canning Jams and Jellies",
    ),

    # ---- NCHFP fact sheets (public domain) --------------------------------
    (
        "nchfp_factsheets",
        "using_boiling_water_canners.pdf",
        "https://nchfp.uga.edu/papers/factsheets/Preserving_Food__Using_Boiling_Water_Canners.pdf",
        "NCHFP: Preserving Food — Using Boiling Water Canners",
    ),
    (
        "nchfp_factsheets",
        "using_pressure_canners.pdf",
        "https://nchfp.uga.edu/papers/factsheets/Preserving_Food__Using_Pressure_Canners.pdf",
        "NCHFP: Preserving Food — Using Pressure Canners",
    ),
    (
        "nchfp_factsheets",
        "home_canning_water.pdf",
        "https://nchfp.uga.edu/papers/factsheets/home_canning_water.pdf",
        "NCHFP: Home Canning Water (water quality for canning)",
    ),

    # ---- UH CTAHR — Hawaii / Pacific foraging and food plants -------------
    # College of Tropical Agriculture and Human Resources, open access
    (
        "hawaii_ctahr",
        "edible_plants_hawaii_landscapes.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/L-14.pdf",
        "CTAHR L-14: Edible Plants for Hawai'i Landscapes (taro, passion fruit, bamboo, etc.)",
    ),
    (
        "hawaii_ctahr",
        "breadfruit_ethnobotany_nutrition.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/breadfruit.pdf",
        "CTAHR: Hawaiian Breadfruit — Ethnobotany, Nutrition, and Human Ecology",
    ),
    (
        "hawaii_ctahr",
        "ulu_breadfruit_postharvest.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/FN-58.pdf",
        "CTAHR FN-58: 'Ulu (Breadfruit) — Postharvest Handling and Nutrition",
    ),
    (
        "hawaii_ctahr",
        "ten_tropical_fruits.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/RES-085.pdf",
        "CTAHR RES-085: Ten Tropical Fruits of Potential Value (guava, sapodilla, etc.)",
    ),
    (
        "hawaii_ctahr",
        "twelve_tropical_fruits_culinary.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/12fruits.pdf",
        "CTAHR: Twelve Fruits With Potential Value-Added and Culinary Uses",
    ),
    (
        "hawaii_ctahr",
        "south_pacific_island_foods.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/B-110.pdf",
        "CTAHR B-110: Some Tropical South Pacific Island Foods — Description, History, Nutritive Value",
    ),
    (
        "hawaii_ctahr",
        "papaya_food_facts.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/CFS-PA-1A.pdf",
        "CTAHR CFS-PA-1A: Papaya — Food Facts and Nutrition",
    ),
    (
        "hawaii_ctahr",
        "native_plants_for_landscapes.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/of-40.pdf",
        "CTAHR OF-40: Best Native Plants for Hawaiian Landscapes",
    ),
    (
        "hawaii_ctahr",
        "native_hawaiian_plants_commercial.pdf",
        "https://www.ctahr.hawaii.edu/oc/freepubs/pdf/OF-52.pdf",
        "CTAHR OF-52: Commercial Use of Native Hawaiian Plants",
    ),
    (
        "hawaii_ctahr",
        "native_plant_booklet.pdf",
        "https://www.ctahr.hawaii.edu/uhmg/Oahu/downloads/MG-NativePlantBooklet.pdf",
        "CTAHR Master Gardener: Native Hawaiian Plants Booklet",
    ),

    # ---- Drying / Dehydrating (UC ANR — open access) ----------------------
    (
        "food_preservation",
        "home_drying_of_food.pdf",
        "https://ucanr.edu/sites/camasterfoodpreservers/files/336020.pdf",
        "UC ANR: Home Drying of Food (Charlotte Brennand)",
    ),
    (
        "food_preservation",
        "drying_vegetables.pdf",
        "https://ucanr.edu/sites/default/files/2020-12/340865.pdf",
        "UC ANR: Drying Vegetables",
    ),
    (
        "food_preservation",
        "drying_fruits.pdf",
        "https://ucanr.edu/sites/default/files/2020-12/340866.pdf",
        "UC ANR: Drying Fruits",
    ),
    (
        "food_preservation",
        "freeze_drying_home_use.pdf",
        "https://ucanr.edu/sites/default/files/2024-10/403504.pdf",
        "UC ANR: Freeze Drying For Home Use",
    ),

    # ---- Fermentation & Pickling ------------------------------------------
    (
        "food_preservation",
        "pickling_vinegar_fermentation.pdf",
        "https://ucanr.edu/sites/default/files/2022-10/374794.pdf",
        "UC ANR: Pickling — Vinegar and Fermentation",
    ),
    (
        "food_preservation",
        "vegetable_fermentation_vt.pdf",
        "https://www.pubs.ext.vt.edu/content/dam/pubs_ext_vt_edu/FST/fst-328/FST-328.pdf",
        "Virginia Tech Extension FST-328: Vegetable Fermentation",
    ),

    # ---- Freezing (UAF Cooperative Extension — open access) ---------------
    (
        "food_preservation",
        "home_freezing_guide.pdf",
        "https://www.uaf.edu/ces/publications/database/food/files/pdfs/FNH-00740-Freezing-02-01-23.pdf",
        "UAF Extension FNH-00740: Freezing (comprehensive home freezing guide)",
    ),
    (
        "food_preservation",
        "freezing_vegetables.pdf",
        "https://www.uaf.edu/ces/publications/database/food/files/pdfs/FNH-00264.pdf",
        "UAF Extension FNH-00264: Home Freezing of Vegetables",
    ),

    # ---- Canning basics (UC ANR) ------------------------------------------
    (
        "food_preservation",
        "canning_basics_ucce.pdf",
        "https://ucanr.edu/sites/default/files/2022-04/366406.pdf",
        "UC ANR: Preserve It — Canning Basics",
    ),

    # ---- Hawaii DLNR DAR — fishing guides and regulations (open access) ---
    (
        "hawaii_fishing",
        "fishes_of_hawaii.pdf",
        "https://dlnr.hawaii.gov/dar/files/2014/04/fishes_of_hawaii.pdf",
        "DLNR DAR: Fishes of Hawai'i (illustrated nearshore and reef species guide)",
    ),
    (
        "hawaii_fishing",
        "fishing_in_hawaii.pdf",
        "https://dlnr.hawaii.gov/dar/files/2016/03/Fishing_in_Hawaii.pdf",
        "DLNR DAR: Fishing in Hawai'i (recreational fishing guide — methods, species, regulations)",
    ),
    (
        "hawaii_fishing",
        "fishing_regs_may_2025.pdf",
        "https://dlnr.hawaii.gov/dar/files/2025/05/fishing_regs_May_2025.pdf",
        "DLNR DAR: Hawaii Fishing Regulations — May 2025",
    ),

    # ---- Wild edible plants / foraging ------------------------------------
    (
        "foraging",
        "edible_wild_plants_northeast.pdf",
        "https://massland.org/sites/default/files/files/Edible%20Wild%20Plants%20Native%20to%20the%20Northeast%20and%20eastern%20Canada%20-%20March%202018%20compilation.pdf",
        "MassLand / Russ Cohen: Edible Wild Plants Native to the Northeast US and Eastern Canada",
    ),
    (
        "foraging",
        "edible_native_plants_ecola.pdf",
        "https://www.ecolandscaping.org/wp-content/uploads/2013/03/Edible-Native-Plants-of-Mass.-Northeast-U.S.-and-E.-Canada-March-18-2013.pdf",
        "Ecological Landscape Alliance / Russ Cohen: Edible Native Plants — Northeast US and Eastern Canada",
    ),
    (
        "foraging",
        "edible_coastal_prairie_plants.pdf",
        "https://txmn.org/coastal/files/2010/03/Edible-Wild-Plants-of-the-Coastal-Prairie.pdf",
        "Texas Extension: Edible Wild Plants of the Coastal Prairie",
    ),
]


def download(category: str, filename: str, url: str, description: str) -> str:
    dest_dir = os.path.join(OUT_ROOT, category)
    os.makedirs(dest_dir, exist_ok=True)
    dest = os.path.join(dest_dir, filename)

    if os.path.exists(dest) and os.path.getsize(dest) > 1024:
        size_kb = os.path.getsize(dest) // 1024
        return f"skip  [{size_kb:>5} KB]  {filename}"

    try:
        r = requests.get(url, headers=HEADERS, timeout=30, stream=True)
        r.raise_for_status()
        content_type = r.headers.get("content-type", "")
        if "html" in content_type and "pdf" not in content_type:
            return f"SKIP  [HTML response — URL may have changed]  {url}"
        with open(dest, "wb") as f:
            for chunk in r.iter_content(chunk_size=65536):
                f.write(chunk)
        size_kb = os.path.getsize(dest) // 1024
        return f"ok    [{size_kb:>5} KB]  {filename}"
    except requests.HTTPError as e:
        return f"FAIL  [{e.response.status_code}]  {url}"
    except Exception as e:
        return f"FAIL  [{type(e).__name__}]  {url}  — {e}"


def main():
    os.makedirs(OUT_ROOT, exist_ok=True)
    print(f"Saving PDFs to: {os.path.abspath(OUT_ROOT)}\n")

    categories_seen: set[str] = set()
    ok = fail = skip = 0

    for entry in PDFS:
        category, filename, url, description = entry
        if category not in categories_seen:
            print(f"\n── {category.upper().replace('_', ' ')} ──")
            categories_seen.add(category)
        print(f"  {description}")
        result = download(category, filename, url, description)
        status = result.split()[0]
        if status == "ok":
            ok += 1
        elif status == "skip":
            skip += 1
        else:
            fail += 1
        print(f"    {result}")
        if status not in ("skip",):
            time.sleep(0.75)

    total = ok + fail + skip
    print(f"\n{'─'*60}")
    print(f"Done: {ok} downloaded, {skip} already present, {fail} failed  ({total} total)")

    if fail > 0:
        print(
            "\nFailed downloads often mean the URL has moved. Check the source"
            " site and update PDFS[] in this script."
        )

    print(f"\nPDFs saved to: {os.path.abspath(OUT_ROOT)}")
    print("To add to git: git add docs/reference_pdfs/ && git commit")


if __name__ == "__main__":
    main()
