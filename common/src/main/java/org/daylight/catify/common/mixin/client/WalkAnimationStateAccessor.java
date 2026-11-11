package org.daylight.catify.common.mixin.client;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WalkAnimationState.class)
public interface WalkAnimationStateAccessor {
    @Invoker("position")
    float caitify$setPosition(float value);
}
