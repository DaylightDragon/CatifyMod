package org.daylight;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.daylight.features.CatChargeFeatureRenderer;

import java.util.function.Predicate;

public interface IFeatureManager {
    boolean catmodel$addFeature(FeatureRenderer<?, ?> feature);
    void renderAllFeatures(LivingEntityRenderState livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Predicate<FeatureRenderer<?, ?>> filter);
    CatChargeFeatureRenderer getCatChargeFeatureRenderer();
}
