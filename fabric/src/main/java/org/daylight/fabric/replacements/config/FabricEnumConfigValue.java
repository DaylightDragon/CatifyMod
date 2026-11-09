package org.daylight.fabric.replacements.config;


import org.daylight.client.config.SimpleConfig;
import org.daylight.client.replacements.common.IEnumConfigValue;

public class FabricEnumConfigValue extends FabricConfigValue<Enum<?>> implements IEnumConfigValue {
    public FabricEnumConfigValue(SimpleConfig config, String key, Enum<?> defaultValue) {
        super(config, key, defaultValue);
    }
}
