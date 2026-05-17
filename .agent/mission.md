# can-can — Mission

## What It Is

A personal Android app for food preservation. The target user is the developer — one person who preserves food at home and wants a reliable, offline-first reference and tracking tool.

### Core Features

1. **Recipes** — Browse, search, and filter food preservation recipes (canning, fermentation, dehydrating, freezing, etc.)
2. **Cooking Mode** — Guided step-by-step execution of a recipe with per-step timers, screen-on, large readable text
3. **Inventory** — Track batches of preserved food: what was made, when, how many jars, and when it expires. Barcode scanning for store-bought goods.
4. **Reference** — Offline authoritative data from:
   - USDA Complete Guide to Home Canning
   - LDS (Church of Jesus Christ) food storage guidelines
   - Ball Blue Book / Ball Complete Book of Home Preserving
   - Safe processing times, acidity requirements, storage durations

## What It Is Not

- Not a social app (no sharing, no accounts, no cloud sync)
- Not a general-purpose recipe manager
- Not designed for multiple users or households (initially)
- Not dependent on any network connection after install

## Strategic Constraints

- **Fully offline.** All reference data bundled with the app. No API calls for core features.
- **Data longevity.** User's inventory and custom data must survive app reinstall. Shared storage, not app-private.
- **Single device.** No sync infrastructure needed now. Export/import is a future nice-to-have.
- **Data sourcing.** Start with a curated initial dataset (manually structured USDA/LDS/Ball data). Expand incrementally in future releases.

## Success Criteria

The app is successful when:
- I can look up a canning processing time without internet
- I can walk through a recipe step-by-step while my hands are covered in tomatoes
- I can see at a glance what's in my pantry and what needs to be used soon
