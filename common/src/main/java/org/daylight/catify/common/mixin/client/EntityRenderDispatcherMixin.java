package org.daylight.catify.common.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import org.daylight.catify.common.Catify;
import org.daylight.catify.common.interfaces.CustomCatTextureHolder;
import org.daylight.catify.common.interfaces.IFeatureManager;
import org.daylight.catify.common.config.ConfigHandler;
import org.daylight.catify.common.features.CatChargeFeatureRenderer;
import org.daylight.catify.common.util.ModStateUtils;
import org.daylight.catify.common.util.PlayerToCatReplacer;
import org.daylight.catify.common.util.StateStorage;
import org.daylight.catify.common.util.flags.TemporaryFlags;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    public abstract <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S state);

    @Inject(
            method = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <S extends EntityRenderState> void render(S renderState, CameraRenderState cameraRenderState, double x, double y, double z, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) throws IllegalAccessException {
        if(!ConfigHandler.replacementActive.getCached()) return;

        if(renderState instanceof AvatarRenderState playerState) {
            if(StateStorage.currentStates.containsKey(playerState)) {
                UUID playerUuid = StateStorage.currentStates.get(playerState);
                if(playerUuid == null) return;
                Player player = PlayerToCatReplacer.getPlayerById(playerUuid);
                // StateStorage.currentStates.remove(playerState);
                if(player == null) return;
                Cat cat = (Cat) PlayerToCatReplacer.getCatForPlayer(player);
                if(cat == null) return;

                CatRenderer catRenderer;
                CatRenderState catState;
                EntityRenderer<?, ? super S> originalRenderer = this.getRenderer(renderState);
                float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

                ci.cancel();
                PlayerToCatReplacer.syncEntity2(player, cat);

                catRenderer = (CatRenderer) this.getRenderer(cat);
                catState = catRenderer.createRenderState(cat, tickDelta);

                if(playerState == null) playerState = (AvatarRenderState) getRenderer(player).createRenderState(player, tickDelta);
                if(ConfigHandler.catDamageVisible.getCached()) catState.hasRedOverlay = playerState.hasRedOverlay;
                else catState.hasRedOverlay = false;

                catState.lightCoords = playerState.lightCoords;

                if(TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.isSet() && TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.get() == true) {
                    if (StateStorage.inventoryRelativeHeadYaw != null) catState.yRot = StateStorage.inventoryRelativeHeadYaw;
                    if (StateStorage.inventoryBodyYaw != null) catState.bodyRot = StateStorage.inventoryBodyYaw;
                    if (StateStorage.inventoryPitch != null) catState.xRot = StateStorage.inventoryPitch;
                }

                CatChargeFeatureRenderer.getChargeData(cat).chargeActive = false;

                try {
                    matrices.pushPose();
//                    Vec3 vec3d = originalRenderer.getRenderOffset(renderState);
                    matrices.translate(x, y, z);
//                    matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
                    if(catRenderer instanceof CustomCatTextureHolder customCatTextureHolder) {
                        if(customCatTextureHolder.catModel$shouldUpdateCustomTexture()) {
                            catRenderer.createRenderState(cat, 0);
                        }
                    }
                    ci.cancel();

//                    if(catState instanceof CustomCatState customCatState) customCatState.catmodel$setChargeActive(false);

                    if(ModStateUtils.shouldRenderCat(player)) {
                        // Just cat
                        catRenderer.submit(catState, matrices, submitNodeCollector, cameraRenderState);
                    }

                    if (renderState.displayFireAnimation) {
                        submitNodeCollector.submitFlame(matrices, renderState, Mth.rotationAroundAxis(Mth.Y_AXIS, cameraRenderState.orientation, new Quaternionf()));
                    }

                    if (!renderState.shadowPieces.isEmpty()) {
                        submitNodeCollector.submitShadow(matrices, renderState.shadowRadius, renderState.shadowPieces);
                    }

//                    matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());

//                    if (renderState.hitbox != null) {
//                        orderedRenderCommandQueue.submitDebugHitbox(matrices, renderState, renderState.hitbox);
//                    } // TODO 1.21.11 broken
                } catch (ClassCastException e) {
                    Catify.LOGGER.error("The renderer is most likely not a EntityRenderer<CatEntity, EntityRenderState>", e);
                } finally {
                    matrices.popPose();
                }

                if(ModStateUtils.shouldRenderCharge(player)) {
                    if(catRenderer instanceof IFeatureManager featureManager) {
                        if(catState == null) catState = (CatRenderState) catRenderer.createRenderState(cat, tickDelta);

                        matrices.pushPose();

                        try {
                            float bodyYaw = catState.bodyRot; // or entity.getBodyYaw(tickDelta)
                            Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(180.0F - bodyYaw));

                            matrices.translate(x, y, z); // inherited from normal cat render
                            matrices.mulPose(new Matrix4f().rotate(rotation));
                            matrices.scale(-1.0F, -1.0F, 1.0F);
                            matrices.translate(0.0f, -1.501f, 0.0f);

                            CatChargeFeatureRenderer.getChargeData(cat).chargeActive = true;

                            featureManager.catModel$getCatChargeFeatureRenderer().customRender(matrices, submitNodeCollector, catState.lightCoords, catState, catState.yRot, catState.xRot);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            matrices.popPose();
                        }
                    }
                }


            }
        }
    }

    @Shadow
    public abstract <T extends Entity> EntityRenderer getRenderer(T entity);
}