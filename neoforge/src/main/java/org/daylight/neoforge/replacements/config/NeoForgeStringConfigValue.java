package org.daylight.neoforge.replacements.config;

import org.daylight.config.SimpleConfig;
import org.daylight.replacements.common.IStringConfigValue;

public class NeoForgeStringConfigValue extends NeoForgeConfigValue<String> implements IStringConfigValue {
    public NeoForgeStringConfigValue(SimpleConfig config, String key, String defaultValue) {
        super(config, key, defaultValue);
    }
}
