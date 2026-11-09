package org.daylight.fabric;

import net.fabricmc.api.ModInitializer;
import org.daylight.Catify;

public final class CatifyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Catify.init();
    }
}
