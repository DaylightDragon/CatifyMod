package org.daylight.neoforge;

import net.neoforged.fml.common.Mod;
import org.daylight.Catify;
import org.daylight.neoforge.client.NeoForgeKeybinds;

@Mod(Catify.MOD_ID)
public final class CatifyNeoForge {
    public CatifyNeoForge() {
        Catify.init();

        NeoForgeConfigInitialization.init();
        NeoForgeKeybinds.init();
    }
}
