package org.daylight.catify.common.mixin.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.feline.Cat;
import org.daylight.catify.common.interfaces.CustomCatTextureHolder;
import org.daylight.catify.common.util.PlayerToCatReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cat.class)
public class CatMixin implements CustomCatTextureHolder {
    @Unique
    private Identifier customTexture = null;
    @Unique
    private boolean customTextureUpdateRequired = false;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void cancelTick(CallbackInfo ci) {
        if (PlayerToCatReplacer.isDummyCat((Cat)(Object)this)) {
            ci.cancel();
        }
    }

    @Override
    public Identifier catModel$getCustomTexture() {
        return customTexture;
    }

    @Override
    public void catModel$setCustomTexture(Identifier texture) {
        this.customTexture = texture;
    }

    @Override
    public boolean catModel$shouldUpdateCustomTexture() {
        return customTextureUpdateRequired;
    }

    @Override
    public void catModel$requestCustomTextureUpdate() {
        customTextureUpdateRequired = true;
    }
}
