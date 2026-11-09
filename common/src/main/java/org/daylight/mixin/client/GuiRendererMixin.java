package org.daylight.mixin.client;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import org.daylight.client.util.StateStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @Shadow
    @Final
    private GuiRenderState renderState;

    @Inject(
            method = "preparePictureInPicture",
            at = @At("TAIL")
    )
    private void prepareSpecialElements(CallbackInfo ci) {
        this.renderState.forEachPictureInPicture(state -> {
            if(state instanceof GuiEntityRenderState entityGuiElementRenderState) {
                if(entityGuiElementRenderState.renderState() instanceof AvatarRenderState) {
//                    System.out.println("Removing from GuiRenderer " + entityGuiElementRenderState.renderState());
                    StateStorage.currentStates.remove(entityGuiElementRenderState.renderState());
                }
            }
        });
    }
}
