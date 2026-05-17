# can-can

A personal Android app for food preservation — recipes, guided cooking, inventory tracking, and authoritative offline reference data.

## Features

- **Recipes** — Browse and search preservation recipes (canning, fermentation, dehydrating, freezing)
- **Cooking Mode** — Step-by-step recipe walkthrough with per-step timers, screen-on, large text
- **Inventory** — Track jars and batches with shelf-life expiration alerts; barcode scanning for store-bought goods
- **Reference** — Offline USDA, LDS, and Ball guide data: safe processing times, acidity requirements, storage durations

## Requirements

- Android 16 (API 36) or higher
- Android Studio Ladybug or newer
- JDK 17+

## Building

```bash
# Clone the repo
git clone https://github.com/thepeterstone/can-can.git
cd can-can

# Set up git hooks
git config core.hooksPath scripts/git-hooks

# Build debug APK
./gradlew assembleDebug

# Install on connected device / running emulator
./gradlew installDebug
```

## Running

Requires an Android 16 (API 36) device or emulator. The app is fully offline — no network access required after install.

## Data Sources

Reference data is compiled from:
- USDA Complete Guide to Home Canning
- LDS (Church of Jesus Christ of Latter-day Saints) food storage guidelines
- Ball Blue Book / Ball Complete Book of Home Preserving

## License

GNU General Public License v3. See [LICENSE](LICENSE).
