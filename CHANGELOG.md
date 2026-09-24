# Changelog

All notable changes to this project will be documented in this file.

## [0.0.0-beta.2]

### Added
- **Structures** (`structure/`): `JigsawUtils` (build/register `StructureTemplatePool`s from a
  piece/weight map, look up pools by key), `StructurePlacement` (spacing/separation helper,
  biome tag matcher), `PieceSpawn` (register a callback keyed by a piece id, triggered by the
  consumer for controlled mob/loot spawning inside a generated room).
- **Boss encounters** (`boss/`): `BossEncounter`, a data-attachment-backed state machine on any
  `Mob` with a builder API (`phase()`/`arena()`/`bar()`/`loot()`/`music()`/`build()`). Supporting
  classes: `BossEncounterData` (attachment payload), `BossPhase` (health-threshold record),
  `ArenaLock` (AABB boundary + escape callback, barrier placement left to the consumer),
  `BossBarController` (wraps `ServerBossEvent`), `BossLootTable` (duplicate-and-distribute roll),
  `BossMusic` (per-player looped sound while engaged). A GeckoLib render base
  (`boss/render/GeoBossEntity`/`GeoBossRenderer`) to cut consumer boilerplate.
- **Loot** (`loot/`): `LootInjector` (adds a pool to an existing vanilla/structure loot table,
  gated by a load-time `ICondition`), `AdvancementHooks` (grant/check/grant-all from server code).
- **Config** (`config/`): `ExpeditionConfig` — server `ModConfigSpec` with multiplayer boss
  health/damage scaling factors and an arena-escape grace period, plus a
  `scalingMultiplier(playerCount, fraction)` helper the consumer applies to its own entity
  attributes.

### Fixed
- `BossEncounter.tick()` initialized `currentPhase` to `0` and only advanced on `i > currentPhase`
  — with the counter starting at `0`, the highest-threshold phase (index `0`, e.g. entering combat
  at 75% health) could never fire, only later phases could. Fixed with a `-1` sentinel ("no phase
  entered yet") and a loop that always scans from index `0`.
- `LootInjector.addPool()` accepted a `LootItemCondition` parameter that was never evaluated —
  `LootTableLoadEvent` fires before any gameplay `LootContext` exists, so a `LootItemCondition`
  cannot be tested at that point. Changed the parameter type to
  `net.neoforged.neoforge.common.conditions.ICondition` (evaluated with `ICondition.IContext.EMPTY`),
  the correct type for datapack-load-time conditions.

### Notes
- Expedition Core is a library: it ships no concrete content — these are frameworks for a consumer
  mod (starting with `majestic`) to build on. `dimension/` (bootstrap, safe travel, return anchors)
  is not implemented yet; that's Milestone 2.
- Delegated to OpenCode and independently verified by Claude (build + `runGameTestServer` boot,
  twice — before and after adding `config/`). Both fixes above were found by code review, not by
  the boot smoke test (the build passed and the mod booted cleanly either way — these were latent
  logic bugs, not crashes).

## [0.0.0-beta.1]

First versioned build. **Minecraft 1.21.1 / NeoForge 21.1.249** (Java 21).

### Added
- Initial project setup: build (`net.neoforged.moddev` 2.0.142), Parchment `2024.11.17`, GitLab CI
  mirror pipeline, `maven-publish` for downstream consumption.
- Empty `@Mod` entry point (`com.skd.expeditioncore.ExpeditionCore`). No subsystems wired yet.
- **GeckoLib** `4.7.6` wired as a real dependency (`software.bernie.geckolib:geckolib-neoforge-1.21.1`).

### Fixed
- `runs.data` used `clientData()`, which doesn't exist on `net.neoforged.moddev` 2.0.142
  (`Trying to prepare unknown run: clientData`) — replaced with `data()`.

### Notes
- Expedition Core is a library: it ships no concrete content. The dimension, structure, boss and
  loot frameworks are added milestone by milestone (M1 onward) — none are implemented yet, this
  is the bare, verified-buildable skeleton. See `docs/DESIGN_EXPEDITION_CORE_1-21-1.md`.
- Independent of `astral_core` (the magic library) — reusable by any SKD mod.
- `common_toolkit` is not wired yet; will be added if/when a milestone needs it, always as an
  external jar, never bundled.
