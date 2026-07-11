# Unbounded Bundle

![Fabric](https://img.shields.io/badge/Loader-Fabric-blue)
![NeoForge](https://img.shields.io/badge/Loader-NeoForge-orange)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow)
![Side: Client](https://img.shields.io/badge/Side-Client-red)

Removes the 12-item display limit on bundles and replaces the fixed tooltip with a scrollable, dynamically-sized grid.
Store as many different items as you want and navigate them without leaving your inventory.

---

## Features

- No more 12-item display limit — all bundle contents are always accessible
- Dynamic column count that adapts to the number of stored items (configurable, 4–8 columns)
- Scrollable tooltip with a configurable max height so it never overflows your screen
- Scroll automatically follows the selected item to keep it always in view
- Full [ModMenu](https://modrinth.com/mod/modmenu) + [Cloth Config](https://modrinth.com/mod/cloth-config) support for
  in-game configuration (both optional)
- Fabric, Quilt and NeoForge support

---

## Installation

1. Download the jar for your loader from [Modrinth](https://modrinth.com/project/unbounded-bundle)
   or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/unbounded-bundle)
2. Drop it into your `mods/` folder

**Optional but recommended:**

- [ModMenu](https://modrinth.com/mod/modmenu) — adds a mod list screen where you can access the config screen (fabric)
- [Cloth Config](https://modrinth.com/mod/cloth-config) — required for the in-game config screen

Without these, the mod works out of the box with its default settings. You can still configure it by editing the config
file manually (see below).

---

## Configuration

The config file is located at `.minecraft/config/unbounded_bundle.json` and is created automatically on first launch.

| Option                  | Type       | Default | Description                                                |
|-------------------------|------------|---------|------------------------------------------------------------|
| `enabled`               | boolean    | `true`  | Enables the whole mod.                                     |
| `minColumns`            | int (4–8)  | `4`     | Minimum number of columns in the tooltip grid.             |
| `maxColumns`            | int (4–8)  | `6`     | Maximum number of columns in the tooltip grid.             |
| `scrollable`            | boolean    | `true`  | Enables the scrollable tooltip.                            |
| `smoothScrolling`       | boolean    | `false` | Enables smooth scrolling.                                  |
| `maxRows`               | int (3–11) | `3`     | Maximum number of visible rows when scrollable is enabled. |
| `usePercentageProgress` | boolean    | `false` | Show the progress as a percentage.                         |                                    

The column count adapts automatically to the number of items in the bundle, clamped between `minColumns` and
`maxColumns`. `maxRows` has no effect when `scrollable` is set to `false`.

---

## License

MIT — see [LICENSE](LICENSE) for details.