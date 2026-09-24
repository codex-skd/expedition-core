package com.skd.expeditioncore.boss.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Mob;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * A thin abstract base class extending GeckoLib's {@link GeoEntityRenderer} for boss entities.
 *
 * <p>This reduces boilerplate in consumer mods by providing a no-fixed-model base.
 * The consumer supplies the model/texture via the constructor, and this base class
 * passes it through to the GeckoLib renderer.
 *
 * <p>Example:
 * <pre>{@code
 * public class MyBossRenderer extends GeoBossRenderer<MyBoss> {
 *     public MyBossRenderer(EntityRendererProvider.Context context) {
 *         super(context, new MyBossGeoModel());
 *     }
 * }
 * }</pre>
 *
 * @param <T> the mob type, which must extend {@link Mob} and implement {@link GeoAnimatable}
 */
public abstract class GeoBossRenderer<T extends Mob & GeoAnimatable> extends GeoEntityRenderer<T> {

    /**
     * Creates a new GeoBossRenderer with the given model.
     *
     * @param renderManager the entity renderer context
     * @param model         the GeckoLib model to use for rendering
     */
    protected GeoBossRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
    }
}
