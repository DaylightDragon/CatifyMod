package org.daylight.catify.fabric.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IBooleanConfigValue;

public class FabricBooleanConfigValue extends FabricConfigValue<Boolean> implements IBooleanConfigValue {
    public FabricBooleanConfigValue(SimpleConfig config, String key, Boolean defaultValue) {
        super(config, key, defaultValue);
    }
}
