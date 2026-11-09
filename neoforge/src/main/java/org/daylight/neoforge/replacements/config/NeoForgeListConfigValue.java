package org.daylight.neoforge.replacements.config;

import org.daylight.config.SimpleConfig;
import org.daylight.replacements.common.IListConfigValue;

import java.util.List;

public class NeoForgeListConfigValue<T> extends NeoForgeConfigValue<List<T>> implements IListConfigValue<T> {
    public NeoForgeListConfigValue(SimpleConfig config, String key, List<T> defaultValue) {
        super(config, key, defaultValue);
    }
}
