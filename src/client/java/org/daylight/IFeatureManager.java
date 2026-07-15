package org.daylight;

import org.daylight.features.CatChargeFeatureRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Predicate;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public interface IFeatureManager {
    boolean catmodel$addFeature(RenderLayer<?, ?> feature);
    void renderAllFeatures(LivingEntityRenderState livingEntityRenderState, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, Predicate<RenderLayer<?, ?>> filter);
    CatChargeFeatureRenderer getCatChargeFeatureRenderer();
}
