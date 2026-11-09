package org.daylight.catify.common;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.daylight.catify.common.util.WhitelistedScreensUtil;
import org.daylight.catify.common.util.hotkey.IHotkeyListener;

import java.util.function.Supplier;

public class ModKeybindListeners {
    public static final Supplier<Boolean> DEFAULT_VALIDATOR = () -> Minecraft.getInstance().screen != null;

    public static KeyMapping.Category KEYBIND_CATEGORY;

    public static KeyMapping WHITELIST_SCREEN_KEY;

    public static void initCommonKeybinds() {
        WHITELIST_SCREEN_KEY = new KeyMapping(
                "key." + Catify.MOD_ID + ".change_screen_whitelist_state",
                InputConstants.UNKNOWN.getValue(),
                KEYBIND_CATEGORY
        );
    }

    public static IHotkeyListener WHITELIST_SCREEN_KEY_LISTENER;

    public static void onWhitelistKeyPressed() {
        WhitelistedScreensUtil.toggleScreen(Minecraft.getInstance().screen);
    }
}
