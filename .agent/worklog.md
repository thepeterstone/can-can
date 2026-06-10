# can-can — Worklog

## Current State (as of 2026-06-10)

**Branch:** `claude/hawaii-fish-reference-lookup-1m8enb`
**Session focus:** Hawaii fish reference — shore fish lookup screen + library PDF stubs

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

### Library / PDF Viewer (complete — PR #5, 2026-05-27)
- Renamed from "Reading Room" to "Library" throughout: package, classes, route, assets
- `library/` package: `LibraryDocument`, `LibraryRepository` (33 hardcoded docs), `LibraryViewModel`, `PdfViewerViewModel`
- `LibraryScreen`: category filter chips + document card list
- `PdfViewerScreen`: PdfRenderer-based page viewer with TopAppBar, back nav, pinch-to-zoom, fullscreen toggle
- Navigation: `Screen.Library` route `"library"` in bottom nav; `"pdf_viewer/{documentId}"` for PDF viewer
- PDFs: bundled under `app/src/main/assets/library/` — must run `scripts/download_reference_pdfs.py` then copy to assets, or download directly
- Unit tests: `LibraryRepositoryTest`, `LibraryViewModelTest`
- **PDF assets are NOT committed** — ~20–50 MB, must be downloaded locally then committed

### Inventory Screen (complete — PR #5, 2026-05-27)
- Room DB: `InventoryItemEntity` (`@Entity`), `InventoryDao` (Flow-returning), `CanCanDatabase`
- `DatabaseModule`: Hilt `@Module` with `@Provides` for DB and DAO (first `@Module` in project)
- `InventoryRepository`: `@Singleton`, wraps DAO
- `InventoryViewModel`: search, category filter, add/edit/delete, barcode scanning state, `pendingBarcode` flow-through
- `InventoryScreen`: search bar, FilterChip categories, item cards with expiry color-coding (red/amber/muted), FAB → `ModalBottomSheet` add/edit form with date picker, dropdowns, scan button
- `BarcodeScannerScreen`: CameraX + ML Kit full-screen overlay, CAMERA permission handling, fires once on first barcode detected

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

### Shore Fish Lookup (in progress — 2026-06-09, branch: claude/hawaii-fish-reference-lookup-1m8enb)
- `assets/reference/shore_fish_lookup.json`: 15 west Hawaii shoreline species with full field set
- `reference/data/ShoreFish.kt`: `ShoreFishData`, `ShoreFish` (`@Serializable`)
- `reference/data/ShoreFishRepository.kt`: loads JSON from assets, same lazy pattern as `ReferenceRepository`
- `reference/ShoreFishViewModel.kt`: `ShoreFishUiState` with category filter + Wikipedia image loading
- `reference/ShoreFishScreen.kt`: `LazyVerticalGrid` (2-col) photo cards + `ModalBottomSheet` detail with full species info, safety warnings
- `AppNavHost.kt`: added `Screen.ShoreFish` (`"shore_fish"` route) + composable
- `ReferenceScreen.kt`: "Visual Shore Fish Lookup" banner card appears when "Hawaii Fishing" category is active
- `LibraryRepository.kt`: 3 Hawaii Fishing PDF stubs added (fishes_of_hawaii, fishing_in_hawaii, fishing_regs_may_2025) — PDFs not yet downloaded

## Stub Screens (not yet started)
- `CookingScreen` — placeholder only

---

## Next Steps
- Download 3 DLNR PDFs manually and commit to `app/src/main/assets/library/hawaii_fishing/`:
  - `https://dlnr.hawaii.gov/dar/files/2014/04/fishes_of_hawaii.pdf`
  - `https://dlnr.hawaii.gov/dar/files/2016/03/Fishing_in_Hawaii.pdf`
  - `https://dlnr.hawaii.gov/dar/files/2025/05/fishing_regs_May_2025.pdf`
- Merge `claude/hawaii-fish-reference-lookup-1m8enb` to main
- Cooking mode: step-by-step, screen-on, large text, per-step timers
- Consider: recipe-to-inventory integration (reduce stock when cooking)

---

## Process Notes
- **Merge to `main` before starting new work.** Keep branches small, one feature per branch. Don't let work pile up on a feature branch — check that it's merged, don't just assume.
- Google Maven is unreachable from cloud sessions. Any dependency changes need a CI push to verify the build.
- Dial gauge accuracy: cooperative extension office annually. (Relevant for USDA content accuracy notes.)
- PDF assets (reading_room/) are gitignored to keep APK size manageable — download and commit them in a dedicated commit.
