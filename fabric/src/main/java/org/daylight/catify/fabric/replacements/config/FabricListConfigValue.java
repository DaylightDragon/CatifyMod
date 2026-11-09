package org.daylight.catify.fabric.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IListConfigValue;

import java.util.List;

public class FabricListConfigValue<T> extends FabricConfigValue<List<T>> implements IListConfigValue<T> {
    public FabricListConfigValue(SimpleConfig config, String key, List<T> defaultValue) {
        super(config, key, defaultValue);
    }
}
