package dev.siah.spintiles;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Direction;

public record IceSlidePayload(boolean active, int directionId) implements CustomPayload {
    public static final CustomPayload.Id<IceSlidePayload> ID =
            new CustomPayload.Id<>(CobblemonSpinTiles.id("ice_slide"));
    public static final PacketCodec<RegistryByteBuf, IceSlidePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL,
            IceSlidePayload::active,
            PacketCodecs.VAR_INT,
            IceSlidePayload::directionId,
            IceSlidePayload::new
    );

    public Direction direction() {
        Direction direction = Direction.byId(directionId);
        return direction.getAxis().isHorizontal() ? direction : Direction.NORTH;
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
