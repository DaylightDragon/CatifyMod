package org.daylight.neoforge.replacements.config;


import org.daylight.config.SimpleConfig;
import org.daylight.replacements.common.IEnumConfigValue;

public class NeoForgeEnumConfigValue extends NeoForgeConfigValue<Enum<?>> implements IEnumConfigValue {
    public NeoForgeEnumConfigValue(SimpleConfig config, String key, Enum<?> defaultValue) {
        super(config, key, defaultValue);
    }
}
