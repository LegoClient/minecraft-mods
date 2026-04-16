package com.respawnhere.mixin;

import com.respawnhere.RespawnHereMod;
import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerRespawnMixin {

    private static final Random RANDOM = new Random();

    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void onRespawnPlayer(ServerPlayerEntity oldPlayer, boolean alive, @Nullable Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        if (alive) return; // alive = true means this is an end portal teleport, not a death respawn

        ServerPlayerEntity newPlayer = cir.getReturnValue();
        UUID id = newPlayer.getUuid();

        // Only teleport if the near-death respawn was requested
        if (!RespawnHereMod.nearDeathRespawnRequested.contains(id)) return;
        RespawnHereMod.nearDeathRespawnRequested.remove(id);

        RespawnHereMod.DeathLocation death = RespawnHereMod.deathLocations.get(id);
        if (death == null) {
            RespawnHereMod.LOGGER.warn("[RespawnHere] No death location stored for player {}", newPlayer.getName().getString());
            return;
        }

        // Check dimension match — only teleport if died in same dimension
        String currentDim = newPlayer.getWorld().getRegistryKey().getValue().toString();
        if (!currentDim.equals(death.dimension())) {
            RespawnHereMod.LOGGER.warn("[RespawnHere] Dimension mismatch, skipping teleport.");
            return;
        }

        double radius = RespawnHereMod.RESPAWN_RADIUS;
        double offsetX = (RANDOM.nextDouble() * 2 - 1) * radius;
        double offsetZ = (RANDOM.nextDouble() * 2 - 1) * radius;

        double tx = death.x() + offsetX;
        double ty = death.y();
        double tz = death.z() + offsetZ;

        // Teleport directly — no permission check, works in singleplayer
        newPlayer.requestTeleport(tx, ty, tz);
        RespawnHereMod.LOGGER.info("[RespawnHere] Teleported {} to near-death location ({}, {}, {})", 
                newPlayer.getName().getString(), tx, ty, tz);
    }
}
