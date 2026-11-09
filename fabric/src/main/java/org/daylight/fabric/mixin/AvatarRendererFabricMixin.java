package org.daylight.fabric.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.animal.feline.CatVariant;
import org.daylight.ModResources;
import org.daylight.config.ConfigHandler;
import org.daylight.config.Data;
import org.daylight.util.CatSkinManager;
import org.daylight.util.CatVariantUtils;
import org.daylight.util.PlayerToCatReplacer;
import org.daylight.util.StateStorage;
import org.daylight.util.flags.TemporaryFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererFabricMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @ModifyArg(
            method = "renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V", remap = false)
    )
    private Identifier replaceRightArmSkin(Identifier skinTexture) {
        return determineOnReplacingHandTexture(skinTexture);
    }

    @ModifyArg(
            method = "renderLeftHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V")
    )
    private Identifier replaceLeftArmSkin(Identifier skinTexture) {
        return determineOnReplacingHandTexture(skinTexture);
    }

    @Unique
    private Identifier determineOnReplacingHandTexture(Identifier original) {
        if (ConfigHandler.catHandActive.getCached() && ConfigHandler.replacementActive.getCached()
                && PlayerToCatReplacer.shouldReplace(getPlayer())) {
            return CatSkinManager.getHandTexture(original);
        }
        return original;
    }


    @Unique
    private AbstractClientPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }
}
