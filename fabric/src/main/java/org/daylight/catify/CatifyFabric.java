package org.daylight.catify.fabric;

import net.fabricmc.api.ModInitializer;
import org.daylight.catify.common.Catify;

public final class CatifyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Catify.init();
    }
}
