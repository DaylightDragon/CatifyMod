package org.daylight.fabric.replacements.config;

import org.daylight.client.config.SimpleConfig;
import org.daylight.client.replacements.common.IListConfigValue;

import java.util.List;

public class FabricListConfigValue<T> extends FabricConfigValue<List<T>> implements IListConfigValue<T> {
    public FabricListConfigValue(SimpleConfig config, String key, List<T> defaultValue) {
        super(config, key, defaultValue);
    }
}
