package com.skd.expeditioncore.loot;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;

/**
 * Utility hooks for granting and querying advancements from server code.
 *
 * <p>All methods require a {@link ServerPlayer} and operate on the server side.
 */
public final class AdvancementHooks {

    private AdvancementHooks() {}

    /**
     * Grants an advancement criterion to a player.
     *
     * <p>If the advancement has a single criterion named "impossible", this grants it directly.
     * Otherwise, it grants the first criterion of the advancement.
     *
     * @param player        the server player to grant the advancement to
     * @param advancementId the resource location of the advancement
     * @return true if the criterion was successfully granted (advancement may not be complete)
     */
    public static boolean grant(ServerPlayer player, ResourceLocation advancementId) {
        ServerAdvancementManager manager = player.server.getAdvancements();
        AdvancementHolder holder = manager.get(advancementId);
        if (holder == null) {
            return false;
        }
        return player.getAdvancements().award(holder, "trigger");
    }

    /**
     * Checks whether a player has completed an advancement.
     *
     * @param player        the server player to check
     * @param advancementId the resource location of the advancement
     * @return true if the player has completed all criteria of the advancement
     */
    public static boolean has(ServerPlayer player, ResourceLocation advancementId) {
        ServerAdvancementManager manager = player.server.getAdvancements();
        AdvancementHolder holder = manager.get(advancementId);
        if (holder == null) {
            return false;
        }
        return player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    /**
     * Grants all criteria of an advancement to a player, completing it.
     *
     * @param player        the server player to grant the advancement to
     * @param advancementId the resource location of the advancement
     * @return true if the advancement existed and was granted
     */
    public static boolean grantAll(ServerPlayer player, ResourceLocation advancementId) {
        ServerAdvancementManager manager = player.server.getAdvancements();
        AdvancementHolder holder = manager.get(advancementId);
        if (holder == null) {
            return false;
        }
        var progress = player.getAdvancements().getOrStartProgress(holder);
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(holder, criterion);
        }
        return true;
    }
}
