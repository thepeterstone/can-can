# can-can — Worklog

## Current State (as of 2026-05-21)

**Branch:** `claude/west-hawaii-content-53619`
**Session focus:** Add West Hawaii fishing, foraging, and recipes content

---

## What's Built

### Reference Screen (complete)
- Offline USDA canning lookup: 66 items across Tomatoes, Vegetables, Fruits, Jams, Pickles, Meats
- Each item has full verbatim USDA sections: Quantity, Quality, Preparation, Procedure, Altitude Adjustments (~187 KB JSON)
- Foraging guide: 28 entries with Wikipedia titles for image loading
- Fermentation guide: 12 guides (sauerkraut, kimchi, kombucha, kefir, sourdough, etc.)
- Preservation guide: 12 guides across Dehydrating, Smoking & Curing, Root Cellaring, Freeze Drying
- Search + category filter chip row
- Expand-in-place cards with AnimatedVisibility
- Images: asset-first (bundled WebP via `scripts/download_plant_images.py`), Wikipedia fallback
- `WikipediaImageRepository`: checks `assets/reference/images/<id>.webp` first, then fetches Wikipedia API
- `ReferenceRepository`: loads 4 JSON asset files lazily via `flatMap`

### Firebase (complete)
- Analytics: `SCREEN_VIEW` events fired on navigation route changes in `AppNavHost`
- Crashlytics: enabled in `CanCanApp.onCreate()`
- `google-services.json` committed (keys are package-restricted, intentional)

### Infrastructure
- GitHub Actions CI: builds debug + unsigned release APKs; Google Maven blocked in cloud, so all compilation happens via CI
- Session-start hook: `.claude/hooks/session-start.sh` sets git hooks path, validates Gradle wrapper
- In-app debug logger: `CanCanLogger` — primary debugging tool (no ADB required)
- INTERNET permission in AndroidManifest

### Scripts (in `scripts/`)
- `download_plant_images.py` — downloads Wikipedia thumbnails for foraging guide plants; run locally, commit images to `assets/reference/images/`
- `download_reference_pdfs.py` — downloads 33 PDFs (USDA guides, NCHFP fact sheets, UH CTAHR Hawaii plant guides, UC ANR/UAF preservation guides, foraging guides) to `docs/reference_pdfs/` (gitignored)
- `reference_data/sections_tomatoes_fruits.py` + `sections_data.py` — source data used to populate canning_guide.json sections; kept for reference

### Reading Room (in progress — 2026-05-21)
- New feature: offline PDF viewer for bundled reference documents
- `reading_room/` package: `ReadingRoomDocument`, `ReadingRoomRepository` (33 hardcoded docs), `ReadingRoomViewModel`, `PdfViewerViewModel`
- `ReadingRoomScreen`: category filter chips + document card list
- `PdfViewerScreen`: PdfRenderer-based page viewer with TopAppBar + back nav
- Navigation: `Screen.ReadingRoom` added to bottom nav; `"reading_room/{documentId}"` for PDF viewer
- PDFs: bundled under `app/src/main/assets/reading_room/` — must run `scripts/download_reference_pdfs.py` then copy to assets, or download directly
- Unit tests: `ReadingRoomRepositoryTest`, `ReadingRoomViewModelTest`
- **PDF assets are NOT committed** — they are ~20–50 MB and must be downloaded locally then committed

---

### West Hawaii Content (complete — 2026-05-21)
- `reference/fishing_guide.json` (new): 9 Hawaii Fishing guide entries — principles, ahi, mahimahi, ono, opakapaka, akule/opelu, ulua/papio, limu, opihi
- `reference/foraging_guide.json`: +7 Hawaii Foraging entries — lilikoi, guava, mountain apple, breadfruit/ʻulu, poha, kukui, noni
- `recipes/recipes.json`: +7 West Hawaii recipes — lilikoi jelly, guava jam, poha jam, ahi jerky, paakai dried fish, smoked ahi, smoked opakapaka
- New "Smoking & Curing" recipe category (2 smoked fish recipes)
- `ReferenceRepository`: added fishing_guide.json to assetFiles
- `ReferenceViewModel`: added "Hawaii Foraging" and "Hawaii Fishing" to categoryOrder
- `RecipesViewModel`: added "Smoking & Curing" to categoryOrder

### Recipes Screen (complete — 2026-05-21)
- `recipes/data/RecipeItem.kt`: `RecipeItem`, `Ingredient`, `RecipeStep`, `RecipesData` (all `@Serializable`)
- `recipes/data/RecipeRepository.kt`: `@Singleton`, loads `assets/recipes/recipes.json` lazily
- `recipes/RecipesViewModel.kt`: `RecipesUiState` with computed `categories`/`filtered`, `onSearch`, `onCategorySelected`
- `recipes/RecipesScreen.kt`: full UI — search bar, category filter chips, expandable recipe cards
  - Cards show: name + `DifficultyBadge` (Beginner/Intermediate/Advanced color-coded) + expand icon
  - Expanded: summary, time/yield meta, ingredients list, numbered steps with tips, safety notes, source
- `assets/recipes/recipes.json`: 15 food preservation recipes across 4 categories:
  - Water Bath Canning (5): Strawberry Jam, Dill Pickles, Tomato Salsa, Apple Butter, Peach Preserves
  - Pressure Canning (3): Green Beans, Chicken Stock, Beef Vegetable Stew
  - Fermentation (4): Sauerkraut, Kimchi, Lacto-Fermented Pickles, Sourdough Starter
  - Dehydrating (3): Fruit Leather, Beef Jerky, Dried Herb Blend

## Stub Screens (not yet started)
- `CookingScreen` — placeholder only
- `InventoryScreen` — placeholder only

---

## Next Steps
- Download PDFs locally with `scripts/download_reference_pdfs.py` and copy to `app/src/main/assets/reading_room/`
- Smoke test: Reading Room tab, category filter, tap → PDF renders pages, back works offline
- Inventory screen: Room DB schema, InventoryItem model, jar/batch log, expiration alerts
- Cooking mode: step-by-step, screen-on, large text, per-step timers

---

## Process Notes
- **Merge to `main` before starting new work.** Keep branches small, one feature per branch. Don't let work pile up on a feature branch — check that it's merged, don't just assume.
- Google Maven is unreachable from cloud sessions. Any dependency changes need a CI push to verify the build.
- Dial gauge accuracy: cooperative extension office annually. (Relevant for USDA content accuracy notes.)
- PDF assets (reading_room/) are gitignored to keep APK size manageable — download and commit them in a dedicated commit.
