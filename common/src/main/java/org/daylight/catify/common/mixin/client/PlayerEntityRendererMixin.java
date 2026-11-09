package org.daylight.catify.common.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.animal.feline.CatVariant;
import org.daylight.catify.common.ModResources;
import org.daylight.catify.common.config.ConfigHandler;
import org.daylight.catify.common.config.Data;
import org.daylight.catify.common.util.CatVariantUtils;
import org.daylight.catify.common.util.StateStorage;
import org.daylight.catify.common.util.flags.TemporaryFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(AvatarRenderer.class)
public abstract class PlayerEntityRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("HEAD")
    )
    public void updateRenderState(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        if(entity instanceof ClientAvatarEntity && state instanceof AvatarRenderState) {
//            System.out.println("Adding from PlayerEntityRenderer: " + state);
            if(TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.isSet() && TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.get() == true) {
//                System.out.println("SET NEW -- INVENTORY -- STATE, state " + state + ", uuid " + entity.getStringUUID() + ", entity " + entity);
                StateStorage.inventoryPlayerUUID = entity.getUUID();
            } else {
//                System.out.println("SET NEW STATE, state " + state + ", uuid " + entity.getStringUUID() + ", entity " + entity);
                StateStorage.currentStates.put(state, entity.getUUID());
            }
        }
    }

//    @ModifyArg(
//            method = "renderRightHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Z)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V", remap = false)
//    )
//    private Identifier replaceRightArmSkin(Identifier skinTexture) {
//        if (ConfigHandler.catHandActive.getCached() && ConfigHandler.replacementActive.getCached() &&
//                PlayerToCatReplacer.shouldReplace(getPlayer())) {
//            return getHandTexture(skinTexture);
//        }
//        return skinTexture;
//    }
//
//    @ModifyArg(
//            method = "renderLeftHand",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/player/AvatarRenderer;renderHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/Identifier;Lnet/minecraft/client/model/geom/ModelPart;Z)V")
//    )
//    private Identifier replaceLeftArmSkin(Identifier skinTexture) {
//        if (ConfigHandler.catHandActive.getCached() && ConfigHandler.replacementActive.getCached()
//                && PlayerToCatReplacer.shouldReplace(getPlayer())) {
//            return getHandTexture(skinTexture);
//        }
//        return skinTexture;
//    }

    @Unique
    private Identifier getHandTexture(Identifier defaultValue) {
        if(!ConfigHandler.catVariantVanilla.getCached() || Minecraft.getInstance().player == null) {
            if(Data.catHandTexture != null) return Data.catHandTexture;
            else return defaultValue;
        }
        Optional<ResourceKey<CatVariant>> optional = CatVariantUtils.deserializeVariant(Minecraft.getInstance().player.registryAccess(), ConfigHandler.catVariant.getCached()).unwrapKey();
        if(optional.isEmpty()) return defaultValue;
        return ModResources.CAT_HAND_BY_VARIANT.get(optional.get());
    }

    @Unique
    private AbstractClientPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }
}
