package org.daylight.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.daylight.client.util.StateStorage;
import org.daylight.client.util.flags.TemporaryFlags;
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

    @Inject(
            method = "extractEntityInInventoryFollowsMouse",
            at = @At("RETURN")
    )
    private static void captureState(
            GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1,
            int size, float offsetY, float mouseX, float mouseY,
            LivingEntity entity, CallbackInfo ci,
            @Local(name = "renderState") EntityRenderState state
    ) {
        if (state instanceof AvatarRenderState) {
//            StateStorage.currentStates.put(state, entity.getUUID());
            StateStorage.inventoryPlayerUUID = entity.getUUID();
        }
    }
}
