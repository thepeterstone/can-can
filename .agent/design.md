# can-can — Architecture & Design

## App Structure

**Single Activity** (`MainActivity`) with a `NavHost` managing all screens. Bottom navigation bar with 4 top-level destinations.

### Navigation Destinations

| Route | Feature | Description |
|-------|---------|-------------|
| `recipes` | Recipes | Browse, search, and filter preservation recipes |
| `cooking/{recipeId}` | Cooking Mode | Step-by-step guided cooking with per-step timers |
| `inventory` | Inventory | Jar/batch tracking with shelf-life expiration alerts |
| `reference` | Reference | Offline USDA, LDS, and Ball guide data |

---

## Package Structure (package-by-feature)

```
org.terst.cancan/
  MainActivity.kt
  CanCanApp.kt              ← Hilt Application subclass
  navigation/
    AppNavHost.kt           ← NavHost + bottom nav setup
    Screen.kt               ← sealed class of routes
  ui/theme/
    Theme.kt
    Color.kt
    Type.kt
  recipes/
    RecipesScreen.kt
    RecipesViewModel.kt
    RecipeDetailScreen.kt
    RecipeDetailViewModel.kt
    data/
      Recipe.kt             ← domain model
      RecipeRepository.kt
  cooking/
    CookingScreen.kt        ← step-by-step, screen-on, large text, timers
    CookingViewModel.kt
  inventory/
    InventoryScreen.kt
    InventoryViewModel.kt
    BarcodeScanner.kt       ← CameraX + ML Kit wrapper
    data/
      InventoryItem.kt      ← domain model
      InventoryDao.kt
      InventoryRepository.kt
  reference/
    ReferenceScreen.kt
    ReferenceViewModel.kt
    data/
      ReferenceRepository.kt  ← reads from bundled assets
  core/
    CanCanLogger.kt         ← in-app debug log (primary debugging tool)
    database/
      CanCanDatabase.kt     ← Room database
```

---

## Data Layer

### Inventory (Room + Flow)
- `InventoryItem`: jar count, contents, date preserved, best-by date, storage location
- Expiration alerts computed from `bestByDate` vs `LocalDate.now()`
- Reactive queries via `Flow<List<InventoryItem>>`

### Reference Data (Bundled Assets)
- USDA, LDS, and Ball guide data stored as JSON in `assets/reference/`
- Loaded at startup by `ReferenceRepository`, held in memory or Room table
- Read-only; updated only via app releases

### Recipes (Room or Bundled)
- Initial recipes bundled as JSON assets
- Room table for user-created/modified recipes (future)

### Preferences (DataStore)
- `DataStore<Preferences>` for user settings (units, theme, etc.)

---

## UI / UX Principles

- **Cooking mode**: screen stays on (`WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON`), extra-large text, prominent timer display
- **Material 3** theming with teal primary color (matching logo background)
- No network required after install — fully offline
- Bottom navigation always visible (except cooking mode, which is full-screen)

---

## Dependency Injection

Hilt with `@HiltAndroidApp` on `CanCanApp`. ViewModels injected via `@HiltViewModel`. Repositories injected into ViewModels. DAOs provided from `CanCanDatabase` module.

---

## Logo / Branding

- Adaptive icon: foreground = dancing canning jars (transparent PNG), background = solid teal
- Color reference: `#008577` (Material teal 700) or closest match to provided asset
- App name display: "can-can"
