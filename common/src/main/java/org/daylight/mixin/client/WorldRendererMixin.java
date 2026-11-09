package org.daylight.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.daylight.features.CatChargeFeatureRenderer;
import org.daylight.util.StateStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Inject(
            method = "submitEntities",
            at = @At("HEAD")
    )
    private void pushEntityRenders(CallbackInfo ci) {
        if(!Minecraft.getInstance().isPaused()) {
            CatChargeFeatureRenderer.moveGlobalTextureForward(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
        }
    }

    @Inject(
            method = "submitEntities",
            at = @At("TAIL")
    )
    private void pushEntityRenders(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector output, CallbackInfo ci) {
        for (EntityRenderState entityRenderState : levelRenderState.entityRenderStates) {
            if(entityRenderState instanceof AvatarRenderState) {
//                System.out.println("Removing from WorldRenderer: " + entityRenderState);
                StateStorage.currentStates.remove(entityRenderState);
            }
        }
    }

//    @Inject(
//            method = "fillEntityRenderStates",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/WorldRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;",
//                    shift = At.Shift.AFTER
//            ),
//            locals = LocalCapture.CAPTURE_FAILSOFT
//    )
//    private void captureState(
//            Camera camera, Frustum frustum, RenderTickCounter tickCounter, WorldRenderState renderStates,
//            CallbackInfo ci,
//            Vec3d vec3d,
//            double d, double e, double f,
//            TickManager tickManager,
//            boolean bl,
//            Entity entity,
//            BlockPos blockPos,
//            float g,
//            EntityRenderState entityRenderState) {
//        System.out.println("Added from World: " + entityRenderState);
//        StateStorage.currentStates.put(entityRenderState, entity.getUuid());
//    }

//    @ModifyVariable(
//            method = "fillEntityRenderStates",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/render/WorldRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;",
//                    shift = At.Shift.AFTER
//            ),
//            ordinal = 0
//    )
//    private EntityRenderState onCaptureState(EntityRenderState state, Entity entity, CallbackInfo ci) {
//        if (state != null) {
//            StateStorage.currentStates.put(state, entity.getUuid());
//            System.out.println("Captured EntityRenderState: " + state + " ← " + entity.getName().getString());
//        }
//        return state;
//    }
}
