package org.daylight.catify.fabric.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.daylight.catify.common.Catify;
import org.daylight.catify.common.ModKeybindListeners;
import org.daylight.catify.common.util.hotkey.TickBasedHotkeyListener;

public class FabricKeybinds {
    public static void init() {
        ModKeybindListeners.KEYBIND_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(Catify.MOD_ID, "main_category"));
        ModKeybindListeners.initCommonKeybinds();

        KeyMappingHelper.registerKeyMapping(ModKeybindListeners.WHITELIST_SCREEN_KEY);

        ModKeybindListeners.KEYBIND_CATEGORY = KeyMapping.Category.register(ModKeybindListeners.KEYBIND_CATEGORY.id());


        initCustomKeybinds();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModKeybindListeners.WHITELIST_SCREEN_KEY_LISTENER.tick();
        });
    }

    private static void initCustomKeybinds() {
        ModKeybindListeners.WHITELIST_SCREEN_KEY_LISTENER = new TickBasedHotkeyListener(
                () -> KeyMappingHelper.getBoundKeyOf(ModKeybindListeners.WHITELIST_SCREEN_KEY).getValue(), // TODO CHECK IF OPTIMIZED
                ModKeybindListeners::onWhitelistKeyPressed,
                ModKeybindListeners.DEFAULT_VALIDATOR
        );
    }
}
