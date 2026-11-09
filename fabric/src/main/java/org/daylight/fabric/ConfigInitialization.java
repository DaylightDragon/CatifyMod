package org.daylight.fabric;

import org.daylight.client.InvisibilityBehaviour;
import org.daylight.client.config.ConfigHandler;
import org.daylight.client.util.WhitelistedScreensUtil;
import org.daylight.fabric.replacements.config.FabricBooleanConfigValue;
import org.daylight.fabric.replacements.config.FabricEnumConfigValue;
import org.daylight.fabric.replacements.config.FabricListConfigValue;
import org.daylight.fabric.replacements.config.FabricStringConfigValue;

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
