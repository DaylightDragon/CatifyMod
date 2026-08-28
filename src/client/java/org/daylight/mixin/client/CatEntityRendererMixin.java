package org.daylight.mixin.client;

import net.minecraft.client.model.animal.feline.AdultCatModel;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.feline.Cat;
import org.daylight.CustomCatTextureHolder;
import org.daylight.IFeatureManager;
import org.daylight.ModResources;
import org.daylight.features.CatChargeFeatureRenderer;
import org.daylight.util.PlayerToCatReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatRenderer.class)
public abstract class CatEntityRendererMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(EntityRendererProvider.Context context, CallbackInfo ci) {
        var feature = new CatChargeFeatureRenderer((RenderLayerParent<CatRenderState, AdultCatModel>) this, context.getModelSet(), ModResources.GHOST_TEXTURE);
        if((Object) this instanceof IFeatureManager featureManager) {
            featureManager.catmodel$addFeature(feature);
        }
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/animal/feline/Cat;Lnet/minecraft/client/renderer/entity/state/CatRenderState;F)V",
            at = @At("TAIL")
    )
    private void onUpdateRenderState(Cat cat, CatRenderState state, float tickDelta, CallbackInfo ci) {
        if (isCustomCat(cat)) {
            Identifier customTexture = getCatEntityCustomTexture(cat);
            if(customTexture == null) return;
            state.texture = customTexture;
        }
    }

    private boolean isCustomCat(Cat cat) {
        return PlayerToCatReplacer.isDummyCat(cat);
    }

    private Identifier getCatEntityCustomTexture(Cat cat) {
        if(cat instanceof CustomCatTextureHolder customCatTextureHolder) {
            return customCatTextureHolder.catModel$getCustomTexture();
        }
        return null;
    }
}
