package com.skd.expeditioncore.boss;

import com.skd.expeditioncore.ExpeditionCore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

/**
 * A data-attachment-backed state machine component for a {@link Mob}.
 *
 * <p>This is the public API for defining and attaching boss encounter behavior to any mob.
 * Use the {@link Builder} to construct an encounter definition, then attach it to a mob
 * instance via {@link #attach(Mob, BossEncounter)}.
 *
 * <p>The encounter definition is immutable once built. The mutable state (phase index,
 * engagement, defeat) is stored on the mob via a data attachment.
 *
 * <h3>Usage example:</h3>
 * <pre>{@code
 * BossEncounter encounter = BossEncounter.builder(myBossEntity)
 *     .phase(0.75f, mob -> { /* phase 2 logic * / })
 *     .phase(0.25f, mob -> { /* phase 3 logic * / })
 *     .arena(new ArenaLock(bounds, 2.0, () -> resetEncounter()))
 *     .bar(Component.literal("Ancient Dragon"), BossEvent.BossBarColor.RED)
 *     .loot(BossLootTables.ANCIENT_DRAGON_LOOT)
 *     .music(ModSounds.BOSS_MUSIC)
 *     .build();
 * BossEncounter.attach(myMob, encounter);
 * }</pre>
 */
public final class BossEncounter {

