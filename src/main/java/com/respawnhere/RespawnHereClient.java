package com.respawnhere;

import com.respawnhere.mixin.RespawnHereClientState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

public class RespawnHereClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Add "Respawn Near Death" button via ScreenEvents to avoid Mixin conflict with Lunar Client.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof DeathScreen)) return;

            // Mirror vanilla button layout: Respawn at height/4+72, Title at height/4+96,
            // our button directly below at height/4+124.
            int buttonY = scaledHeight / 4 + 124;

            Screens.getButtons(screen).add(ButtonWidget.builder(
                    Text.literal("Respawn Near Death"),
                    button -> {
                        ClientPlayerEntity player = client.player;
                        if (player != null) {
                            // In singleplayer the server runs in the same JVM — mark for teleport
                            if (client.getServer() != null) {
                                RespawnHereMod.nearDeathRespawnRequested.add(player.getUuid());
                            }
                            player.requestRespawn();
                        }
                        client.setScreen(null);
                    })
                    .dimensions(scaledWidth / 2 - 100, buttonY, 200, 20)
                    .build()
            );
        });

        // After respawn, if the flag is set, notify the server to teleport us
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!RespawnHereClientState.pendingNearDeathRespawn) return;

            ClientPlayerEntity player = client.player;
            if (player == null || !player.isAlive()) return;

            // Player is alive — clear flag, server mixin will handle the teleport
            RespawnHereClientState.pendingNearDeathRespawn = false;
            RespawnHereMod.LOGGER.info("[RespawnHere] Client flagged near-death respawn, server will teleport.");
        });
    }
}
