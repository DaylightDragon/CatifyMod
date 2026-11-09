package org.daylight.catify.neoforge;

import org.daylight.catify.common.InvisibilityBehaviour;
import org.daylight.catify.common.config.ConfigHandler;
import org.daylight.catify.neoforge.replacements.config.NeoForgeBooleanConfigValue;
import org.daylight.catify.neoforge.replacements.config.NeoForgeEnumConfigValue;
import org.daylight.catify.neoforge.replacements.config.NeoForgeListConfigValue;
import org.daylight.catify.neoforge.replacements.config.NeoForgeStringConfigValue;
import org.daylight.catify.common.util.WhitelistedScreensUtil;

public class NeoForgeConfigInitialization {
    public static void init() {
        ConfigHandler.CONFIG.load();

        ConfigHandler.catVariant = new NeoForgeStringConfigValue(ConfigHandler.CONFIG, "catVariant", "JELLIE");
        ConfigHandler.catVariantVanilla = new NeoForgeBooleanConfigValue(ConfigHandler.CONFIG, "catVariantIsVanilla", true);
        ConfigHandler.replacementActive = new NeoForgeBooleanConfigValue(ConfigHandler.CONFIG, "replacementActive", true);
        ConfigHandler.catHandActive = new NeoForgeBooleanConfigValue(ConfigHandler.CONFIG, "catHandActive", true);
        ConfigHandler.catDamageVisible = new NeoForgeBooleanConfigValue(ConfigHandler.CONFIG, "catDamageVisible", true);
        ConfigHandler.invisibilityBehaviour = new NeoForgeEnumConfigValue(ConfigHandler.CONFIG, "invisibilityBehaviour", InvisibilityBehaviour.VANILLA);
        ConfigHandler.whitelistedScreenNames = new NeoForgeListConfigValue<>(ConfigHandler.CONFIG, "whitelistedAffectedScreens", null);

        if(ConfigHandler.whitelistedScreenNames.get() == null) WhitelistedScreensUtil.initDefaultWhitelistedScreens();
        else WhitelistedScreensUtil.deserializeClassNames();
    }
}
