package org.daylight.catify.neoforge.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IStringConfigValue;

public class NeoForgeStringConfigValue extends NeoForgeConfigValue<String> implements IStringConfigValue {
    public NeoForgeStringConfigValue(SimpleConfig config, String key, String defaultValue) {
        super(config, key, defaultValue);
    }
}
