package dev.siah.spintiles;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

final class IceSlideNetworking {
    private IceSlideNetworking() {
    }

    static void sendState(ServerPlayerEntity player, boolean active, Direction direction) {
        if (ServerPlayNetworking.canSend(player, IceSlidePayload.ID)) {
            ServerPlayNetworking.send(player, new IceSlidePayload(active, direction.getId()));
        }
    }
}
