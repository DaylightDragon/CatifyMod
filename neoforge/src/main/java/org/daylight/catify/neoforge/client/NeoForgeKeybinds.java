package org.daylight.catify.neoforge.client;

import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.daylight.catify.common.ModKeybindListeners;
import org.daylight.catify.common.util.hotkey.InputBasedHotkeyListener;

public class NeoForgeKeybinds {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(NeoForgeKeybinds::onKeyInput);
        initCustomKeybinds();
    }

    private static void initCustomKeybinds() {
        ModKeybindListeners.WHITELIST_SCREEN_KEY_LISTENER = new InputBasedHotkeyListener(
                () -> ModKeybindListeners.WHITELIST_SCREEN_KEY.getKey().getValue(),
                ModKeybindListeners::onWhitelistKeyPressed,
                ModKeybindListeners.DEFAULT_VALIDATOR
        );
    }

    private static void onKeyInput(InputEvent.Key event) {
        ModKeybindListeners.WHITELIST_SCREEN_KEY_LISTENER.onRawKeyInput(event.getAction(), event.getKey());
    }
}
