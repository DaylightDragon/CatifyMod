package org.daylight.catify.common.mixin.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.daylight.catify.common.interfaces.IFeatureManager;
import org.daylight.catify.common.features.CatChargeFeatureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> implements IFeatureManager {
    @Shadow
    protected abstract boolean addLayer(RenderLayer<S, M> layer);

    @Shadow
    @Final
    protected List<RenderLayer<S, M>> layers;

    @Override
    public boolean catmodel$addFeature(RenderLayer<?, ?> feature) {
        return addLayer((RenderLayer<S, M>) feature);
    }

//    @Override
//    public void renderAllFeatures(LivingEntityRenderState livingEntityRenderState, PoseStack matrixStack, SubmitNodeCollector collector, int i, Predicate<FeatureRenderer<?, ?>> filter) {
//        for(RenderLayer featureRenderer : layers) {
////            if(filter.test(featureRenderer)) featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntityRenderState, ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw, ((LivingEntityRenderState)livingEntityRenderState).pitch); // TODO
//        }
//    }

    @Override
    public CatChargeFeatureRenderer catModel$getCatChargeFeatureRenderer() {
        for(RenderLayer featureRenderer : layers) {
            if(featureRenderer instanceof CatChargeFeatureRenderer) return (CatChargeFeatureRenderer) featureRenderer;
        }
        return null;
    }
}
