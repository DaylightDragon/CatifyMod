package org.daylight.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.WorldView;
import org.daylight.*;
import org.daylight.config.ConfigHandler;
import org.daylight.config.Data;
import org.daylight.features.CatChargeFeatureRenderer;
import org.daylight.util.PlayerToCatReplacer;
import org.daylight.util.WhitelistedScreensUtil;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    private Quaternionf rotation;

    @Shadow
    protected abstract void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, Quaternionf rotation);

    @Shadow
    private boolean renderShadows;
    @Shadow
    @Final
    public GameOptions gameOptions;

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
        if(MinecraftClient.getInstance().player == null) return;

        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(screen != null && Data.currentlyRenderingInventory && !WhitelistedScreensUtil.isWhitelisted(screen)) return;

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
                            CatifyModClient.LOGGER.info("RenderDispatcher updates CatRenderer state");
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

                    if((visible || behaviour == InvisibilityBehaviour.NEVER)) {
                        // Just cat
                        catRenderer.render(catState, matrices, vertexConsumers, light);
                    }

                    if(playerState.onFire) renderFire(matrices, vertexConsumers, playerState, MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf()));
                } catch (ClassCastException e) {
                    CatifyModClient.LOGGER.error("The renderer is most likely not a EntityRenderer<CatEntity, EntityRenderState>", e);
                } finally {
                    matrices.pop();
                }

                // Shadow, after the cat render

                if((visible || behaviour == InvisibilityBehaviour.NEVER || behaviour == InvisibilityBehaviour.CHARGED)) {
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

                if(!visible && behaviour == InvisibilityBehaviour.CHARGED) {
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

                            featureManager.renderAllFeatures(catState, matrices, vertexConsumers, light, featureRenderer -> featureRenderer instanceof CatChargeFeatureRenderer);
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
    }

    @Shadow
    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius) {
    }

    @Shadow
    public abstract <T extends Entity> EntityRenderer getRenderer(T entity);

    @Shadow
    public abstract boolean shouldRenderHitboxes();

    @Shadow
    protected abstract void renderHitboxes(MatrixStack matrices, EntityRenderState state, EntityHitboxAndView hitbox, VertexConsumerProvider vertexConsumers);
}