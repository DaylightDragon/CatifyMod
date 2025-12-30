package org.daylight.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {
    @Invoker("renderFire")
    void callRenderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, Quaternionf rotation);

    @Accessor("rotation")
    Quaternionf getRotation();
}
