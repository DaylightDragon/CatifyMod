package org.daylight.util.flags;

public class TemporaryFlags {
    public static final ThreadFlag<Boolean> CURRENTLY_RENDERING_INVENTORY = new ThreadFlag<>();
    public static final ThreadFlag<Boolean> CURRENTLY_EXTRACTING_INVENTORY_PLAYER = new ThreadFlag<>();
}
