package org.daylight.interfaces;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.daylight.features.CatChargeFeatureRenderer;

import java.util.function.Predicate;

public interface IFeatureManager {
    boolean catmodel$addFeature(RenderLayer<?, ?> feature);
    void renderAllFeatures(LivingEntityRenderState livingEntityRenderState, RenderLayer matrixStack, SubmitNodeCollector collector, int i, Predicate<RenderLayer<?, ?>> filter);
    CatChargeFeatureRenderer catModel$getCatChargeFeatureRenderer();
}
