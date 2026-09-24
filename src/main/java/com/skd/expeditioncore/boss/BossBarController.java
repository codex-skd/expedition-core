package com.skd.expeditioncore.boss;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

/**
 * Wraps a server-side {@link ServerBossEvent} to display and update the boss health bar
 * for players participating in the encounter.
 *
 * <p>Provides methods to create, update, restyle, and remove the bar for a given player set.
 */
public class BossBarController {

    private ServerBossEvent bossEvent;

    /**
     * Creates a new boss bar controller with the given name, color, and overlay.
     *
     * @param name    the display name of the boss bar
     * @param color   the bar color
     * @param overlay the bar overlay style
     */
    public BossBarController(Component name, BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        this.bossEvent = new ServerBossEvent(name, color, overlay);
    }

    /**
     * Updates the boss bar progress (0.0 to 1.0).
     *
     * @param progress the health fraction to display
     */
    public void setProgress(float progress) {
        if (this.bossEvent != null) {
            this.bossEvent.setProgress(progress);
        }
    }

    /**
     * Restyles the boss bar with a new color and overlay.
     *
     * @param color   the new bar color
     * @param overlay the new bar overlay style
     */
    public void restyle(BossEvent.BossBarColor color, BossEvent.BossBarOverlay overlay) {
        if (this.bossEvent != null) {
            this.bossEvent.setColor(color);
            this.bossEvent.setOverlay(overlay);
        }
    }

    /**
     * Updates the boss bar display name.
     *
     * @param name the new display name
     */
    public void setName(Component name) {
        if (this.bossEvent != null) {
            this.bossEvent.setName(name);
        }
    }

    /**
     * Adds a player to the boss bar.
     *
     * @param player the player to add
     */
    public void addPlayer(ServerPlayer player) {
        if (this.bossEvent != null) {
            this.bossEvent.addPlayer(player);
        }
    }

    /**
     * Removes a player from the boss bar.
     *
     * @param player the player to remove
     */
    public void removePlayer(ServerPlayer player) {
        if (this.bossEvent != null) {
            this.bossEvent.removePlayer(player);
        }
    }

    /**
     * Removes all players from the boss bar.
     */
    public void removeAllPlayers() {
        if (this.bossEvent != null) {
            this.bossEvent.removeAllPlayers();
        }
    }

    /**
     * Sets the boss bar visibility.
     *
     * @param visible whether the bar should be visible
     */
    public void setVisible(boolean visible) {
        if (this.bossEvent != null) {
            this.bossEvent.setVisible(visible);
        }
    }

    /**
     * Returns the underlying {@link ServerBossEvent}.
     *
     * @return the boss event, or null if not created
     */
    public ServerBossEvent getBossEvent() {
        return this.bossEvent;
    }

    /**
     * Removes all players and marks the bar as invisible, effectively disposing of it.
     */
    public void dispose() {
        if (this.bossEvent != null) {
            this.bossEvent.removeAllPlayers();
            this.bossEvent.setVisible(false);
            this.bossEvent = null;
        }
    }
}
