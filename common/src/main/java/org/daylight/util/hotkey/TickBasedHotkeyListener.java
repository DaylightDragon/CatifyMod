package org.daylight.util.hotkey;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class TickBasedHotkeyListener implements IHotkeyListener {
    private final IntSupplier keyCodeGetter;
    private final Runnable onPressAction;
    private final Supplier<Boolean> contextValidator;

    private boolean wasDownLastTick = false;

    public TickBasedHotkeyListener(IntSupplier keyCodeGetter, Runnable onPressAction) {
        this(keyCodeGetter, onPressAction, () -> true);
    }

    public TickBasedHotkeyListener(IntSupplier keyCodeGetter, Runnable onPressAction, Supplier<Boolean> contextValidator) {
        this.keyCodeGetter = Objects.requireNonNull(keyCodeGetter);
        this.onPressAction = Objects.requireNonNull(onPressAction);
        this.contextValidator = Objects.requireNonNull(contextValidator);
    }

    @Override
    public void tick() {
        // Проверяем контекст
        if (!contextValidator.get()) {
            wasDownLastTick = false;
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client == null || client.getWindow() == null) {
            wasDownLastTick = false;
            return;
        }

        // Получаем текущий код клавиши
        int currentKey = keyCodeGetter.getAsInt();
        if (currentKey <= 0) {
            wasDownLastTick = false;
            return;
        }

        // Проверяем, зажата ли клавиша прямо сейчас
        boolean isCurrentlyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), currentKey);

        // Логика Rising Edge (клавиша была отпущена, а теперь нажата)
        if (isCurrentlyDown && !wasDownLastTick) {
            onPressAction.run();
        }

        // Запоминаем состояние для следующего тика
        wasDownLastTick = isCurrentlyDown;
    }

    @Override
    public void onRawKeyInput(int action, int key) {
        // Не используется, т.к. мы работаем по тикам
    }

    @Override
    public void resetState() {
        wasDownLastTick = false;
    }
}
