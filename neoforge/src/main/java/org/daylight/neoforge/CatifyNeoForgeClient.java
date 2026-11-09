package org.daylight.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.daylight.neoforge.client.NeoForgeKeybinds;

@EventBusSubscriber(value = Dist.CLIENT)
public class CatifyNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        NeoForgeKeybinds.init();
    }
}
