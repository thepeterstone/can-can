# can-can — Coding Standards

## Language & Toolchain

- **Kotlin only.** No Java files, ever.
- **Kotlin DSL** for all Gradle files (`.gradle.kts`). No Groovy.
- **Version catalog** (`gradle/libs.versions.toml`) for all dependency versions. Never hardcode versions in build files.

## UI

- **Jetpack Compose only.** No XML layouts, no `layout/` resource files.
- No ViewBinding, no data binding.
- Material 3 components (`androidx.compose.material3`).
- `@Preview` composables for all non-trivial UI components.

## Architecture

- **MVVM.** `ViewModel` per screen (or per feature if screens share state).
- State exposed as `StateFlow<UiState>` from ViewModel. No `LiveData`.
- UI collects state via `collectAsStateWithLifecycle()`.
- No business logic in composables — composables call ViewModel, ViewModel owns logic.

## Async

- **Kotlin Coroutines + Flow.** No RxJava, no callbacks.
- `viewModelScope` for ViewModel-scoped coroutines.
- `Dispatchers.IO` for DB and file operations; switch to `Main` for UI updates (handled by `StateFlow`).

## Database

- **Room** with KSP for annotation processing. No SQLiteOpenHelper.
- DAOs return `Flow<T>` for reactive queries.
- Suspend functions for one-shot DB writes.

## Dependency Injection

- **Hilt.** `@HiltAndroidApp`, `@HiltViewModel`, `@Inject`.
- No manual DI, no service locator.

## Logging

- All decision-relevant events go to `CanCanLogger` (in-app log). This is the primary debug tool.
- `android.util.Log` is acceptable for crash-adjacent events but not a substitute for `CanCanLogger`.
- Never log user data (jar contents, dates) at INFO or higher.

## What's Forbidden

| Thing | Reason |
|-------|--------|
| `getExternalFilesDir()` / app-private storage for user data | Doesn't survive uninstall |
| `LiveData` | Replaced by `StateFlow` |
| XML layouts | Replaced by Compose |
| ViewBinding | Not used with Compose |
| RxJava | Replaced by Coroutines/Flow |
| Java source files | Kotlin-only project |
| Groovy Gradle scripts | Use Kotlin DSL |
| Hardcoded dependency versions | Use version catalog |
| Compatibility shims for API < 36 | Target API 36 only |

## Naming Conventions

- Composables: `PascalCase`, no `fun` prefix in name
- ViewModels: `<Feature>ViewModel`
- Screens: `<Feature>Screen.kt`
- Room entities: `<Name>Entity` or just `<Name>` if no domain conflict
- DAOs: `<Name>Dao`
- Repositories: `<Name>Repository`
- State classes: `<Feature>UiState`
