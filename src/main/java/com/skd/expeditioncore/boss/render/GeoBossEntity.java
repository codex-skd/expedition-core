package com.skd.expeditioncore.boss.render;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;

/**
 * Interface for boss entities that use GeckoLib for rendering.
 *
 * <p>A consumer mob class should implement this interface (alongside extending
 * {@link net.minecraft.world.entity.Mob}) to get the default
 * {@link AnimatableInstanceCache} helper and the {@link GeoEntity} contract.
 *
 * <p>Example:
 * <pre>{@code
 * public class MyBoss extends Monster implements GeoBossEntity {
 *     private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
 *
 *     @Override
 *     public AnimatableInstanceCache getAnimatableInstanceCache() {
 *         return this.cache;
 *     }
 *
 *     @Override
 *     public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
 *         // register animation controllers
 *     }
 * }
 * }</pre>
 */
public interface GeoBossEntity extends GeoEntity {

    /**
     * Creates a default {@link AnimatableInstanceCache} suitable for a boss entity.
     *
     * <p>This is a convenience method for boss entities that use
     * {@link SingletonAnimatableInstanceCache} (the typical choice for entities with
     * per-instance animation state).
     *
     * @param self this entity instance (pass {@code this})
     * @return a new SingletonAnimatableInstanceCache
     */
    static AnimatableInstanceCache createCache(GeoBossEntity self) {
        return new SingletonAnimatableInstanceCache(self);
    }
}
