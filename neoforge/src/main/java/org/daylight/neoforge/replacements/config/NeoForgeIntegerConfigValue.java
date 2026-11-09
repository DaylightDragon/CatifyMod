package org.daylight.neoforge.replacements.config;


import org.daylight.config.SimpleConfig;
import org.daylight.replacements.common.IIntConfigValue;

public class NeoForgeIntegerConfigValue extends NeoForgeConfigValue<Integer> implements IIntConfigValue {
    public NeoForgeIntegerConfigValue(SimpleConfig config, String key, Integer defaultValue) {
        super(config, key, defaultValue);
    }
}
