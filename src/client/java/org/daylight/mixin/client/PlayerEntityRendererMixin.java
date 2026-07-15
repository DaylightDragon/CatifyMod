package org.daylight.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import org.daylight.ModResources;
import org.daylight.config.ConfigHandler;
import org.daylight.config.Data;
import org.daylight.util.CatVariantUtils;
import org.daylight.util.PlayerToCatReplacer;
import org.daylight.util.StateStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("HEAD")
    )
    public void updateRenderState(AvatarlikeEntity playerLikeEntity, AvatarRenderState state, float f, CallbackInfo ci) {
        if(playerLikeEntity instanceof LocalPlayer clientPlayerEntity && state instanceof AvatarRenderState) {
//            System.out.println("Adding from PlayerEntityRenderer: " + state);
            StateStorage.currentStates.put(state, clientPlayerEntity.getUUID());
        }
    }

    @ModifyArg(
            method = "renderRightHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V")
    )
    private Identifier replaceRightArmSkin(Identifier skinTexture) {
        if (ConfigHandler.catHandActive.getCached() && ConfigHandler.replacementActive.getCached() &&
                PlayerToCatReplacer.shouldReplace(getPlayer())) {
            return getHandTexture(skinTexture);
        }
        return skinTexture;
    }

    @ModifyArg(
            method = "renderLeftHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V")
    )
    private Identifier replaceLeftArmSkin(Identifier skinTexture) {
        if (ConfigHandler.catHandActive.getCached() && ConfigHandler.replacementActive.getCached()
                && PlayerToCatReplacer.shouldReplace(getPlayer())) {
            return getHandTexture(skinTexture);
        }
        return skinTexture;
    }

    @Unique
    private Identifier getHandTexture(Identifier defaultValue) {
        if(!ConfigHandler.catVariantVanilla.getCached()) {
            if(Data.catHandTexture != null) return Data.catHandTexture;
            else return defaultValue;
        }
        return ModResources.CAT_HAND_BY_VARIANT.get(CatVariantUtils.deserializeVariant(ConfigHandler.catVariant.getCached()));
    }

    @Unique
    private AbstractClientPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }
}
