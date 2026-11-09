package org.daylight;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.daylight.util.CatSkinManager;

//@Environment(EnvType.CLIENT)
public class CatifyClient {
	public static void init() {
        ModResources.init();
        CatSkinManager.init();
	}
}
