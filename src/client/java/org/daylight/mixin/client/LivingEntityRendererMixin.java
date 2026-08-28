package org.daylight.mixin.client;

//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.entity.LivingEntityRenderer;
//import net.minecraft.client.render.entity.feature.FeatureRenderer;
//import net.minecraft.client.render.entity.model.EntityModel;
//import net.minecraft.client.render.entity.state.LivingEntityRenderState;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.entity.LivingEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.daylight.IFeatureManager;
import org.daylight.features.CatChargeFeatureRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Predicate;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> implements IFeatureManager {
    @Shadow
    protected abstract boolean addFeature(FeatureRenderer<S, M> feature);

    @Shadow
    @Final
    protected List<FeatureRenderer<S, M>> features;

    @Override
    public boolean catmodel$addFeature(FeatureRenderer<?, ?> feature) {
        return addFeature((FeatureRenderer<S, M>) feature);
    }

    @Override
    public void renderAllFeatures(LivingEntityRenderState livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Predicate<FeatureRenderer<?, ?>> filter) {
        for(FeatureRenderer featureRenderer : features) {
//            if(filter.test(featureRenderer)) featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntityRenderState, ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw, ((LivingEntityRenderState)livingEntityRenderState).pitch); // TODO
        }
    }

    @Override
    public CatChargeFeatureRenderer getCatChargeFeatureRenderer() {
        for(FeatureRenderer featureRenderer : features) {
            if(featureRenderer instanceof CatChargeFeatureRenderer) return (CatChargeFeatureRenderer) featureRenderer;
        }
        return null;
    }
}
