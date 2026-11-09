package org.daylight.mixin.client;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.daylight.interfaces.CustomCatState;
import org.daylight.interfaces.IShadowHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> implements IShadowHolder {
    @Shadow
    public abstract float getShadowRadius(S state);

    @Shadow
    public abstract float getShadowStrength(S state);

    public float getShadowRadiusAccessor(CatRenderState state) {
        return getShadowRadius((S) state);
    }

    public float getShadowOpacityAccessor(CatRenderState state) {
        return getShadowStrength((S) state);
    }

    @Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("RETURN"))
    private void onGetAndUpdateRenderState(Entity entity, float tickDelta, CallbackInfoReturnable<EntityRenderState> cir) {
        EntityRenderState state = cir.getReturnValue();
        if (state instanceof CustomCatState customCatState) {
            customCatState.catmodel$setCurrentEntityId(entity.getUUID());
        }
    }
}
