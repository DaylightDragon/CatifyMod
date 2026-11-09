package org.daylight.fabric.replacements.config;


import org.daylight.client.config.SimpleConfig;
import org.daylight.client.replacements.common.IIntConfigValue;

public class FabricIntegerConfigValue extends FabricConfigValue<Integer> implements IIntConfigValue {
    public FabricIntegerConfigValue(SimpleConfig config, String key, Integer defaultValue) {
        super(config, key, defaultValue);
    }
}
