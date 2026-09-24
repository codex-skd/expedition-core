package com.skd.expeditioncore.structure;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

/**
 * Static helpers for registering jigsaw structure pools and structure pieces.
 */
public final class JigsawUtils {

    private JigsawUtils() {}

    /**
     * Builds a {@link StructureTemplatePool} from a map of piece names to weights.
     *
     * <p>Each entry maps a jigsaw template name (the path within the structure template directory)
     * to its weight. The fallback pool holder is used when no other pieces can be placed.
     *
     * @param fallback   holder for the fallback pool (can be a self-referencing empty pool)
     * @param pieces     map of template name to weight
     * @param projection the projection type for all elements (RIGID, TERRAIN_MATCHING, etc.)
     * @return a new StructureTemplatePool ready for registration
     */
    public static StructureTemplatePool buildPool(
            Holder<StructureTemplatePool> fallback,
            Map<String, Integer> pieces,
            StructureTemplatePool.Projection projection
    ) {
        List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> templates =
                new ArrayList<>();

        for (Map.Entry<String, Integer> entry : pieces.entrySet()) {
            String templateName = entry.getKey();
            int weight = entry.getValue();
            Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> elementFactory =
                    StructurePoolElement.single(templateName);
            templates.add(Pair.of(elementFactory, weight));
        }

        return new StructureTemplatePool(fallback, templates, projection);
    }

    /**
     * Builds a {@link StructureTemplatePool} from a list of pieces with a single processor list.
     *
     * @param fallback      holder for the fallback pool
     * @param processorList holder for the processor list to apply to all pieces
     * @param pieces        list of (template name, weight) pairs
     * @param projection    the projection type for all elements
     * @return a new StructureTemplatePool ready for registration
     */
    public static StructureTemplatePool buildPool(
            Holder<StructureTemplatePool> fallback,
            Holder<StructureProcessorList> processorList,
            List<Pair<String, Integer>> pieces,
            StructureTemplatePool.Projection projection
    ) {
        List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> templates =
                new ArrayList<>();

        for (Pair<String, Integer> entry : pieces) {
            String templateName = entry.getFirst();
            int weight = entry.getSecond();
            Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> elementFactory =
                    StructurePoolElement.single(templateName, processorList);
            templates.add(Pair.of(elementFactory, weight));
        }

        return new StructureTemplatePool(fallback, templates, projection);
    }

    /**
     * Looks up a registered structure template pool by its {@link ResourceKey}.
     *
     * @param registries the registry access (from a server or world)
     * @param key        the resource key of the pool to look up
     * @return an Optional containing the pool holder, or empty if not found
     */
    public static Optional<Holder.Reference<StructureTemplatePool>> getPool(
            HolderLookup.Provider registries, ResourceKey<StructureTemplatePool> key
    ) {
        return registries.lookupOrThrow(net.minecraft.core.registries.Registries.TEMPLATE_POOL).get(key);
    }
}
