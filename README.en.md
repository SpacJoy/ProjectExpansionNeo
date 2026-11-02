[ProjectExpansionNeo](README.en.md) › **English** / [简体中文](README.md)

# ProjectExpansionNeo

<a href="https://github.com/SpacJoy/ProjectExpansionNeo/releases"><img src="https://img.shields.io/github/v/release/SpacJoy/ProjectExpansionNeo?logo=github&label=Latest%20Release" alt="Latest Release"></a>
<a href="https://github.com/SpacJoy/ProjectExpansionNeo/blob/26.3/LICENSE"><img src="https://img.shields.io/github/license/SpacJoy/ProjectExpansionNeo?label=License" alt="License"></a>
<a href="https://github.com/deepseek-ai/deepseek-harness"><img src="https://img.shields.io/badge/powered_by-dsh-4D6BFE?style=flat-square&logo=deepseek&logoColor=white" alt="powered by dsh"></a>

**ProjectExpansionNeo** is an expansion for **ProjectEF Neo** on **Minecraft 26.3 / Fabric**.
Ported from the NeoForge version of Project Expansion, it adds more options for EMC generation, storage, and automation.

> **Required dependency: [ProjectEF](https://github.com/SpacJoy/ProjectEF)**
> This mod is an addon for ProjectEF and will not load without it.
> For this branch use [ProjectEF 1.3.4](https://github.com/SpacJoy/ProjectEF/releases/tag/1.3.4)
> and download `ProjectEF-26.3-PE1.3.4.jar` — not the `-api.jar` or `-sources.jar` asset.

> **This branch targets Minecraft 26.3.**
> It is a port of the 1.21.1 line by [SpacJoy](https://github.com/SpacJoy/ProjectExpansionNeo),
> forked from [wchiway/ProjectExpansionNeo](https://github.com/wchiway/ProjectExpansionNeo).
> The differences from the 1.21.1 branch are listed under [Changes in this branch](#changes-in-this-branch).

## Changes in this branch

### 1. Ported to Minecraft 26.3

The same 26.3 mechanics apply as in ProjectEF 26.3:

| Item | 1.21.1 | 26.3 |
| --- | --- | --- |
| Item model entry | `assets/<ns>/models/item/*.json` | `assets/<ns>/items/*.json` |
| Slot icon | any atlas | must live under `textures/gui/sprites/` |
| GUI layering | draw-call order | `GuiRenderState` bounding-box tree; use `nextStratum()` to force a layer on top |
| Block item translation key | `block.<ns>.<path>` | `item.<ns>.<path>`; call `useBlockDescriptionPrefix()` to switch back |
| Access widener | `*.accesswidener` | `*.classtweaker` |
| Build | Loom 1.10.x / JDK 21 | Loom 1.18.2 / JDK 25 |

Recipe JSON now writes item references as plain strings instead of `{"item": "..."}` objects.

### 2. Behaviour fixes unique to this branch

- **Arcane Transmutation Tablet count labels** are drawn in a separate stratum after the item icons, so the icons no longer cover them.
- **EMC unit names** follow the game language (Chinese uses 兆 / 京 / 垓 and so on instead of Quintillion-style English suffixes). The top-left HUD uses the same formatter.
- **EMC per second readout** was showing twice the real gain; the two 20-tick samples are now averaged instead of summed.
- **Stack limits** go through `getMaxStackSize()` instead of reading `MAX_STACK_SIZE` directly, so stacking mods such as Stackable are respected.
- **Curios data removed.** The upstream 1.21.1 tree still carried `data/curios/` files even though the mod targets Trinkets; those dead files are gone and the necklace/ring tags list item IDs directly.

### 3. Not ported

- **EMI** has no 26.3 build. The integration source stays in the tree but is excluded from compilation.

## Features

- **EMC generation:** Higher-tier energy collectors, relays, and power flowers.
- **EMC storage and automation:** Large EMC stars, EMC links, transmutation interfaces, and the Energy Condenser MK3.
- **Storage and utilities:** Advanced alchemical chests, alchemical books, the Arcane Transmutation Tablet, and infinite fuel.

## Installation

For this branch, use Minecraft 26.3, Java 25, and Fabric Loader 0.19.5 or later.
Place the following in your instance's `mods/` directory:

- Fabric API 0.165.0+26.3.
- **[ProjectEF](https://github.com/SpacJoy/ProjectEF) 1.3.4** — the full Minecraft 26.3 JAR, from the
  [ProjectEF 1.3.4 release page](https://github.com/SpacJoy/ProjectEF/releases/tag/1.3.4).
  This is a hard dependency; ProjectExpansionNeo does not load without it.
- `ProjectExpansionNeo-26.3-1.2.0.jar`.

Choose the regular ProjectEF JAR, such as `ProjectEF-26.3-PE1.3.4.jar`, **not** the `-api.jar` or `-sources.jar` asset.
ProjectEF Neo bundles Forge Config API Port and the permissions library; you do not need to install them separately.

The 1.21.1 branch instead needs Minecraft 1.21.1, Java 21, and Fabric Loader 0.16.9 or later, plus the
[ProjectEF 1.21.1 JAR](https://github.com/wchiway/ProjectEF/releases).

## Optional integrations

Supported integrations include JEI, EMI, Jade, WTHIT, and Trinkets.
Trinkets provides a dedicated transmutation tablet slot in place of the former Curios integration.
TOP integration is not included in this Fabric version.

## Configuration and compatibility

Server and client configuration files continue to use TOML. The mod ID remains `projectexpansion`.
Migrating existing NeoForge worlds across loaders has not been verified.

See the [development guide (Chinese)](DEV.md) for building, development setup, resource generation, and testing.

## Project origins

- [Upstream Project Expansion](https://github.com/DonovanDMC/ProjectExpansion) — the original NeoForge mod
- [wchiway/ProjectEF](https://github.com/wchiway/ProjectEF) — upstream ProjectEF (1.21.1 line)
- [SpacJoy/ProjectEF](https://github.com/SpacJoy/ProjectEF) — the 26.3 ProjectEF this branch depends on
- [This branch (Minecraft 26.3)](https://github.com/SpacJoy/ProjectExpansionNeo)

This project retains upstream code, assets, and author attribution and is licensed under the [MIT License](LICENSE).
It is an unofficial expansion and is not supported by the original ProjectE project.
