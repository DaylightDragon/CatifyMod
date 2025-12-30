package org.daylight.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.CatEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldView;
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

import java.util.UUID;

@Mixin(EntityRenderManager.class)
public abstract class EntityRenderManagerMixin {
//    @Shadow
//    private boolean renderShadows;
//    private boolean renderShadows() { return true; } // TODO

    @Shadow
    public abstract <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S state);

    @Shadow
    @Final
    public GameOptions gameOptions;

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
            method = "Lnet/minecraft/client/render/entity/EntityRenderManager;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/render/state/CameraRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private <S extends EntityRenderState> void render(S renderState, CameraRenderState cameraRenderState, double x, double y, double z, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, CallbackInfo ci) throws IllegalAccessException {
        if(!ConfigHandler.replacementActive.getCached()) return;

        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(StateStorage.currentlyRenderingUi && screen != null && !WhitelistedScreensUtil.isWhitelisted(screen)) return;

        if(renderState instanceof PlayerEntityRenderState playerState) {
            if(StateStorage.currentStates.containsKey(playerState)) {
                UUID playerUuid = StateStorage.currentStates.get(playerState);
                if(playerUuid == null) return;
                PlayerEntity player = PlayerToCatReplacer.getPlayerById(playerUuid);
                // StateStorage.currentStates.remove(playerState);
                if(player == null) return;
                CatEntity cat = (CatEntity) PlayerToCatReplacer.getCatForPlayer(player);
                if(cat == null) return;

                CatEntityRenderer catRenderer;
                CatEntityRenderState catState;
                EntityRenderer<?, ? super S> originalRenderer = this.getRenderer(renderState);
                float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);

                ci.cancel();
                PlayerToCatReplacer.syncEntity2(player, cat);

                catRenderer = (CatEntityRenderer) this.getRenderer(cat);
                catState = catRenderer.getAndUpdateRenderState(cat, tickDelta);

                if(playerState == null) playerState = (PlayerEntityRenderState) getRenderer(player).getAndUpdateRenderState(player, tickDelta);
                if(ConfigHandler.catDamageVisible.getCached()) catState.hurt = playerState.hurt;
                else catState.hurt = false;

                catState.light = playerState.light;

                if(StateStorage.inventoryRelativeHeadYaw != null) catState.relativeHeadYaw = StateStorage.inventoryRelativeHeadYaw;
                if(StateStorage.inventoryBodyYaw != null) catState.bodyYaw = StateStorage.inventoryBodyYaw;
                if(StateStorage.inventoryPitch != null) catState.pitch = StateStorage.inventoryPitch;

                CatChargeFeatureRenderer.getChargeData(cat).chargeActive = false;

                try {
                    matrices.push();
                    Vec3d vec3d = originalRenderer.getPositionOffset(renderState);
                    matrices.translate(x, y, z);
//                    matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
                    if(catRenderer instanceof CustomCatTextureHolder customCatTextureHolder) {
                        if(customCatTextureHolder.catModel$shouldUpdateCustomTexture()) {
//                            CatifyModClient.LOGGER.info("RenderDispatcher updates CatRenderer state");
                            catRenderer.getAndUpdateRenderState(cat, 0);
                        }
                    }
                    ci.cancel();


//                    if(catState instanceof CustomCatState customCatState) customCatState.catmodel$setChargeActive(false);

                    if(ModStateUtils.shouldRenderCat(player)) {
                        // Just cat
                        catRenderer.render(catState, matrices, orderedRenderCommandQueue, cameraRenderState);
                    }

                    if (renderState.onFire) {
                        orderedRenderCommandQueue.submitFire(matrices, renderState, MathHelper.rotateAround(MathHelper.Y_AXIS, cameraRenderState.orientation, new Quaternionf()));
                    }

                    if (!renderState.shadowPieces.isEmpty()) {
                        orderedRenderCommandQueue.submitShadowPieces(matrices, renderState.shadowRadius, renderState.shadowPieces);
                    }

//                    matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());

//                    if (renderState.hitbox != null) {
//                        orderedRenderCommandQueue.submitDebugHitbox(matrices, renderState, renderState.hitbox);
//                    } // TODO 1.21.11 broken
                } catch (ClassCastException e) {
                    CatifyModClient.LOGGER.error("The renderer is most likely not a EntityRenderer<CatEntity, EntityRenderState>", e);
                } finally {
                    matrices.pop();
                }

                if(ModStateUtils.shouldRenderCharge(player)) {
                    if(catRenderer instanceof IFeatureManager featureManager) {
                        if(catState == null) catState = (CatEntityRenderState) catRenderer.getAndUpdateRenderState(cat, tickDelta);

                        matrices.push();

                        try {
                            float bodyYaw = catState.bodyYaw; // or entity.getBodyYaw(tickDelta)
                            matrices.translate(x, y, z); // inherited from normal cat render
                            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
                            matrices.scale(-1.0F, -1.0F, 1.0F);
                            matrices.translate(0.0f, -1.501f, 0.0f);

                            CatChargeFeatureRenderer.getChargeData(cat).chargeActive = true;

                            featureManager.getCatChargeFeatureRenderer().customRender(matrices, orderedRenderCommandQueue, catState.light, catState, catState.relativeHeadYaw, catState.pitch);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            matrices.pop();
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