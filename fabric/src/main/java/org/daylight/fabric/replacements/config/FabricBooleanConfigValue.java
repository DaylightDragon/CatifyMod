package org.daylight.fabric.replacements.config;

import org.daylight.client.config.SimpleConfig;
import org.daylight.client.replacements.common.IBooleanConfigValue;

public class FabricBooleanConfigValue extends FabricConfigValue<Boolean> implements IBooleanConfigValue {
    public FabricBooleanConfigValue(SimpleConfig config, String key, Boolean defaultValue) {
        super(config, key, defaultValue);
    }
}
