package org.daylight.neoforge.replacements.config;

import org.daylight.config.SimpleConfig;
import org.daylight.replacements.common.IBooleanConfigValue;

public class NeoForgeBooleanConfigValue extends NeoForgeConfigValue<Boolean> implements IBooleanConfigValue {
    public NeoForgeBooleanConfigValue(SimpleConfig config, String key, Boolean defaultValue) {
        super(config, key, defaultValue);
    }
}
