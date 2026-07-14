package dev.siah.spintiles.client.mixin;

import dev.siah.spintiles.client.IceSlideInputState;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
abstract class KeyboardInputMixin extends Input {
    @Inject(method = "tick", at = @At("TAIL"))
    private void cobblemonSpinTiles$lockIceSlideInput(
            boolean slowDown,
            float slowDownFactor,
            CallbackInfo callbackInfo
    ) {
        if (IceSlideInputState.isActive()) {
            movementForward = 0.0F;
            movementSideways = 0.0F;
            pressingForward = false;
            pressingBack = false;
            pressingLeft = false;
            pressingRight = false;
        }
    }
}
