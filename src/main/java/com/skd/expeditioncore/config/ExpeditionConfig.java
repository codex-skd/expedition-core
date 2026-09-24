package com.skd.expeditioncore.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ExpeditionConfig {

    private ExpeditionConfig() {}

    public static final ModConfigSpec SERVER_SPEC;
    public static final Server SERVER;

    static {
        Pair<Server, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Server::new);
        SERVER = pair.getLeft();
        SERVER_SPEC = pair.getRight();
    }

    public static final class Server {

        public final ModConfigSpec.DoubleValue bossHealthMultiplierPerExtraPlayer;
        public final ModConfigSpec.DoubleValue bossDamageMultiplierPerExtraPlayer;
        public final ModConfigSpec.IntValue arenaEscapeGraceTicks;

        Server(ModConfigSpec.Builder builder) {
            builder.push("boss");

            bossHealthMultiplierPerExtraPlayer = builder
                    .comment("Extra max-health fraction added per player beyond the first inside a boss arena.",
                            "E.g. 0.5 means a 2-player fight gets +50% max health, a 3-player fight +100%.")
                    .defineInRange("healthMultiplierPerExtraPlayer", 0.5, 0.0, 10.0);

            bossDamageMultiplierPerExtraPlayer = builder
                    .comment("Extra damage-dealt fraction added per player beyond the first inside a boss arena.")
                    .defineInRange("damageMultiplierPerExtraPlayer", 0.25, 0.0, 10.0);

            builder.pop();

            builder.push("arena");

            arenaEscapeGraceTicks = builder
                    .comment("Ticks a player may spend outside the arena bounds before ArenaLock's",
                            "escape callback is triggered (0 = trigger immediately).")
                    .defineInRange("escapeGraceTicks", 60, 0, 72000);

            builder.pop();
        }
    }

    /**
     * Computes the multiplayer scaling multiplier for a stat (health or damage) given the number
     * of players participating in the encounter and the per-extra-player fraction.
     *
     * <p>The consumer mod applies the returned multiplier to the boss's max health attribute (on
     * spawn/engage) or to outgoing damage, as appropriate — this library does not touch attributes
     * directly since the correct attribute instance depends on the consumer's entity class.
     *
     * @param playerCount        number of players inside the arena (minimum 1)
     * @param multiplierPerExtra the per-extra-player fraction (one of the config values above)
     * @return the multiplier to apply, always >= 1.0
     */
    public static double scalingMultiplier(int playerCount, double multiplierPerExtra) {
        int extraPlayers = Math.max(0, playerCount - 1);
        return 1.0 + (extraPlayers * multiplierPerExtra);
    }
}
