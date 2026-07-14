package dev.siah.spintiles.client;

import dev.siah.spintiles.IceSlideRules;
import dev.siah.spintiles.IceSlidePayload;
import dev.siah.spintiles.SpinRidePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.Vec3d;

public final class CobblemonSpinTilesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SpinRidePayload.ID, (payload, context) ->
                SpinRideVisualState.setActive(payload.entityId(), payload.active()));
        ClientPlayNetworking.registerGlobalReceiver(IceSlidePayload.ID, (payload, context) ->
                IceSlideInputState.setActive(payload.active(), payload.direction()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && IceSlideInputState.isActive()) {
                Vec3d velocity = client.player.getVelocity();
                client.player.setVelocity(IceSlideRules.lockedVelocity(
                        IceSlideInputState.direction(),
                        velocity.y
                ));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            SpinRideVisualState.clear();
            IceSlideInputState.clear();
        });
    }
}
