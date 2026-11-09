package org.daylight.catify.fabric.replacements.config;


import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IEnumConfigValue;

public class FabricEnumConfigValue extends FabricConfigValue<Enum<?>> implements IEnumConfigValue {
    public FabricEnumConfigValue(SimpleConfig config, String key, Enum<?> defaultValue) {
        super(config, key, defaultValue);
    }
}