    // --- Attachment registration (single DeferredRegister, wired in ExpeditionCore constructor) ---
    public static final DeferredRegister<AttachmentType<?>> TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ExpeditionCore.MOD_ID);

    public static final Supplier<AttachmentType<BossEncounterData>> BOSS_ENCOUNTER =
            TYPES.register("boss_encounter", () -> AttachmentType.serializable(BossEncounterData::new).build());

    // --- Encounter definition (immutable after build) ---
    private final List<BossPhase> phases;
    private final ArenaLock arena;
    private final Component barName;
    private final BossEvent.BossBarColor barColor;
    private final BossLootTable lootTable;
    private final BossMusic music;

    private BossEncounter(Builder builder) {
        List<BossPhase> sorted = new ArrayList<>(builder.phases);
        sorted.sort((a, b) -> Float.compare(b.healthThreshold(), a.healthThreshold()));
        this.phases = List.copyOf(sorted);
        this.arena = builder.arena;
        this.barName = builder.barName;
        this.barColor = builder.barColor;
        this.lootTable = builder.lootTable;
        this.music = builder.music;
    }

    // --- Static API ---

    /**
     * Creates a new builder for a boss encounter.
     *
     * @param entity the mob type (used only for type inference; the actual mob instance is passed at attach time)
     * @return a new builder
     */
    public static Builder builder(EntityType<? extends Mob> entity) {
        return new Builder();
    }

    /**
     * Creates a new builder for a boss encounter from an existing mob instance.
     *
     * @param mob the mob instance
     * @return a new builder
     */
    public static Builder builder(Mob mob) {
        return new Builder();
    }

    /**
     * Attaches a boss encounter definition to a mob instance.
     *
     * <p>This sets up the initial encounter data on the mob. The consumer mod should call
     * this when spawning the boss mob.
     *
     * @param mob        the mob to attach the encounter to
     * @param encounter  the encounter definition
     */
    public static void attach(Mob mob, BossEncounter encounter) {
        BossEncounterData data = new BossEncounterData();
        mob.setData(BOSS_ENCOUNTER.get(), data);
    }

    /**
     * Gets the boss encounter data attached to a mob, or null if none is attached.
     *
     * @param mob the mob to query
     * @return the encounter data, or null
     */
    public static BossEncounterData getData(Mob mob) {
        return mob.getData(BOSS_ENCOUNTER.get());
    }

    /**
     * Returns true if the mob has boss encounter data attached.
     *
     * @param mob the mob to check
     * @return true if encounter data exists
     */
    public static boolean hasData(Mob mob) {
        return mob.hasData(BOSS_ENCOUNTER.get());
    }

    // --- Instance methods (on the immutable definition) ---

    /**
     * Returns the list of phases, sorted from highest health threshold to lowest.
     */
    public List<BossPhase> phases() {
        return this.phases;
    }

    /**
     * Returns the arena lock, or null if no arena was configured.
     */
    public ArenaLock arena() {
        return this.arena;
    }

    /**
     * Returns the boss bar display name, or null if not configured.
     */
    public Component barName() {
        return this.barName;
    }

    /**
     * Returns the boss bar color, or null if not configured.
     */
    public BossEvent.BossBarColor barColor() {
        return this.barColor;
    }

    /**
     * Returns the loot table reference, or null if not configured.
     */
    public BossLootTable lootTable() {
        return this.lootTable;
    }

    /**
     * Returns the boss music handler, or null if not configured.
     */
    public BossMusic music() {
        return this.music;
    }

    /**
     * Processes a health change on the boss mob, triggering phase transitions as needed.
     *
     * <p>Call this from the mob's hurt/death logic or from a tick handler.
     *
     * @param mob the boss mob instance
     */
    public void tick(Mob mob) {
        BossEncounterData data = getData(mob);
        if (data == null || data.defeated() || !data.engaged()) {
            return;
        }

        float healthFraction = mob.getHealth() / mob.getMaxHealth();
        int currentPhase = data.currentPhase();

        for (int i = 0; i < this.phases.size(); i++) {
            BossPhase phase = this.phases.get(i);
            if (phase.shouldActivate(healthFraction) && i > currentPhase) {
                data.setCurrentPhase(i);
                phase.onEnter().accept(mob);
                break;
            }
        }
    }

    /**
     * Engages the boss encounter, setting the engaged state to true.
     *
     * @param mob the boss mob instance
     */
    public void engage(Mob mob) {
        BossEncounterData data = getData(mob);
        if (data != null) {
            data.setEngaged(true);
            data.setCurrentPhase(-1);
        }
    }

    /**
     * Marks the boss as defeated.
     *
     * @param mob the boss mob instance
     */
    public void defeat(Mob mob) {
        BossEncounterData data = getData(mob);
        if (data != null) {
            data.setDefeated(true);
            data.setEngaged(false);
        }
    }

    // --- Builder ---

    /**
     * Builder for constructing an immutable {@link BossEncounter}.
     */
    public static final class Builder {
        private final List<BossPhase> phases = new ArrayList<>();
        private ArenaLock arena;
        private Component barName;
        private BossEvent.BossBarColor barColor;
        private BossLootTable lootTable;
        private BossMusic music;

        private Builder() {}

        /**
         * Adds a phase transition at the given health threshold.
         *
         * @param healthThreshold the health fraction (0.0–1.0) at which this phase begins
         * @param onEnter         callback invoked when the boss enters this phase
         * @return this builder
         */
        public Builder phase(float healthThreshold, Consumer<Mob> onEnter) {
            this.phases.add(new BossPhase(healthThreshold, onEnter));
            return this;
        }

        /**
         * Sets the arena boundary for the encounter.
         *
         * @param arena the arena lock
         * @return this builder
         */
        public Builder arena(ArenaLock arena) {
            this.arena = arena;
            return this;
        }

        /**
         * Sets the boss bar display name and color.
         *
         * @param name  the display name
         * @param color the bar color
         * @return this builder
         */
        public Builder bar(Component name, BossEvent.BossBarColor color) {
            this.barName = name;
            this.barColor = color;
            return this;
        }

        /**
         * Sets the boss bar display name (using a string).
         *
         * @param name  the display name as a string
         * @param color the bar color
         * @return this builder
         */
        public Builder bar(String name, BossEvent.BossBarColor color) {
            this.barName = Component.literal(name);
            this.barColor = color;
            return this;
        }

        /**
         * Sets the loot table to roll when the boss is defeated.
         *
         * @param lootTable the boss loot table reference
         * @return this builder
         */
        public Builder loot(BossLootTable lootTable) {
            this.lootTable = lootTable;
            return this;
        }

        /**
         * Sets the loot table key to roll when the boss is defeated.
         *
         * @param lootTableKey the resource key of the loot table
         * @return this builder
         */
        public Builder loot(ResourceKey<LootTable> lootTableKey) {
            this.lootTable = new BossLootTable(lootTableKey);
            return this;
        }

        /**
         * Sets the music to play during the encounter.
         *
         * @param soundEvent the sound event to loop
         * @return this builder
         */
        public Builder music(SoundEvent soundEvent) {
            this.music = new BossMusic(soundEvent);
            return this;
        }

        /**
         * Sets the music handler for the encounter.
         *
         * @param music the boss music handler
         * @return this builder
         */
        public Builder music(BossMusic music) {
            this.music = music;
            return this;
        }

        /**
         * Builds the immutable boss encounter definition.
         *
         * @return the built encounter
         * @throws IllegalStateException if no phases were added
         */
        public BossEncounter build() {
            if (this.phases.isEmpty()) {
                throw new IllegalStateException("A BossEncounter must have at least one phase");
            }
            return new BossEncounter(this);
        }
    }
}
