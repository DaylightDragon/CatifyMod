package org.daylight.neoforge.events;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import org.daylight.Catify;
import org.daylight.ModKeybindListeners;
import org.daylight.neoforge.client.NeoForgeCommands;
import org.daylight.neoforge.client.NeoForgeOwnResourceReloadListener;
import org.daylight.util.PlayerToCatReplacer;

import java.util.concurrent.atomic.AtomicBoolean;

@EventBusSubscriber(modid = Catify.MOD_ID)
public class NeoForgeGeneralEvents {
    private static AtomicBoolean checked = new AtomicBoolean(false);

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ModKeybindListeners.KEYBIND_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Catify.MOD_ID, "main_category"));
        ModKeybindListeners.initCommonKeybinds();

        event.registerCategory(ModKeybindListeners.KEYBIND_CATEGORY);

        event.register(ModKeybindListeners.WHITELIST_SCREEN_KEY);
    }

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(NeoForgeCommands.register());
    }

    @SubscribeEvent
    public static void onPlayerLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        // Игрок вошёл в мир, сбрасываем флаг
        checked.set(false);
        PlayerToCatReplacer.initWorld();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (!checked.get() && client.player != null) {
            PlayerToCatReplacer.replaceWithCat(client.player);
            checked.set(true);
        }
    }

    @SubscribeEvent
    public static void onAddClientReloadListeners(AddClientReloadListenersEvent event) {
        // Первый параметр — уникальный Identifier для использования при сортировке/отладке
        event.addListener(
                Identifier.fromNamespaceAndPath(Catify.MOD_ID, "example_cat_texture_extractor"),
                new NeoForgeOwnResourceReloadListener()
        );
    }
}
