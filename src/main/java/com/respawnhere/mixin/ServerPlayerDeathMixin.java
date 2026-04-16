package com.respawnhere.mixin;

import com.respawnhere.RespawnHereMod;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerDeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(net.minecraft.entity.damage.DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        Vec3d pos = player.getPos();
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        RespawnHereMod.deathLocations.put(player.getUuid(),
                new RespawnHereMod.DeathLocation(pos.x, pos.y, pos.z, dimension));
        RespawnHereMod.LOGGER.info("[RespawnHere] Death recorded for {} at {}, {}, {}",
                player.getName().getString(), pos.x, pos.y, pos.z);
    }
}
