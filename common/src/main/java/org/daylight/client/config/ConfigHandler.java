package org.daylight.client.config;

import org.daylight.client.replacements.common.IBooleanConfigValue;
import org.daylight.client.replacements.common.IEnumConfigValue;
import org.daylight.client.replacements.common.IListConfigValue;
import org.daylight.client.replacements.common.IStringConfigValue;

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
