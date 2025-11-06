package org.daylight.util;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.daylight.CatifyModClient;

public class CompatabilityUtil {
    private static final String SKYHANNI_FAKE_PLAYER_CLASS = "at.hannibal2.skyhanni.utils.FakePlayer";
    private static Class<?> skyHanniFakePlayerClass = null;

    public static void init() {
        if(skyHanniFakePlayerClass == null) {
            try {
                skyHanniFakePlayerClass = Class.forName(SKYHANNI_FAKE_PLAYER_CLASS);
            } catch (Throwable ignored) {}
        }
    }

    public static boolean playerClassExcluded(AbstractClientPlayerEntity player) {
//        CatifyModClient.LOGGER.info(skyHanniFakePlayerClass.getSimpleName());
        if(skyHanniFakePlayerClass != null && skyHanniFakePlayerClass.isInstance(player)) return true;
        return false;
    }
}
