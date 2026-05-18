# can-can — Worklog

## Current Session (2026-05-18)

**Focus:** Build pipeline — GitHub Actions CI, session-start hook

**Completed this session:**
- [x] Launcher icon: extracted foreground (transparent PNG) from logo reference sheet, generated all 5 density variants (mdpi–xxxhdpi)
- [x] `.github/workflows/build.yml`: CI builds debug + unsigned release APKs on push to main/claude/* branches; artifacts available for 14 days
- [x] `.claude/hooks/session-start.sh` + `.claude/settings.json`: async SessionStart hook that sets git hooks path and validates Gradle wrapper
- [x] Fixed `AppNavHost.kt`: exhaustive `when()` — no dead `else` branch
- [x] Build note: Google Maven (AGP / AndroidX) is not reachable from cloud sessions; APK compilation goes through GitHub Actions

**Build is GREEN.** Download APK from Actions tab → can-can-debug artifact.

Root cause of initial CI failure: missing `gradle.properties` — `android.useAndroidX=true` is required for any AndroidX project and was absent from the scaffold.

**Next steps:**
- Wire up Room database schema for inventory
- Begin recipe data model
- Source and structure initial USDA/LDS/Ball guide reference data
- Implement RecipesScreen (list + detail)
- Implement InventoryScreen (batch log + expiration alerts)

---

## Log

### 2026-05-18
- Set up GitHub Actions build workflow (.github/workflows/build.yml)
- Confirmed Google Maven is blocked in cloud sessions — builds run via CI only
- Session-start hook added (.claude/hooks/session-start.sh)
- Launcher icon foreground extracted and placed at all 5 mipmap densities

### 2026-05-17
- Project created from scratch; nav conventions imported and adapted for Compose
- Key decisions recorded: API 36 target, Compose-only UI, MVVM, Hilt, Room + Flow, barcode scanning via CameraX + ML Kit
- Package set to `org.terst.cancan`
- Logo provided as adaptive icon components (dancing canning jars, teal background)
