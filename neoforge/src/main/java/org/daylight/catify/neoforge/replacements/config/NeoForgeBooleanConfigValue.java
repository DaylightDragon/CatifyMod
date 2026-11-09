package org.daylight.catify.neoforge.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IBooleanConfigValue;

public class NeoForgeBooleanConfigValue extends NeoForgeConfigValue<Boolean> implements IBooleanConfigValue {
    public NeoForgeBooleanConfigValue(SimpleConfig config, String key, Boolean defaultValue) {
        super(config, key, defaultValue);
    }
}
