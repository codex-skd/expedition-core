package com.skd.expeditioncore.boss;

import java.util.Objects;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Represents a temporary arena boundary for a boss encounter.
 *
 * <p>The arena is defined by an {@link AABB} (axis-aligned bounding box).
 * When the encounter starts, barrier blocks (or a barrier-particle/collision stub)
 * can be applied to prevent players from leaving, and removed when the encounter ends.
 *
 * <p><b>Barrier implementation note:</b> For this generic library, we provide a stub
 * implementation that only checks containment and invokes escape callbacks.
 * Concrete barrier block placement (e.g., invisible barrier blocks or custom collision
 * entities) is left to the consumer mod ({@code majestic}), as the correct approach
 * depends on the specific arena design. The consumer mod should listen to the
 * encounter start/end events and apply its own barrier logic using
 * {@link #applyBarriers()} and {@link #removeBarriers()}.
 */
public class ArenaLock {

    private final AABB bounds;
    private final double escapeMargin;
    private final Runnable onPlayerEscape;

    /**
     * Creates an arena lock from a bounding box.
     *
     * @param bounds          the axis-aligned bounding box defining the arena
     * @param escapeMargin    additional margin (in blocks) outside the arena before an escape is triggered
     * @param onPlayerEscape  callback invoked when a player escapes the arena
     */
    public ArenaLock(AABB bounds, double escapeMargin, Runnable onPlayerEscape) {
        this.bounds = Objects.requireNonNull(bounds, "bounds must not be null");
        this.escapeMargin = escapeMargin;
        this.onPlayerEscape = onPlayerEscape;
    }

    /**
     * Creates an arena lock defined by a center position and half-extents.
     *
     * @param center         the center of the arena
     * @param halfExtents    the half-extents (distance from center to each edge)
     * @param escapeMargin   additional margin before escape is triggered
     * @param onPlayerEscape callback invoked when a player escapes the arena
     */
    public ArenaLock(Vec3 center, Vec3 halfExtents, double escapeMargin, Runnable onPlayerEscape) {
        this(
                new AABB(
                        center.x - halfExtents.x, center.y - halfExtents.y, center.z - halfExtents.z,
                        center.x + halfExtents.x, center.y + halfExtents.y, center.z + halfExtents.z
                ),
                escapeMargin,
                onPlayerEscape
        );
    }

    /**
     * Creates an arena lock defined by a center position and a radius (cuboid shape).
     *
     * @param center         the center of the arena
     * @param radiusX        the half-extent along X
     * @param radiusY        the half-extent along Y
     * @param radiusZ        the half-extent along Z
     * @param escapeMargin   additional margin before escape is triggered
     * @param onPlayerEscape callback invoked when a player escapes the arena
     */
    public ArenaLock(Vec3 center, double radiusX, double radiusY, double radiusZ,
                     double escapeMargin, Runnable onPlayerEscape) {
        this(
                new AABB(
                        center.x - radiusX, center.y - radiusY, center.z - radiusZ,
                        center.x + radiusX, center.y + radiusY, center.z + radiusZ
                ),
                escapeMargin,
                onPlayerEscape
        );
    }

    /**
     * Returns the arena bounds.
     */
    public AABB bounds() {
        return this.bounds;
    }

    /**
     * Checks whether a position is inside the arena (with the escape margin).
     *
     * @param pos the position to check
     * @return true if the position is within the arena bounds (including margin)
     */
    public boolean contains(Vec3 pos) {
        return this.bounds.inflate(this.escapeMargin).contains(pos);
    }

    /**
     * Checks whether a position is strictly inside the arena (without margin).
     *
     * @param pos the position to check
     * @return true if the position is within the arena bounds
     */
    public boolean containsStrict(Vec3 pos) {
        return this.bounds.contains(pos);
    }

    /**
     * Invokes the escape callback when a player escapes the arena.
     *
     * <p>This is called by the encounter logic when a player is detected outside the arena.
     */
    public void onPlayerEscape() {
        if (this.onPlayerEscape != null) {
            this.onPlayerEscape.run();
        }
    }

    /**
     * Applies barrier blocks or visual barriers around the arena boundary.
     *
     * <p><b>Stub implementation:</b> This library does not place barrier blocks directly.
     * The consumer mod should override or replace this with its own barrier logic
     * (e.g., placing invisible barrier blocks, summoning barrier entities, or showing
     * particle effects).
     */
    public void applyBarriers() {
        // Stub: consumer mod implements actual barrier placement
    }

    /**
     * Removes barrier blocks or visual barriers placed by {@link #applyBarriers()}.
     *
     * <p><b>Stub implementation:</b> The consumer mod should implement actual removal
     * of its barriers.
     */
    public void removeBarriers() {
        // Stub: consumer mod implements actual barrier removal
    }
}
