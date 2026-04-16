package com.respawnhere;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RespawnHereMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("respawnhere");

    // ─── CONFIGURATION ───────────────────────────────────────────────
    // Radius in blocks around death point (0 = exact spot)
    public static final double RESPAWN_RADIUS = 5.0;
    // ─────────────────────────────────────────────────────────────────

    // Stores last death position per player UUID (populated by ServerPlayerDeathMixin)
    public static final Map<UUID, DeathLocation> deathLocations = new HashMap<>();

    // Players who clicked "Respawn Near Death" (populated by network handler mixin)
    public static final Set<UUID> nearDeathRespawnRequested = new HashSet<>();

    @Override
    public void onInitialize() {
        LOGGER.info("[RespawnHere] Loaded! Respawn radius: {} blocks", RESPAWN_RADIUS);
    }

    public record DeathLocation(double x, double y, double z, String dimension) {}
}
