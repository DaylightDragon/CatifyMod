package org.daylight.catify.neoforge.replacements.config;


import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IEnumConfigValue;

public class NeoForgeEnumConfigValue extends NeoForgeConfigValue<Enum<?>> implements IEnumConfigValue {
    public NeoForgeEnumConfigValue(SimpleConfig config, String key, Enum<?> defaultValue) {
        super(config, key, defaultValue);
    }
}
