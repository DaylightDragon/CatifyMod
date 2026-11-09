package org.daylight.catify.common.interfaces;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public interface IRenderableFeature<S extends EntityRenderState> {
    void catify$render(PoseStack matrices, SubmitNodeCollector collector, int light, CatRenderState state, float limbAngle, float limbDistance);
}
