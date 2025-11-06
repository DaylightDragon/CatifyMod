package org.daylight.mixin.client;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.EntityGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import org.daylight.IElementWVertexConsumerProvider;
import org.daylight.IFeatureManager;
import org.daylight.IModifiableGuiElement;
import org.daylight.ModResources;
import org.daylight.config.ConfigHandler;
import org.daylight.features.CatChargeFeatureRenderer;
import org.daylight.util.ModStateUtils;
import org.daylight.util.PlayerToCatReplacer;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(EntityGuiElementRenderer.class)
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

    private static final Pattern SKYBLOCK_WARDROBE_PATTERN = Pattern.compile("(?i)^wardrobe \\(\\d+/\\d+\\)$");

    @Inject(
            method = "Lnet/minecraft/client/gui/render/EntityGuiElementRenderer;render(Lnet/minecraft/client/gui/render/state/special/EntityGuiElementRenderState;Lnet/minecraft/client/util/math/MatrixStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void render(EntityGuiElementRenderState state, MatrixStack matrices, CallbackInfo ci) {
        if(MinecraftClient.getInstance().player == null) return;
//        System.out.println(state.getClass().getName());
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if(screen instanceof GenericContainerScreen) {
            Text title = screen.getTitle();
            String text = title.getString();
            if (SKYBLOCK_WARDROBE_PATTERN.matcher(text).matches()) {
                return;
            }
        }

//        System.out.println(state.renderState().getClass().getSimpleNa me());
//        System.out.println(state instanceof IModifiableGuiElement);

        if (ConfigHandler.replacementActive.getCached())
            if (state.renderState().entityType == EntityType.PLAYER) {
                if ((Object) this instanceof IElementWVertexConsumerProvider elementWVertexes
                        && (Object) state instanceof IModifiableGuiElement modifiableGuiElement
                        && (Object) state.renderState() instanceof PlayerEntityRenderState playerEntityRenderState) {
                    VertexConsumerProvider vertexConsumerProvider = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
//                VertexConsumerProvider vertexConsumerProvider = elementWVertexes.getVertexConsumers();

                    MinecraftClient mc = MinecraftClient.getInstance();
                    EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
                    CatEntity cat = (CatEntity) PlayerToCatReplacer.getCatForPlayer(mc.player);

                    if(cat == null) return;
                    ci.cancel();

                    mc.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.PLAYER_SKIN);

                    LivingEntityRenderer<CatEntity, CatEntityRenderState, CatEntityModel> renderer = (LivingEntityRenderer<CatEntity, CatEntityRenderState, CatEntityModel>) dispatcher.getRenderer(cat);
                    CatEntityRenderState renderState = renderer.createRenderState();
                    renderer.updateRenderState(cat, renderState, 0); // mc.getRenderTickCounter().getTickProgress(true)
                    CatChargeFeatureRenderer.getChargeData(cat).chargeActive = false;

                    renderState.relativeHeadYaw = playerEntityRenderState.relativeHeadYaw;
                    renderState.bodyYaw = playerEntityRenderState.bodyYaw;
                    renderState.pitch = playerEntityRenderState.pitch;

                    Vector3f translation = state.translation();

                    if(ModStateUtils.shouldRenderCat(mc.player)) {
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
                        }
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
}
