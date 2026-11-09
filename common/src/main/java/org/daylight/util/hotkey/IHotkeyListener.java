package org.daylight.util.hotkey;

public interface IHotkeyListener {
    void tick();
    void onRawKeyInput(int action, int key);
    default void resetState() {}
}
