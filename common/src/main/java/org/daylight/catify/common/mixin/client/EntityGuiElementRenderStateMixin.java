package org.daylight.catify.common.mixin.client;

import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import org.daylight.catify.common.interfaces.IModifiableGuiElement;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiEntityRenderState.class)
public class EntityGuiElementRenderStateMixin implements IModifiableGuiElement {
    @Mutable
    @Final
    @Shadow
    private Quaternionf rotation;

    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
    }
}
