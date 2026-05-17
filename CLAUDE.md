# can-can — Claude Agent Instructions

## Session Start (do this every time)

1. `git branch --show-current` — verify you're on the right branch (`main` or an approved `claude/…` branch)
2. `git config core.hooksPath scripts/git-hooks`
3. Read `.agent/config.md` and `.agent/worklog.md`
4. Update `.agent/worklog.md` with today's session focus **before doing any work**

## Agent Memory Files

All persistent context lives in `.agent/`:

| File | What's in it |
|------|-------------|
| `.agent/config.md` | Master rulebook — non-negotiables, git conventions, Android target |
| `.agent/worklog.md` | Session state — current focus, completed work, next steps |
| `.agent/design.md` | Architecture, navigation, data layer, package structure |
| `.agent/coding_standards.md` | Tech stack, what's forbidden, naming conventions |
| `.agent/preferences.md` | How this developer works, what annoys them |
| `.agent/mission.md` | What the app is, what it isn't, success criteria |

**Read all of them before making significant decisions. Update them after significant decisions.**

## Non-Negotiables

- **Android API 36 (Android 16) target.** No compat shims for older versions.
- **Jetpack Compose only.** No XML layouts, no ViewBinding.
- **MVVM + Kotlin Coroutines.** `StateFlow` for UI state, no `LiveData`.
- **`main` branch** for development. Never `master`.
- **Package: `org.terst.cancan`**
- **User data must survive uninstall** — shared storage only, never `getExternalFilesDir()`.
- **In-app `CanCanLogger`** is the primary debug tool, not just ADB logcat.

## Workflow Rule

Research first → present strategy → wait for explicit GO → implement. Never make system-changing actions based on assumptions.
