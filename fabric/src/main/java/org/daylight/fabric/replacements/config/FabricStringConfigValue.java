package org.daylight.fabric.replacements.config;

import org.daylight.client.config.SimpleConfig;
import org.daylight.client.replacements.common.IStringConfigValue;

public class FabricStringConfigValue extends FabricConfigValue<String> implements IStringConfigValue {
    public FabricStringConfigValue(SimpleConfig config, String key, String defaultValue) {
        super(config, key, defaultValue);
    }
}
