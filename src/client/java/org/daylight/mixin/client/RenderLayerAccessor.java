package org.daylight.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.daylight.IRenderableFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderLayer.class)
public abstract class RenderLayerAccessor<S extends EntityRenderState, M extends EntityModel<? super S>> implements IRenderableFeature<S> {
    @Shadow
    public abstract void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot);

    @Override
    public void catify$render(PoseStack matrices, SubmitNodeCollector submitNodeCollector, int light, CatRenderState state, float limbAngle, float limbDistance) {
        submit(matrices, submitNodeCollector, light, (S) state, limbAngle, limbDistance);
    }
}
