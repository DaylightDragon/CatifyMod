package org.daylight.catify.fabric.replacements.config;


import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IIntConfigValue;

public class FabricIntegerConfigValue extends FabricConfigValue<Integer> implements IIntConfigValue {
    public FabricIntegerConfigValue(SimpleConfig config, String key, Integer defaultValue) {
        super(config, key, defaultValue);
    }
}
