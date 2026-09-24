# Expedition Core

The **adventure / world library** of the [Majestic](https://gitlab.com/stalking-dragons/minecraft/majestic)
mod ecosystem for Minecraft NeoForge. Expedition Core provides the frameworks; it ships **no
concrete content** of its own. It is independent of the magic library and reusable by any mod.

## What it provides

- **Structures** (`structure/`) — `JigsawUtils` (build/register `StructureTemplatePool`s, look up
  pools by key), `StructurePlacement` (spacing/separation helper), `PieceSpawn` (register a
  callback that a consumer triggers when a specific jigsaw piece is placed, for controlled
  mob/loot spawning).
- **Boss encounters** (`boss/`) — `BossEncounter`: a data-attachment-backed state machine on any
  `Mob` with a builder API (phases by health threshold, arena lock, boss bar, loot, music).
  `ArenaLock` (temporary boundary + escape callback, barrier placement left to the consumer),
  `BossBarController` (wraps `ServerBossEvent`), `BossLootTable` (duplicate-and-distribute roll),
  `BossMusic` (per-player looped sound while engaged), and a GeckoLib render base
  (`GeoBossEntity`/`GeoBossRenderer`) to cut boilerplate.
- **Loot** (`loot/`) — `LootInjector` (adds a pool to an existing vanilla/structure loot table,
  gated by a load-time `ICondition`), `AdvancementHooks` (grant/check/grant-all from server code).

## Planned (not implemented yet)

- **Dimensions** (`dimension/`) — bootstrap helpers for registering custom dimensions, safe
  cross-dimension travel, return anchors, and optional SPI hooks for waystone / teleport-animation
  mods (never hard dependencies). Milestone 2.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.249
- Java 21

## Dependencies

- **GeckoLib** `4.7.6` (`software.bernie.geckolib:geckolib-neoforge-1.21.1`) — real dependency for
  boss / complex-mob rendering.
- [Common Toolkit](https://gitlab.com/stalking-dragons/minecraft/common-toolkit) (MIT) — wired in a later milestone.

Dependencies are **external**: installed as separate jars, never bundled.

## Building from Source

```bash
./gradlew build
```

The built JAR will be in `build/libs/`.

## License

MIT — see [LICENSE](LICENSE).
