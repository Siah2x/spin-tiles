package dev.siah.spintiles;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

final class SpinRideNetworking {
    private SpinRideNetworking() {
    }

    static void sendState(ServerPlayerEntity rider, boolean active) {
        SpinRidePayload payload = new SpinRidePayload(rider.getId(), active);
        Set<ServerPlayerEntity> recipients = new HashSet<>(PlayerLookup.tracking(rider));
        recipients.add(rider);
        for (ServerPlayerEntity recipient : recipients) {
            if (ServerPlayNetworking.canSend(recipient, SpinRidePayload.ID)) {
                ServerPlayNetworking.send(recipient, payload);
            }
        }
    }
}
