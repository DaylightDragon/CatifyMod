package org.daylight.catify.neoforge.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.IListConfigValue;

import java.util.List;

public class NeoForgeListConfigValue<T> extends NeoForgeConfigValue<List<T>> implements IListConfigValue<T> {
    public NeoForgeListConfigValue(SimpleConfig config, String key, List<T> defaultValue) {
        super(config, key, defaultValue);
    }
}
