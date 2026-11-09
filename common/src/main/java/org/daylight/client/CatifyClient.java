package org.daylight.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.daylight.Catify;
import org.daylight.client.config.ConfigHandler;
import org.daylight.client.util.CatSkinManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class CatifyClient {
	public static void init() {
        ModResources.init();
        CatSkinManager.init();
	}
}
