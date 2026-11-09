package org.daylight.interfaces;

import net.minecraft.client.renderer.entity.state.CatRenderState;

public interface IShadowHolder {
    float getShadowRadiusAccessor(CatRenderState state);

    float getShadowOpacityAccessor(CatRenderState state);
}
