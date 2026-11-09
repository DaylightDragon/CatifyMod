package org.daylight.catify.neoforge.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import org.daylight.catify.common.config.ConfigHandler;
import org.daylight.catify.common.util.CatSkinManager;
import org.daylight.catify.common.util.PlayerToCatReplacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererNeoForgeMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @ModifyArg(
            method = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V")
    )
    private Identifier replaceRightArmSkin(Identifier skinTexture) {
        return determineOnReplacingHandTexture(skinTexture);
    }

    @ModifyArg(
            method = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderLeftHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V",
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
