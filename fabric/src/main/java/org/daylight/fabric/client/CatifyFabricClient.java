package org.daylight.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.client.Minecraft;
import org.daylight.client.CatifyClient;
import org.daylight.client.config.ConfigHandler;
import org.daylight.client.util.PlayerToCatReplacer;
import org.daylight.fabric.ConfigInitialization;

import java.util.concurrent.atomic.AtomicBoolean;

public final class CatifyFabricClient implements ClientModInitializer {
    AtomicBoolean checked = new AtomicBoolean(false);

    @Override
    public void onInitializeClient() {
        CatifyClient.init();

        ConfigInitialization.init();
        ModCommands.register();
        ModKeyBindings.register();
        OwnResourceReloadListener.register();

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((handler, world) -> {
            if (world != null) {
                PlayerToCatReplacer.initWorld();
                checked.set(false);
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!checked.get() && client.player != null) {
                PlayerToCatReplacer.replaceWithCat(client.player);
                checked.set(true);
            }
        });

        //ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
//		ServerWorldEvents.LOAD.register((server, world) -> {
//			LOGGER.info("World loaded, initializing fake world");
//			if (world != null) {
//				PlayerToCatReplacer.initFakeWorld();
//				if (client.player != null) {
//					PlayerToCatReplacer.replaceWithCat(client.player);
//				}
//			}
//		});

        ServerLevelEvents.UNLOAD.register((server, world) -> {
            PlayerToCatReplacer.cleanup();
        });

        // Синхронизация каждый тик
//		ClientTickEvents.START_CLIENT_TICK.register(client -> {
//			if (client.player != null) {
//				PlayerToCatReplacer.syncEntities();
//			}
//		});

//        WorldRenderEvents.START.register(context -> {
//            if(!MinecraftClient.getInstance().isPaused()) {
//                CatChargeFeatureRenderer.moveGlobalTextureForward(context.tickCounter().getTickProgress(true));
//            }
//        });
    }
}
