package com.skd.expeditioncore.boss;

import java.util.Objects;
import net.minecraft.world.entity.Mob;

/**
 * Simple immutable record representing a boss phase transition.
 *
 * <p>Each phase has a health threshold (fraction of max health, 0.0–1.0) and a callback
 * that is invoked when the boss enters this phase.
 *
 * @param healthThreshold the health fraction (0.0 = defeated, 1.0 = full health) at which this phase begins
 * @param onEnter         callback invoked when the boss enters this phase
 */
public record BossPhase(float healthThreshold, java.util.function.Consumer<Mob> onEnter) {

    public BossPhase {
        Objects.requireNonNull(onEnter, "onEnter callback must not be null");
        if (healthThreshold < 0f || healthThreshold > 1f) {
            throw new IllegalArgumentException("healthThreshold must be between 0.0 and 1.0, got " + healthThreshold);
        }
    }

    /**
     * Checks whether the boss should transition to this phase based on its current health fraction.
     *
     * @param healthFraction the boss's current health as a fraction of max health (0.0–1.0)
     * @return true if the boss's health is at or below this phase's threshold
     */
    public boolean shouldActivate(float healthFraction) {
        return healthFraction <= healthThreshold;
    }
}
