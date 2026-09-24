package com.skd.expeditioncore.boss;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Thin wrapper resolving a loot table {@link ResourceKey} and rolling it against a
 * {@link LootParams} built from the boss's death context.
 *
 * <p>Uses <b>duplicate-and-distribute</b> strategy: each participating player receives
 * the full loot roll. This is the simpler and more common approach for boss loot,
 * avoiding the complexity of split distribution while ensuring all contributors are
 * rewarded.
 */
public class BossLootTable {

    private final ResourceKey<LootTable> lootTableKey;

    /**
     * Creates a boss loot table reference.
     *
     * @param lootTableKey the resource key of the loot table to roll
     */
    public BossLootTable(ResourceKey<LootTable> lootTableKey) {
        this.lootTableKey = Objects.requireNonNull(lootTableKey, "lootTableKey must not be null");
    }

    /**
     * Returns the loot table resource key.
     */
    public ResourceKey<LootTable> lootTableKey() {
        return this.lootTableKey;
    }

    /**
     * Rolls the loot table and distributes items to all participating players.
     *
     * <p>Each player receives the full loot table roll (duplicate-and-distribute).
     *
     * @param level    the server level
     * @param boss     the boss entity (used as THIS_ENTITY in the loot context)
     * @param players  the list of participating players
     */
    public void distributeLoot(ServerLevel level, Entity boss, List<ServerPlayer> players) {
        if (players.isEmpty()) {
            return;
        }

        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(this.lootTableKey);
        if (lootTable == LootTable.EMPTY) {
            return;
        }

        LootParams.Builder paramsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, boss)
                .withParameter(LootContextParams.ORIGIN, boss.position());
        LootParams params = paramsBuilder.create(LootContextParamSets.ENTITY);

        for (ServerPlayer player : players) {
            MutableInt totalDrops = new MutableInt(0);
            lootTable.getRandomItems(params, (stack) -> {
                if (!stack.isEmpty()) {
                    player.getInventory().add(stack);
                    totalDrops.add(stack.getCount());
                }
            });
        }
    }

    /**
     * Rolls the loot table and returns the items without distributing them.
     *
     * @param level the server level
     * @param boss  the boss entity
     * @return the list of item stacks from the roll
     */
    public List<ItemStack> rollLoot(ServerLevel level, Entity boss) {
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(this.lootTableKey);
        if (lootTable == LootTable.EMPTY) {
            return List.of();
        }

        LootParams.Builder paramsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, boss)
                .withParameter(LootContextParams.ORIGIN, boss.position());
        LootParams params = paramsBuilder.create(LootContextParamSets.ENTITY);

        return lootTable.getRandomItems(params);
    }
}
