package org.daylight.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.daylight.config.Data;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void renderBefore(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        Data.currentlyRenderingInventory = true;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderAfter(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        Data.currentlyRenderingInventory = false;
    }
}
