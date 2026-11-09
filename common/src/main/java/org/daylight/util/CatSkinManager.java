package org.daylight.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.feline.CatVariant;
import org.daylight.ModResources;
import org.daylight.config.ConfigHandler;
import org.daylight.config.Data;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class CatSkinManager {
    public static final List<String> STATIC_VARIANTS  = List.of(
            "tabby", "black", "red", "siamese",
            "british_shorthair", "calico", "persian",
            "ragdoll", "white", "jellie", "all_black"
    );
    public static final CopyOnWriteArrayList<String> DYNAMIC_VARIANTS = new CopyOnWriteArrayList<>();
    public static final CopyOnWriteArrayList<String> ALL_VARIANTS = new CopyOnWriteArrayList<>();

    public static void startWatcher() {
        String basePath = Minecraft.getInstance().gameDirectory.getAbsolutePath() + "/config/catify/cat_enitity_skins";
        Path dir = Paths.get(basePath);

        // Mkdirs
        dir.toFile().mkdirs();
        new File(Minecraft.getInstance().gameDirectory.getAbsolutePath() + "/config/catify/cat_hand_skins").mkdirs();

        Thread watcherThread = new Thread(new CustomSkinWatcher(dir, DYNAMIC_VARIANTS) {
            @Override
            public void updateAllVariants() {
                CatSkinManager.updateAllVariants();
            }
        }, "CatEntitySkinWatcher");
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    public static void updateAllVariants() {
        ALL_VARIANTS.clear();
        ALL_VARIANTS.addAll(STATIC_VARIANTS);
        for(String variant : DYNAMIC_VARIANTS) {
            String variantFinal = variant;
            if(ALL_VARIANTS.contains(variantFinal)) variantFinal = "custom_" +  variantFinal;
            if(!ALL_VARIANTS.contains(variantFinal)) ALL_VARIANTS.add(variantFinal);
        }
    }

    public static boolean isVanillaVariant(String variant) {
        return STATIC_VARIANTS.contains(variant);
    }

    public static String getActualCustomFileName(String variant) {
        if(DYNAMIC_VARIANTS.contains(variant)) return variant;
        while(variant.startsWith("custom_")) {
            String newStr = variant.substring("custom_".length());
            if(DYNAMIC_VARIANTS.contains(newStr)) return newStr;
            variant = newStr;
        }
        return null;
    }

    public static void init() {
        startWatcher();
    }

    public static void setupCustomSkin() {
        if(!ConfigHandler.catVariantVanilla.get() && Minecraft.getInstance().player != null) { // TODO idk I added non null check but when is it canned
            if(!PlayerToCatReplacer.setCustomCatTexture(Minecraft.getInstance().player, ConfigHandler.catVariant.get())) {
                ConfigHandler.catVariant.set("JELLIE");
                ConfigHandler.catVariantVanilla.set(true);
                PlayerToCatReplacer.setLocalCatVariant(CatVariantUtils.deserializeVariant(Minecraft.getInstance().player.registryAccess(), ConfigHandler.catVariant.getCached()));
            } else {
                PlayerToCatReplacer.setCustomCatHandTexture(ConfigHandler.catVariant.get());
            }
        }
    }

    public static Identifier getHandTexture(Identifier defaultValue) {
        if(!ConfigHandler.catVariantVanilla.getCached() || Minecraft.getInstance().player == null) {
            if(Data.catHandTexture != null) return Data.catHandTexture;
            else return defaultValue;
        }
        Optional<ResourceKey<CatVariant>> optional = CatVariantUtils.deserializeVariant(Minecraft.getInstance().player.registryAccess(), ConfigHandler.catVariant.getCached()).unwrapKey();
        if(optional.isEmpty()) return defaultValue;
        return ModResources.CAT_HAND_BY_VARIANT.get(optional.get());
    }
}
