package org.daylight.mixin.client;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WalkAnimationState.class)
public interface LimbAnimatorAccessor {

    @Accessor("position")
    float getAnimationProgress();

    @Accessor("position")
    void setAnimationProgress(float progress);

    @Accessor("speed")
    float getSpeed();

    @Accessor("speed")
    void setSpeed(float speed);
}

