package org.daylight.mixin.client;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.state.pip.GuiEntityRenderState;
import net.minecraft.client.model.animal.feline.CatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.feline.Cat;
import org.daylight.IElementWVertexConsumerProvider;
import org.daylight.IFeatureManager;
import org.daylight.IModifiableGuiElement;
import org.daylight.ModResources;
import org.daylight.config.ConfigHandler;
import org.daylight.features.CatChargeFeatureRenderer;
import org.daylight.util.ModStateUtils;
import org.daylight.util.PlayerToCatReplacer;
import org.daylight.util.StateStorage;
import org.daylight.util.WhitelistedScreensUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEntityRenderer.class)
public class EntityGuiElementRendererMixin { // NEW
//    private static final RenderLayer GUI_ENERGY =
//            RenderLayer.of(
//                    "gui_energy",
//                    VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
//                    VertexFormat.DrawMode.QUADS,
//                    256,
//                    false,
//                    true,
//                    RenderLayer.MultiPhaseParameters.builder()
//                            .target(RenderPhase.TRANSLUCENT_TARGET)
//                            .texture(new RenderPhase.Texture(ModResources.GHOST_TEXTURE, false))
////                            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
//                            .build(true)
//            );

    @Inject(
            method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void render(GuiEntityRenderState state, PoseStack matrices, CallbackInfo ci) {
        if(Minecraft.getInstance().player == null) return;

//        System.out.println(state.renderState().getClass().getSimpleNa me());
//        System.out.println(state instanceof IModifiableGuiElement);

        if (ConfigHandler.replacementActive.getCached() && state.renderState().entityType == EntityType.PLAYER) {
            if ((Object) this instanceof IElementWVertexConsumerProvider elementWVertexes
                    && (Object) state instanceof IModifiableGuiElement modifiableGuiElement
                    && (Object) state.renderState() instanceof AvatarRenderState playerEntityRenderState) {
                MultiBufferSource vertexConsumerProvider = Minecraft.getInstance().renderBuffers().bufferSource();
//                VertexConsumerProvider vertexConsumerProvider = elementWVertexes.getVertexConsumers();

                Minecraft mc = Minecraft.getInstance();
                EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
                Cat cat = (Cat) PlayerToCatReplacer.getCatForPlayer(mc.player);

                StateStorage.inventoryRelativeHeadYaw = playerEntityRenderState.yRot;
                StateStorage.inventoryBodyYaw = playerEntityRenderState.bodyRot;
                StateStorage.inventoryPitch = playerEntityRenderState.xRot;
                StateStorage.currentlyRenderingUi = true;

                if(cat == null) return;
//                ci.cancel(); // TODO
                if(true) return;

//                mc.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.PLAYER_SKIN);

                LivingEntityRenderer<Cat, CatRenderState, CatModel> renderer = (LivingEntityRenderer<Cat, CatRenderState, CatModel>) dispatcher.getRenderer(cat);
                CatRenderState renderState = (CatRenderState) renderer.createRenderState(cat, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)); // renderer.createRenderState();
                renderer.extractRenderState(cat, renderState, 0); // mc.getRenderTickCounter().getTickProgress(true)
                CatChargeFeatureRenderer.getChargeData(cat).chargeActive = false;

                renderState.yRot = playerEntityRenderState.yRot;
                renderState.bodyRot = playerEntityRenderState.bodyRot;
                renderState.xRot = playerEntityRenderState.xRot;

                Vector3f translation = state.translation();

                if(ModStateUtils.shouldRenderCat(mc.player)) {
                    /*
                    try {
                        matrices.push();
                        matrices.translate(translation.x, translation.y, translation.z);
//                matrices.scale(1.0F, 1.0F, -1.0F);
                        matrices.multiply(state.rotation());

                        Quaternionf overrideAngle = state.overrideCameraAngle();
                        if (overrideAngle != null) {
                            dispatcher.setRotation(overrideAngle.conjugate(new Quaternionf()).rotateY((float) Math.PI));
                        }

//                    modifiableGuiElement.setRotation(quaternionf.conjugate(new Quaternionf()).rotateY((float) Math.PI));

                        PlayerToCatReplacer.syncSittingAndLimbs(mc.player, cat);

                        if(ConfigHandler.catDamageVisible.getCached()) renderState.hurt = playerEntityRenderState.hurt;
                        else renderState.hurt = false;

                        dispatcher.setRenderShadows(false);
                        renderer.render(renderState, matrices, vertexConsumerProvider, LightmapTextureManager.MAX_LIGHT_COORDINATE);
                        dispatcher.setRenderShadows(true);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    } finally {
                        matrices.pop();
                    }*/
                }

                // Charge, disabled because doesn't work at all
                if(ModStateUtils.shouldRenderCharge(mc.player) && false && renderer instanceof IFeatureManager featureManager) {
                    /*try {
                        matrices.push();

                        matrices.translate(translation.x, translation.y, translation.z);
//                matrices.scale(1.0F, 1.0F, -1.0F);
                        matrices.multiply(state.rotation());

//                        matrices.translate(x, y, z); // inherited from normal cat render
//                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
                        matrices.scale(-1.0F, -1.0F, 1.0F);
                        matrices.translate(0.0f, -1.501f, 0.0f);

                        CatChargeFeatureRenderer.getChargeData(cat).chargeActive = true;
                        CatChargeFeatureRenderer chargeFeatureRenderer = featureManager.getCatChargeFeatureRenderer();
                        float scroll = CatChargeFeatureRenderer.globalProgress;
//                        float u = (scroll * 0.01F) % 1.0F;
//                        float v = (scroll * 0.01F) % 1.0F;
                        if(chargeFeatureRenderer != null) {
//                            VertexConsumer vertexConsumer = elementWVertexes.getVertexConsumers().getBuffer(
//                                RenderLayer.getEnergySwirl(chargeFeatureRenderer.getEnergySwirlTexture(), u, v)
//                            );
//                            RenderLayer layer = RenderLayer.getEntityCutoutNoCull(chargeFeatureRenderer.getEnergySwirlTexture());
                            RenderLayer layer = RenderLayer.getEntityTranslucentEmissive(chargeFeatureRenderer.getEnergySwirlTexture());
//                            RenderLayer layer = RenderLayer.getGui(chargeFeatureRenderer.getEnergySwirlTexture());
                            VertexConsumer vc = elementWVertexes.getVertexConsumers().getBuffer(layer);

//                            VertexConsumer buf = elementWVertexes.getVertexConsumers().getBuffer(RenderLayer.getEntityCutoutNoCull(ModResources.GHOST_TEXTURE));

                            matrices.translate(0, 0, 500);
                            matrices.scale(20f, 20f, 20f); // test size to check if it's out of bounds

                            GlStateManager._disableDepthTest();
                            GlStateManager._enableBlend();
                            GlStateManager._depthFunc(515);
                            chargeFeatureRenderer.customRender(matrices, vc, LightmapTextureManager.MAX_LIGHT_COORDINATE, renderState, renderState.relativeHeadYaw, renderState.pitch);
                            elementWVertexes.getVertexConsumers().draw();
                            GlStateManager._disableBlend();
                            GlStateManager._enableDepthTest();
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }  finally {
                        matrices.pop();
                    }*/
                }

//                CatEntityModel catModel = renderer.getModel();
            }
        }
    }

    @Inject(
            method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("TAIL")
    )
    protected void renderEnd(GuiEntityRenderState state, PoseStack matrices, CallbackInfo ci) {
        StateStorage.currentlyRenderingUi = false;
    }

    @Inject(
            method = "renderToTexture(Lnet/minecraft/client/gui/render/state/pip/GuiEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("TAIL")
    )
    protected void afterRender(GuiEntityRenderState state, PoseStack matrices, CallbackInfo ci) {
        StateStorage.inventoryRelativeHeadYaw = null;
        StateStorage.inventoryBodyYaw = null;
        StateStorage.inventoryPitch = null;
    }
}
