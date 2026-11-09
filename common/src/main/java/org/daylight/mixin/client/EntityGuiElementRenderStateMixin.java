package org.daylight.mixin.client;

import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import org.daylight.client.IModifiableGuiElement;
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
