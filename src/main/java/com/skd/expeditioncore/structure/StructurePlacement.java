package com.skd.expeditioncore.structure;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

/**
 * Static helpers for common structure placement settings.
 */
public final class StructurePlacement {

    private StructurePlacement() {}

    /**
     * Creates a simple {@link RandomSpreadStructurePlacement} with the given spacing, separation, and salt.
     *
     * <p>Uses {@link RandomSpreadType#LINEAR} spread and {@link net.minecraft.world.level.levelgen.structure.placement.StructurePlacement$FrequencyReductionMethod#DEFAULT}
     * frequency reduction.
     *
     * @param spacing    the average spacing in chunks between structures
     * @param separation the minimum separation in chunks between structures
     * @param salt       the random salt for placement
     * @return a new RandomSpreadStructurePlacement
     */
    public static RandomSpreadStructurePlacement spread(int spacing, int separation, int salt) {
        return new RandomSpreadStructurePlacement(spacing, separation, RandomSpreadType.LINEAR, salt);
    }

    /**
     * Creates a {@link RandomSpreadStructurePlacement} with the given parameters.
     *
     * @param spacing    the average spacing in chunks between structures
     * @param separation the minimum separation in chunks between structures
     * @param spreadType the random spread type (LINEAR or GAUSSIAN)
     * @param salt       the random salt for placement
     * @return a new RandomSpreadStructurePlacement
     */
    public static RandomSpreadStructurePlacement spread(
            int spacing,
            int separation,
            RandomSpreadType spreadType,
            int salt
    ) {
        return new RandomSpreadStructurePlacement(spacing, separation, spreadType, salt);
    }

    /**
     * Checks whether a given biome holder matches a biome tag.
     *
     * @param biome    the biome holder to check
     * @param biomeTag the tag key to test against
     * @return true if the biome is in the given tag
     */
    public static boolean matchesBiomeTag(Holder<Biome> biome, TagKey<Biome> biomeTag) {
        return biome.is(biomeTag);
    }
}
