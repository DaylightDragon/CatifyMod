package org.daylight;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.daylight.util.WhitelistedScreensUtil;

public class ModKeyBindings {
    public static KeyBinding CHANGE_SCREEN_WHITELIST_STATE;

    private static boolean prevWhitelistDown = false;

    public static void register() {
        CHANGE_SCREEN_WHITELIST_STATE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + CatifyModClient.MOD_ID + ".change_screen_whitelist_state",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                "key.categories." + CatifyModClient.MOD_ID
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client == null || client.getWindow() == null) return;
            long window = client.getWindow().getHandle();

            InputUtil.Key whitelistScreenKey = KeyBindingHelper.getBoundKeyOf(CHANGE_SCREEN_WHITELIST_STATE);
            if (whitelistScreenKey.getCategory() == InputUtil.Type.KEYSYM) {
                if(whitelistScreenKey.getCode() != -1) {
                    boolean down = InputUtil.isKeyPressed(window, whitelistScreenKey.getCode());

                    if (down && !prevWhitelistDown) {
                        WhitelistedScreensUtil.toggleScreen(MinecraftClient.getInstance().currentScreen);
                    }
                    prevWhitelistDown = down;
                }
            }
        });
    }
}
