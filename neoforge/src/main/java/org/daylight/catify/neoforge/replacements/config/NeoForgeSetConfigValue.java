package org.daylight.catify.neoforge.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.ISetConfigValue;

import java.util.Set;

public class NeoForgeSetConfigValue<T> extends NeoForgeConfigValue<Set<T>> implements ISetConfigValue<T> {
    public NeoForgeSetConfigValue(SimpleConfig config, String key, Set<T> defaultValue) {
        super(config, key, defaultValue);
    }
}
