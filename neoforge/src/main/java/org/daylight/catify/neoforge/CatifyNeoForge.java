package org.daylight.catify.neoforge;

import net.neoforged.fml.common.Mod;
import org.daylight.catify.common.Catify;

@Mod(Catify.MOD_ID)
public final class CatifyNeoForge {
    public CatifyNeoForge() {
        Catify.init();

        NeoForgeConfigInitialization.init();
    }
}
