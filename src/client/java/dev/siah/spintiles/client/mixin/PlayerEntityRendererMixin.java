package dev.siah.spintiles.client.mixin;

import dev.siah.spintiles.client.SpinRideVisualState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
abstract class PlayerEntityRendererMixin {
    @Inject(
            method = "setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFFF)V",
            at = @At("TAIL")
    )
    private void cobblemonSpinTiles$spinModel(
            AbstractClientPlayerEntity player,
            MatrixStack matrices,
            float animationProgress,
            float bodyYaw,
            float tickDelta,
            float scale,
            CallbackInfo callbackInfo
    ) {
        if (SpinRideVisualState.isActive(player.getId())) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(SpinRideVisualState.rotationDegrees(player.getId())));
        }
    }
}
