package org.daylight;

import net.minecraft.client.renderer.entity.state.CatRenderState;
import org.spongepowered.asm.mixin.Shadow;

public interface IShadowHolder {
    float getShadowRadiusAccessor(CatRenderState state);

    float getShadowOpacityAccessor(CatRenderState state);
}
