package org.daylight.catify.common.mixin.client;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.daylight.catify.common.util.flags.TemporaryFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
    @Inject(method = "extractEntityInInventoryFollowsMouse", at = @At("HEAD"))
    private static void beforeInventoryRender(CallbackInfo ci) {
        TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.set(true);
    }

    @Inject(method = "extractEntityInInventoryFollowsMouse", at = @At("TAIL"))
    private static void afterInventoryRender(CallbackInfo ci) {
        TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.clear();
    }
}
