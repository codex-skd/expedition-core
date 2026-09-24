package com.skd.expeditioncore;

import com.skd.expeditioncore.boss.BossEncounter;
import com.skd.expeditioncore.config.ExpeditionConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for Expedition Core — the adventure/world library of the Majestic ecosystem.
 *
 * <p>This library ships <em>no concrete content</em>. It exposes reusable frameworks:</p>
 * <ul>
 *   <li><b>dimension</b> — bootstrap helpers for registering custom dimensions, safe cross-dimension
 *       travel, return anchors. Optional SPI hooks for {@code tower_waystone} / {@code teleport_animation}
 *       (never hard dependencies).</li>
 *   <li><b>structure</b> — jigsaw pool/placement utilities, controlled piece spawns.</li>
 *   <li><b>boss</b> — {@code BossEncounter} state machine: phased boss bar, arena lock, loot table,
 *       music, and a GeckoLib render base to cut boilerplate.</li>
 *   <li><b>loot</b> — pool injection into vanilla / structure loot tables, advancement hooks.</li>
 * </ul>
 *
 * <p>Full design: {@code docs/DESIGN_EXPEDITION_CORE_1-21-1.md}. Independent of the magic
 * library ({@code astral_core}) — reusable by any SKD mod. GeckoLib is a real dependency.</p>
 */
@Mod(ExpeditionCore.MOD_ID)
public final class ExpeditionCore {

    public static final String MOD_ID = "expedition_core";

    public static final Logger LOGGER = LoggerFactory.getLogger("Expedition Core");

    public ExpeditionCore(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Expedition Core v{} loading", modContainer.getModInfo().getVersion());

        BossEncounter.TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, ExpeditionConfig.SERVER_SPEC);
    }
}
