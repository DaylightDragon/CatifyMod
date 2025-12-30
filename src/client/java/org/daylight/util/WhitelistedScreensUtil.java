package org.daylight.util;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.daylight.config.ConfigHandler;

import java.util.ArrayList;
import java.util.Optional;

public class WhitelistedScreensUtil {
    public static void initDefaultWhitelistedScreens() {
        ConfigHandler.whitelistedScreens.set(new ArrayList<>());
        ConfigHandler.whitelistedScreens.get().add(InventoryScreen.class);
        ConfigHandler.whitelistedScreens.get().add(CreativeInventoryScreen.class);

        Optional<Class<? extends Screen>> screen = findScreenClass("de.hysky.skyblocker.skyblock.item.SkyblockInventoryScreen");
        screen.ifPresent(aClass -> ConfigHandler.whitelistedScreens.get().add(aClass));
    }

    public static Optional<Class<? extends Screen>> findScreenClass(String name) {
        try {
            return Optional.of(
                    Class.forName(name).asSubclass(Screen.class)
            );
        } catch (ClassNotFoundException | ClassCastException e) {
            return Optional.empty();
        }
    }

    public static void whitelistScreen(Screen screen) {
        ConfigHandler.whitelistedScreens.get().add(screen.getClass());
        ConfigHandler.CONFIG.save();
    }

    public static void unwhitelistScreen(Screen screen) {
        ConfigHandler.whitelistedScreens.get().remove(screen.getClass());
        ConfigHandler.CONFIG.save();
    }

    public static boolean isWhitelisted(Screen screen) {
        return ConfigHandler.whitelistedScreens.getCached().contains(screen.getClass());
    }

    public static void toggleScreen(Screen screen) {
        if(screen == null) return;
        if(isWhitelisted(screen)) unwhitelistScreen(screen);
        else whitelistScreen(screen);
    }
}
