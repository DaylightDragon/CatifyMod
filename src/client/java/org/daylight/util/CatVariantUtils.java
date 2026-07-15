package org.daylight.util;

import java.util.Locale;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.feline.CatVariant;

public class CatVariantUtils {
    public static String serializeVariant(ResourceKey<CatVariant> key) {
        return key.identifier().getPath().toUpperCase(Locale.ROOT);
    }

    public static ResourceKey<CatVariant> deserializeVariant(String name) {
        String path = name.toLowerCase(Locale.ROOT);
        return ResourceKey.create(Registries.CAT_VARIANT, Identifier.parse(path));
    }
}
