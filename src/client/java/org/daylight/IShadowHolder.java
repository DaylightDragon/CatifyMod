package org.daylight;

import net.minecraft.client.renderer.entity.state.CatRenderState;

public interface IShadowHolder {
    float getShadowRadiusAccessor(CatRenderState state);

    float getShadowOpacityAccessor(CatRenderState state);
}
