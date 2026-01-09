package org.daylight.mixin.client;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.daylight.config.Data;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void renderAfter(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        Data.currentlyRenderingInventory = false;
    }
}
