package org.daylight.catify.common;

import org.daylight.catify.common.util.CatSkinManager;

//@Environment(EnvType.CLIENT)
public class CatifyClient {
	public static void init() {
        ModResources.init();
        CatSkinManager.init();
	}
}
