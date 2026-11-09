package org.daylight.catify.fabric.replacements.config;

import org.daylight.catify.common.config.SimpleConfig;
import org.daylight.catify.common.replacements.common.ISetConfigValue;

import java.util.Set;

public class FabricSetConfigValue<T> extends FabricConfigValue<Set<T>> implements ISetConfigValue<T> {
    public FabricSetConfigValue(SimpleConfig config, String key, Set<T> defaultValue) {
        super(config, key, defaultValue);
    }
}
