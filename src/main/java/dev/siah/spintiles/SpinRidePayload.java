package dev.siah.spintiles;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SpinRidePayload(int entityId, boolean active) implements CustomPayload {
    public static final CustomPayload.Id<SpinRidePayload> ID = new CustomPayload.Id<>(CobblemonSpinTiles.id("spin_ride"));
    public static final PacketCodec<RegistryByteBuf, SpinRidePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            SpinRidePayload::entityId,
            PacketCodecs.BOOL,
            SpinRidePayload::active,
            SpinRidePayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
