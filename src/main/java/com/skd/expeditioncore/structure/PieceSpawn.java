package com.skd.expeditioncore.structure;

import com.skd.expeditioncore.ExpeditionCore;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * A small registry letting consumer mods register callbacks that run when a specific
 * jigsaw piece is placed during structure generation.
 *
 * <p>This allows controlled mob/loot spawning inside a room when the jigsaw piece is placed.
 * The consumer mod identifies each piece by a {@link ResourceLocation} id convention
 * (e.g. read from a piece's NBT/name).
 *
 * <p><b>Note:</b> This class does <em>not</em> subscribe to any generation event.
 * The consumer mod calls {@link #trigger(ResourceLocation, ServerLevelAccessor, BoundingBox)}
 * manually from its own structure-implementation-specific hooks.
 */
public final class PieceSpawn {

    private static final Map<ResourceLocation, BiConsumer<ServerLevelAccessor, BoundingBox>> CALLBACKS = new HashMap<>();

    private PieceSpawn() {}

    /**
     * Registers a callback to be invoked when the given jigsaw piece is placed.
     *
     * @param id       the resource location identifying the piece (e.g. "my_structure:boss_room")
     * @param callback a consumer that receives the world accessor and the piece's bounding box
     */
    public static void register(ResourceLocation id, BiConsumer<ServerLevelAccessor, BoundingBox> callback) {
        if (CALLBACKS.containsKey(id)) {
            ExpeditionCore.LOGGER.warn("PieceSpawn callback already registered for {}, overwriting", id);
        }
        CALLBACKS.put(id, callback);
    }

    /**
     * Triggers the registered callback for the given piece id, if one exists.
     *
     * <p>Call this from your structure's piece-placement hook (e.g. from a jigsaw
     * placement event handler or a custom generation listener).
     *
     * @param id    the resource location identifying the placed piece
     * @param level the server level accessor for the world
     * @param box   the bounding box of the placed piece
     */
    public static void trigger(ResourceLocation id, ServerLevelAccessor level, BoundingBox box) {
        BiConsumer<ServerLevelAccessor, BoundingBox> callback = CALLBACKS.get(id);
        if (callback != null) {
            callback.accept(level, box);
        }
    }

    /**
     * Returns true if a callback is registered for the given piece id.
     *
     * @param id the resource location identifying the piece
     * @return true if a callback exists
     */
    public static boolean hasCallback(ResourceLocation id) {
        return CALLBACKS.containsKey(id);
    }

    /**
     * Removes the callback for the given piece id, if any.
     *
     * @param id the resource location identifying the piece
     */
    public static void unregister(ResourceLocation id) {
        CALLBACKS.remove(id);
    }
}
