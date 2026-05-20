# can-can — User Preferences & Workflow Quirks

## Developer Context

- **Sole developer, sole user.** There is no team, no external users. Design for one person.
- Error messages and UI copy can be terse and direct — no need to explain things to strangers.

## Workflow

- Always research and present a strategy before implementing. Wait for explicit GO.
- Flag temporary hacks with `// HACK:` or `// TODO(cleanup):` comments. Don't leave them undocumented.
- One-off solutions are fine when justified — don't over-engineer for hypothetical reuse.
- **Keep branches small and short-lived.** Before starting new work, offer to merge the current branch to `main` first. One feature per branch.

## Debugging

- Not always at a computer with ADB. **In-app debug log is the primary debugging tool.**
- When adding features, consider whether relevant state/decisions are visible in `CanCanLogger`.
- ADB logcat is a nice-to-have, not a requirement.

## Data / Storage

- All user data (inventory, custom recipes, preferences) must survive app uninstall/reinstall.
- Use shared storage (MediaStore, Documents/) or exported DB for anything the user cares about.
- Assume the user will reinstall, switch devices, or restore from backup.

## UI Preferences

- Cooking mode: screen must stay on, text must be large and readable from counter distance.
- Offline-first: no feature should require network after initial install.
- Material 3 teal theme (matches logo).

## Things That Annoy Me

- Over-abstracted code for simple tasks.
- Unnecessary error handling for impossible states.
- Comments that describe what the code does (the code should do that).
- Gold-plating features I haven't asked for.
