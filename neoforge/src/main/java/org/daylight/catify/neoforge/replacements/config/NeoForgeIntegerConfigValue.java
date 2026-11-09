package org.daylight.catify.neoforge.replacements.config;


import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IIntConfigValue;

public class NeoForgeIntegerConfigValue extends NeoForgeConfigValue<Integer> implements IIntConfigValue {
    public NeoForgeIntegerConfigValue(SimpleConfig config, String key, Integer defaultValue) {
        super(config, key, defaultValue);
    }
}
