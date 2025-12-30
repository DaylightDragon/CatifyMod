package org.daylight.mixin.client;

import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.daylight.IElementWVertexConsumerProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpecialGuiElementRenderer.class)
public class SpecialGuiElementRendererAccessorLegacy implements IElementWVertexConsumerProvider {
    @Shadow
    @Final
    protected VertexConsumerProvider.Immediate vertexConsumers;

    public VertexConsumerProvider.Immediate getVertexConsumers() {
        return this.vertexConsumers;
    }
}
