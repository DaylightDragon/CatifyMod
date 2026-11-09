package org.daylight.client.util;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.feline.CatVariant;

import java.util.Optional;

public class CatVariantUtils {
    public static String serializeVariant(Holder<CatVariant> holder) {
        Optional<ResourceKey<CatVariant>> keyOpt = holder.unwrapKey();

        if (keyOpt.isPresent()) {
            Identifier id = keyOpt.get().identifier(); // в 26.1 это Identifier, не ResourceLocation
            String name = id.toString();              // "minecraft:jellie"
            String path = id.getPath();               // "jellie"
            String namespace = id.getNamespace();     // "minecraft"
            return path.toLowerCase(java.util.Locale.ROOT);
        }

        return null;
    }

    public static Holder<CatVariant> deserializeVariant(RegistryAccess registryAccess, String name) {
        String safeName = name.toLowerCase(java.util.Locale.ROOT)
                .replace(' ', '_');

        Identifier id = Identifier.withDefaultNamespace(safeName); // предполагается пространство имён minecraft
        ResourceKey<CatVariant> key = ResourceKey.create(Registries.CAT_VARIANT, id);

        return registryAccess
                .lookupOrThrow(Registries.CAT_VARIANT)  // получаем реестр CatVariant
                .getOrThrow(key);
    }
}
