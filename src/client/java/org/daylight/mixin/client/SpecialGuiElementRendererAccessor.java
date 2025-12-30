package org.daylight.mixin.client;

import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpecialGuiElementRenderer.class)
public interface SpecialGuiElementRendererAccessor {
    @Accessor("vertexConsumers")
    VertexConsumerProvider.Immediate getVertexConsumers();
}
