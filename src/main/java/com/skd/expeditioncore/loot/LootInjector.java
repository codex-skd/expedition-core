package com.skd.expeditioncore.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.LootTableLoadEvent;

/**
 * Utility for injecting additional loot pools into existing vanilla or structure loot tables.
 *
 * <p>Register injections via {@link #addPool}, then subscribe this class (or call
 * {@link #onLootTableLoad} from your own handler) on the NeoForge event bus:
 *
 * <pre>{@code
 * NeoForge.EVENT_BUS.register(LootInjector.class);
 * }</pre>
 *
 * <p>Example:
 * <pre>{@code
 * LootInjector.addPool(
 *     ResourceLocation.fromNamespaceAndPath("minecraft", "chests/simple_dungeon"),
 *     LootPool.lootPool()
 *         .setRolls(ConstantValue.exactly(1))
 *         .add(LootItem.lootTableReference(
 *             ResourceLocation.fromNamespaceAndPath("my_mod", "special_loot")).build())
 *         .build(),
 *     TrueCondition.INSTANCE
 * );
 * }</pre>
 */
public final class LootInjector {

    private static final List<Injection> INJECTIONS = new ArrayList<>();

    private LootInjector() {}

    /**
     * Adds a loot pool injection that will be applied to the specified loot table
     * when it is loaded.
     *
     * @param targetTableId the resource location of the loot table to inject into
     * @param pool          the loot pool to add
     * @param condition     the {@link ICondition} that must be met for the pool to be included; this is
     *                      evaluated with {@link ICondition.IContext#EMPTY} at load time (loot tables are
     *                      loaded before any gameplay context exists, so this is a datapack-load-time
     *                      condition like "mod X is loaded", not a gameplay {@code LootItemCondition})
     */
    public static void addPool(ResourceLocation targetTableId, LootPool pool, ICondition condition) {
        Objects.requireNonNull(targetTableId, "targetTableId must not be null");
        Objects.requireNonNull(pool, "pool must not be null");
        Objects.requireNonNull(condition, "condition must not be null");
        INJECTIONS.add(new Injection(targetTableId, pool, condition));
    }

    /**
     * Event handler for {@link LootTableLoadEvent}. Register this on the NeoForge event bus.
     *
     * @param event the loot table load event
     */
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableName = event.getName();
        LootTable table = event.getTable();

        for (Injection injection : INJECTIONS) {
            if (tableName.equals(injection.targetTableId) && injection.condition.test(ICondition.IContext.EMPTY)) {
                table.addPool(injection.pool);
            }
        }
    }

    private record Injection(ResourceLocation targetTableId, LootPool pool, ICondition condition) {}
}
