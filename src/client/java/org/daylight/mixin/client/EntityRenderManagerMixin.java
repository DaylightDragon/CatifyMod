package org.daylight.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.daylight.CatifyModClient;
import org.daylight.CustomCatTextureHolder;
import org.daylight.IFeatureManager;
import org.daylight.config.ConfigHandler;
import org.daylight.features.CatChargeFeatureRenderer;
import org.daylight.util.ModStateUtils;
import org.daylight.util.PlayerToCatReplacer;
import org.daylight.util.StateStorage;
import org.daylight.util.WhitelistedScreensUtil;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.UUID;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderManagerMixin {
//    @Shadow
//    private boolean renderShadows;
//    private boolean renderShadows() { return true; } // TODO

    @Shadow
    public abstract <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S state);

    @Shadow
    @Final
    public Options options;

    /*
    @SuppressWarnings("unchecked")
    @Inject(
            method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <E extends Entity> void onRenderEntity(
            E entity,
            double x, double y, double z,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            CallbackInfo ci
    ) {
        if (ConfigHandler.replacementActive.getCached() && entity instanceof AbstractClientPlayerEntity player &&
                PlayerToCatReplacer.shouldReplace(player)) {
            CatEntity existingCat = (CatEntity) PlayerToCatReplacer.getCatForPlayer(player);
            EntityRenderer<CatEntity, EntityRenderState> catRenderer = null;
            EntityRenderer<PlayerEntity, PlayerEntityRenderState> playerRenderer = null;
            boolean visible = !player.isInvisible();
            InvisibilityBehaviour behaviour = (InvisibilityBehaviour) ConfigHandler.invisibilityBehaviour.getCached();
            CatEntityRenderState catState = null;
            PlayerEntityRenderState playerState = null;

            if (existingCat != null) {
                PlayerToCatReplacer.syncEntity2(player, existingCat);

                matrices.push();
                matrices.translate(x, y, z);

                try {
                    catRenderer = (EntityRenderer<CatEntity, EntityRenderState>) this.getRenderer(existingCat);
                    if(catRenderer instanceof CustomCatTextureHolder customCatTextureHolder) {
                        if(customCatTextureHolder.catModel$shouldUpdateCustomTexture()) {
//                            CatifyModClient.LOGGER.info("RenderDispatcher updates CatRenderer state");
                            catRenderer.getAndUpdateRenderState(existingCat, 0);
                        }
                    }
                    ci.cancel();

                    catState = (CatEntityRenderState) catRenderer.getAndUpdateRenderState(existingCat, tickDelta);
//                    if(catState instanceof CustomCatState customCatState) customCatState.catmodel$setChargeActive(false);
                    CatChargeFeatureRenderer.getChargeData(existingCat).chargeActive = false;

                    if(playerState == null) playerState = (PlayerEntityRenderState) getRenderer(player).getAndUpdateRenderState(player, tickDelta);
                    if(ConfigHandler.catDamageVisible.getCached()) catState.hurt = playerState.hurt;
                    else catState.hurt = false;

                    if(ModStateUtils.shouldRenderCat(player)) {
                        // Just cat
                        catRenderer.render(catState, matrices, vertexConsumers, light);
                    }
                } catch (ClassCastException e) {
                    CatifyModClient.LOGGER.error("The renderer is most likely not a EntityRenderer<CatEntity, EntityRenderState>", e);
                } finally {
                    matrices.pop();
                }

                // Shadow, after the cat render

                if(ModStateUtils.shouldRenderShadow(player)) {
//                    if(catRenderer == null) catRenderer = (EntityRenderer<CatEntity, EntityRenderState>) this.getRenderer(existingCat);
                    if(playerRenderer == null) playerRenderer = this.getRenderer(player);
//                    if(catState == null) catState = (CatEntityRenderState) catRenderer.getAndUpdateRenderState(existingCat, tickDelta);
                    playerState = (PlayerEntityRenderState) getRenderer(player).getAndUpdateRenderState(player, tickDelta);
                    if (this.gameOptions.getEntityShadows().getValue()
                            && this.renderShadows) {
//                            && !player.isInvisible()) {

                        try {
//                            Vec3d vec3d = playerRenderer.getPositionOffset(playerState);

                            matrices.push();
                            matrices.translate(x, y, z);

                            if (playerRenderer instanceof EntityRendererAccessor accessor) {
                                float shadowRadius = accessor.callGetShadowRadius(catState);
                                float opacity = (float) ((1.0 - (playerState.squaredDistanceToCamera) / 256.0)
                                        * (double) accessor.callGetShadowOpacity(catState));

//                                System.out.println(x + " " + vec3d.getX() + " " +  y + " " + vec3d.getY() + " " +  z + " " + vec3d.getZ());
//                                System.out.println(shadowRadius + " " + opacity + matrices.peek().getPositionMatrix());

                                if (shadowRadius > 0 && opacity > 0) {
                                    renderShadow(
                                            matrices,
                                            vertexConsumers,
                                            playerState,
                                            opacity,
                                            player.getWorld(),
                                            Math.min(shadowRadius, 32.0f)
                                    );
                                }
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        } finally {
                            matrices.pop();
                        }
                    }
                }

//                CatEntityRenderer
//                PlayerEntityRenderer

                // Charge

                if(ModStateUtils.shouldRenderCharge(player)) {
                    if(catRenderer instanceof IFeatureManager featureManager) {
                        if(catState == null) catState = (CatEntityRenderState) catRenderer.getAndUpdateRenderState(existingCat, tickDelta);

                        matrices.push();

                        try {
                            float bodyYaw = catState.bodyYaw; // or entity.getBodyYaw(tickDelta)
                            matrices.translate(x, y, z); // inherited from normal cat render
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
                            matrices.scale(-1.0F, -1.0F, 1.0F);
                            matrices.translate(0.0f, -1.501f, 0.0f);

                            CatChargeFeatureRenderer.getChargeData(existingCat).chargeActive = true;

                            featureManager.getCatChargeFeatureRenderer().customRender(matrices, vertexConsumers, light, catState, catState.relativeHeadYaw, catState.pitch);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            matrices.pop();
                        }
                    }
                }

                // Hitboxes

                if (shouldRenderHitboxes()) {
                    if(playerState == null) playerState = (PlayerEntityRenderState) getRenderer(player).getAndUpdateRenderState(player, tickDelta);
                    if(playerState.hitbox != null) {
                        try {
                            matrices.push();
                            matrices.translate(x, y, z);
                            renderHitboxes(matrices, playerState, playerState.hitbox, vertexConsumers);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            matrices.pop();
                        }
                    }
                }
            }
        }
    }*/

    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <S extends EntityRenderState> void render(S renderState, CameraRenderState cameraRenderState, double x, double y, double z, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, CallbackInfo ci) throws IllegalAccessException {
        if(!ConfigHandler.replacementActive.getCached()) return;

        Screen screen = Minecraft.getInstance().screen;
        if(StateStorage.currentlyRenderingUi && screen != null && !WhitelistedScreensUtil.isWhitelisted(screen)) return;

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

                if(StateStorage.inventoryRelativeHeadYaw != null) catState.yRot = StateStorage.inventoryRelativeHeadYaw;
                if(StateStorage.inventoryBodyYaw != null) catState.bodyRot = StateStorage.inventoryBodyYaw;
                if(StateStorage.inventoryPitch != null) catState.xRot = StateStorage.inventoryPitch;

                CatChargeFeatureRenderer.getChargeData(cat).chargeActive = false;

                try {
                    matrices.pushPose();
                    Vec3 vec3d = originalRenderer.getRenderOffset(renderState);
                    matrices.translate(x, y, z);
//                    matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
                    if(catRenderer instanceof CustomCatTextureHolder customCatTextureHolder) {
                        if(customCatTextureHolder.catModel$shouldUpdateCustomTexture()) {
//                            CatifyModClient.LOGGER.info("RenderDispatcher updates CatRenderer state");
                            catRenderer.createRenderState(cat, 0);
                        }
                    }
                    ci.cancel();


//                    if(catState instanceof CustomCatState customCatState) customCatState.catmodel$setChargeActive(false);

                    if(ModStateUtils.shouldRenderCat(player)) {
                        // Just cat
                        catRenderer.submit(catState, matrices, orderedRenderCommandQueue, cameraRenderState);
                    }

                    if (renderState.displayFireAnimation) {
                        orderedRenderCommandQueue.submitFlame(matrices, renderState, Mth.rotationAroundAxis(Mth.Y_AXIS, cameraRenderState.orientation, new Quaternionf()));
                    }

                    if (!renderState.shadowPieces.isEmpty()) {
                        orderedRenderCommandQueue.submitShadow(matrices, renderState.shadowRadius, renderState.shadowPieces);
                    }

//                    matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());

//                    if (renderState.hitbox != null) {
//                        orderedRenderCommandQueue.submitDebugHitbox(matrices, renderState, renderState.hitbox);
//                    } // TODO 1.21.11 broken
                } catch (ClassCastException e) {
                    CatifyModClient.LOGGER.error("The renderer is most likely not a EntityRenderer<CatEntity, EntityRenderState>", e);
                } finally {
                    matrices.popPose();
                }

                if(ModStateUtils.shouldRenderCharge(player)) {
                    if(catRenderer instanceof IFeatureManager featureManager) {
                        if(catState == null) catState = (CatRenderState) catRenderer.createRenderState(cat, tickDelta);

                        matrices.pushPose();

                        try {
                            float bodyYaw = catState.bodyRot; // or entity.getBodyYaw(tickDelta)
                            matrices.translate(x, y, z); // inherited from normal cat render
                            matrices.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
                            matrices.scale(-1.0F, -1.0F, 1.0F);
                            matrices.translate(0.0f, -1.501f, 0.0f);

                            CatChargeFeatureRenderer.getChargeData(cat).chargeActive = true;

                            featureManager.getCatChargeFeatureRenderer().customRender(matrices, orderedRenderCommandQueue, catState.lightCoords, catState, catState.yRot, catState.xRot);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            matrices.popPose();
                        }
                    }
                }


            }
//            System.out.println(playerState.entityType == EntityType.PLAYER);
//            throw new IllegalAccessException("What is calling this help");
//            playerState.ent
        }
    }

//    @Shadow
//    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius) {
//    }
//    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius) { // TODO
//    }

    @Shadow
    public abstract <T extends Entity> EntityRenderer getRenderer(T entity);

//    @Shadow
//    public abstract boolean shouldRenderHitboxes();
//    public boolean shouldRenderHitboxes() { return false; }

//    @Shadow
//    protected abstract void renderHitboxes(MatrixStack matrices, EntityRenderState state, EntityHitboxAndView hitbox, VertexConsumerProvider vertexConsumers);
//    protected void renderHitboxes(MatrixStack matrices, EntityRenderState state, EntityHitboxAndView hitbox, VertexConsumerProvider vertexConsumers) {}
}