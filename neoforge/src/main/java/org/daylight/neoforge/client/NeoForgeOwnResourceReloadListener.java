package org.daylight.neoforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.daylight.Catify;
import org.daylight.ModResources;
import org.daylight.util.CatSkinManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class NeoForgeOwnResourceReloadListener extends SimplePreparableReloadListener<Void> {
    // prepare можно оставить пустым, вся работа выполняется в apply
    @Override
    protected Void prepare(ResourceManager manager, ProfilerFiller profiler) {
        return null;
    }

    @Override
    protected void apply(Void unused, ResourceManager manager, ProfilerFiller profiler) {
        try {
            extractCatTextures(manager);
            extractHandTextures(manager);
        } catch (Throwable t) {
            Catify.LOGGER.error("Failed to reload texture extractor, report this to the mod developer", t);
        }
    }

    // --- Следующие методы (extractCatTextures, extractHandTextures, createOutputDir, copyResource, extractBaseName)
    //     полностью скопированы из оригинального кода, без изменений ---

    private void extractCatTextures(ResourceManager manager) {
        Path outputDir = createOutputDir("example_cats");
        for (String name : CatSkinManager.STATIC_VARIANTS) {
            Identifier id = Identifier.withDefaultNamespace("textures/entity/cat/cat_" + name + ".png");
            copyResource(manager, id, outputDir, name);
        }
    }

    private void extractHandTextures(ResourceManager manager) {
        Path outputDir = createOutputDir("example_hands");
        for (Map.Entry<?, Identifier> entry : ModResources.CAT_HAND_BY_VARIANT.entrySet()) {
            Identifier textureId = entry.getValue();
            String name = extractBaseName(textureId);
            copyResource(manager, textureId, outputDir, name);
        }
    }

    private Path createOutputDir(String folderName) {
        Path outputDir = Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config/catify/example_skins/" + folderName);
        try {
            Files.createDirectories(outputDir);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create output folder: " + outputDir, e);
        }
        return outputDir;
    }

    private void copyResource(ResourceManager manager, Identifier resourceId, Path outputDir, String fileName) {
        try {
            Resource resource = manager.getResource(resourceId).orElse(null);
            if (resource == null) {
                Catify.LOGGER.error("Not found: {}", resourceId);
                return;
            }
            Path outFile = outputDir.resolve(fileName + ".png");
            try (InputStream input = resource.open()) {
                Files.copy(input, outFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            Catify.LOGGER.error("Error extracting {}: {} ", resourceId, e);
        }
    }

    private String extractBaseName(Identifier id) {
        String path = id.getPath();
        int lastSlash = path.lastIndexOf('/');
        String file = (lastSlash >= 0 ? path.substring(lastSlash + 1) : path);
        return file.replace("_hand.png", "");
    }
}
