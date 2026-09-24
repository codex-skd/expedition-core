package com.skd.expeditioncore.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;

/**
 * Data attachment payload for the {@link BossEncounter} state machine.
 *
 * <p>Stored on a {@link net.minecraft.world.entity.Mob} via NeoForge data attachments.
 * Tracks the boss's current phase, engagement state, and defeat status.
 *
 * <p>Serialization uses simple {@link CompoundTag} save/load methods via
 * {@link net.neoforged.neoforge.common.util.INBTSerializable}.
 */
public class BossEncounterData implements net.neoforged.neoforge.common.util.INBTSerializable<CompoundTag> {

    /** {@code -1} means no phase has been entered yet (baseline, before the first threshold is crossed). */
    private int currentPhase = -1;
    private boolean engaged;
    private boolean defeated;

    public BossEncounterData() {}

    public int currentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(int phase) {
        this.currentPhase = phase;
    }

    public boolean engaged() {
        return engaged;
    }

    public void setEngaged(boolean engaged) {
        this.engaged = engaged;
    }

    public boolean defeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("CurrentPhase", this.currentPhase);
        tag.putBoolean("Engaged", this.engaged);
        tag.putBoolean("Defeated", this.defeated);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.currentPhase = tag.getInt("CurrentPhase");
        this.engaged = tag.getBoolean("Engaged");
        this.defeated = tag.getBoolean("Defeated");
    }
}
