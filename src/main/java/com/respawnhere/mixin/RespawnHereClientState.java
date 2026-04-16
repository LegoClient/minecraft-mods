package com.respawnhere.mixin;

/**
 * Simple static flag shared between the death screen button and the
 * client tick listener that handles the teleport after respawn.
 */
public class RespawnHereClientState {
    public static boolean pendingNearDeathRespawn = false;
}
