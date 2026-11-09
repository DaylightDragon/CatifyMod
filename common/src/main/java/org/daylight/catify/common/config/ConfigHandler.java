package org.daylight.catify.common.config;

import org.daylight.catify.common.replacements.common.IBooleanConfigValue;
import org.daylight.catify.common.replacements.common.IEnumConfigValue;
import org.daylight.catify.common.replacements.common.IListConfigValue;
import org.daylight.catify.common.replacements.common.IStringConfigValue;

public class ConfigHandler {
    public static final SimpleConfig CONFIG = new SimpleConfig();

    public static IStringConfigValue catVariant;
    public static IBooleanConfigValue catVariantVanilla;
    public static IBooleanConfigValue replacementActive;
    public static IBooleanConfigValue catDamageVisible;
    public static IBooleanConfigValue catHandActive;
    public static IEnumConfigValue invisibilityBehaviour;
    public static IListConfigValue<String> whitelistedScreenNames;
}
