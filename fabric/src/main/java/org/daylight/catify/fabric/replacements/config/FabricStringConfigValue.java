package org.daylight.catify.fabric.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IStringConfigValue;

public class FabricStringConfigValue extends FabricConfigValue<String> implements IStringConfigValue {
    public FabricStringConfigValue(SimpleConfig config, String key, String defaultValue) {
        super(config, key, defaultValue);
    }
}
