package com.skd.expeditioncore.boss;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/**
 * Plays and stops a looping {@link SoundEvent} for players inside the encounter
 * while the boss is engaged.
 *
 * <p>Server-side methods manage the state and send client-bound sound packets.
 * The consumer mod should call {@link #startPlaying(ServerPlayer)} and
 * {@link #stopPlaying(ServerPlayer)} from appropriate server-side hooks.
 */
public class BossMusic {

    private final SoundEvent soundEvent;
    private final float volume;
    private final float pitch;

    /**
     * Creates a boss music handler.
     *
     * @param soundEvent the sound event to play in a loop
     */
    public BossMusic(SoundEvent soundEvent) {
        this(soundEvent, 1.0f, 1.0f);
    }

    /**
     * Creates a boss music handler with custom volume and pitch.
     *
     * @param soundEvent the sound event to play in a loop
     * @param volume     the volume (0.0 to 1.0)
     * @param pitch      the pitch (0.5 to 2.0)
     */
    public BossMusic(SoundEvent soundEvent, float volume, float pitch) {
        this.soundEvent = soundEvent;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * Returns the sound event being played.
     */
    public SoundEvent soundEvent() {
        return this.soundEvent;
    }

    /**
     * Starts playing the boss music for a specific player.
     *
     * <p>This method is called server-side and sends a client-bound sound packet.
     *
     * @param player the player to start playing music for
     */
    public void startPlaying(ServerPlayer player) {
        player.connection.send(
                new net.minecraft.network.protocol.game.ClientboundSoundPacket(
                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(this.soundEvent),
                        SoundSource.HOSTILE,
                        player.getX(), player.getY(), player.getZ(),
                        this.volume,
                        this.pitch,
                        player.getRandom().nextLong()
                )
        );
    }

    /**
     * Stops the boss music for a specific player by sending a stop-sound packet.
     *
     * @param player the player to stop music for
     */
    public void stopPlaying(ServerPlayer player) {
        player.connection.send(
                new net.minecraft.network.protocol.game.ClientboundStopSoundPacket(
                        this.soundEvent.getLocation(),
                        SoundSource.HOSTILE
                )
        );
    }

    /**
     * Starts playing the boss music for a list of players.
     *
     * @param players the players to start playing music for
     */
    public void startPlayingForAll(List<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            startPlaying(player);
        }
    }

    /**
     * Stops the boss music for a list of players.
     *
     * @param players the players to stop music for
     */
    public void stopPlayingForAll(List<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            stopPlaying(player);
        }
    }
}
