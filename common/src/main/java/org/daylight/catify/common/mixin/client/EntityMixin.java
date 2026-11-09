package org.daylight.catify.common.mixin.client;

import net.minecraft.world.entity.Entity;
import org.daylight.catify.common.features.CatChargeFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "remove", at = @At("HEAD"))
    private void onRemove(CallbackInfo ci) {
        CatChargeFeatureRenderer.CHARGE_DATA.remove(((Entity) (Object) this).getUUID());
    }
}
