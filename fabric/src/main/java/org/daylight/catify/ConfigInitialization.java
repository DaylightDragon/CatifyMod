package org.daylight.catify.fabric;

import org.daylight.catify.common.InvisibilityBehaviour;
import org.daylight.catify.common.config.ConfigHandler;
import org.daylight.catify.common.util.WhitelistedScreensUtil;
import org.daylight.catify.fabric.replacements.config.FabricBooleanConfigValue;
import org.daylight.catify.fabric.replacements.config.FabricEnumConfigValue;
import org.daylight.catify.fabric.replacements.config.FabricListConfigValue;
import org.daylight.catify.fabric.replacements.config.FabricStringConfigValue;

public class ConfigInitialization {
    public static void init() {
        ConfigHandler.CONFIG.load();

        ConfigHandler.catVariant = new FabricStringConfigValue(ConfigHandler.CONFIG, "catVariant", "JELLIE");
        ConfigHandler.catVariantVanilla = new FabricBooleanConfigValue(ConfigHandler.CONFIG, "catVariantIsVanilla", true);
        ConfigHandler.replacementActive = new FabricBooleanConfigValue(ConfigHandler.CONFIG, "replacementActive", true);
        ConfigHandler.catHandActive = new FabricBooleanConfigValue(ConfigHandler.CONFIG, "catHandActive", true);
        ConfigHandler.catDamageVisible = new FabricBooleanConfigValue(ConfigHandler.CONFIG, "catDamageVisible", true);
        ConfigHandler.invisibilityBehaviour = new FabricEnumConfigValue(ConfigHandler.CONFIG, "invisibilityBehaviour", InvisibilityBehaviour.VANILLA);
        ConfigHandler.whitelistedScreenNames = new FabricListConfigValue<>(ConfigHandler.CONFIG, "whitelistedAffectedScreens", null);

        if(ConfigHandler.whitelistedScreenNames.get() == null) WhitelistedScreensUtil.initDefaultWhitelistedScreens();
        else WhitelistedScreensUtil.deserializeClassNames();
    }
}
