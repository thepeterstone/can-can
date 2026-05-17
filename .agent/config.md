# can-can — Agent Config (Master Rulebook)

## Working Style

**Workflow:** Research first, present a strategy, wait for explicit GO before making any system-changing action. Never implement based on assumptions.

**Sole developer and sole user.** Optimise for my experience, not for a hypothetical general public. Error messages are for me, not strangers.

**Pragmatism:** One-off solutions and temporary hacks are acceptable — flag them clearly in code and ensure they get cleaned up. Don't gold-plate things I haven't asked for.

**Debug logging:** I am not always at a computer with ADB. The primary debugging tool is the in-app debug log (`CanCanLogger` or equivalent). Log anything decision-relevant there, not just to Android's Log. ADB is a bonus, not a dependency.

**Persistence:** All user data must survive app uninstall/reinstall. Never use `getExternalFilesDir()` or any app-private storage for anything the user cares about. Use shared storage (MediaStore / Documents/) with appropriate ownership handling.

---

## Git Conventions — NON-NEGOTIABLE

- Default and only development branch is `main`. Never `master`, never anything else unless I explicitly create a feature branch.
- Claude Code session branches (`claude/…`) are acceptable but must be merged to `main` promptly.
- CI/CD fires on `main` pushes only. Never change the trigger branch.
- Before any commit or push: confirm `git branch --show-current` is the correct branch.
- A pre-commit hook blocking commits to `master` lives in `scripts/git-hooks/`.
  Activate with: `git config core.hooksPath scripts/git-hooks`

---

## Android Target

- **Build target: Android 16 (API 36).** Do not add compatibility shims, workarounds, or intent-filter entries targeting older versions unless I explicitly ask. When something doesn't work, diagnose for API 36 first.
- **Architecture: MVVM + Jetpack Compose + Kotlin Coroutines.**
- **No ViewBinding.** Compose replaces it entirely. No XML layouts.
- **Package / application ID: `org.terst.cancan`**
- All storage decisions must account for scoped storage on API 29+.

---

## Session Start Checklist (run at the start of every session)

1. Run `git branch --show-current` — verify you're on the right branch.
2. Run `git config core.hooksPath scripts/git-hooks` if not already set.
3. Read `.agent/config.md` (this file) and `.agent/worklog.md`.
4. Update `.agent/worklog.md` with today's session focus before doing any work.

---

## Living Documentation Mandate

After any session where a new constraint, preference, architectural decision, or user quirk is revealed, update the relevant `.agent/` file immediately. Do not let documentation drift from reality.

---

## .agent/ File Structure

| File | Purpose |
|------|---------|
| `config.md` | Master rulebook (this file) |
| `worklog.md` | Session state: current focus, recently completed, next steps |
| `design.md` | Architecture, component design, UI decisions |
| `coding_standards.md` | Tech stack specifics, patterns, what's forbidden |
| `preferences.md` | User preferences and workflow quirks (living document) |
| `mission.md` | What the app is, what it's not, strategic constraints |
