package org.daylight.util.hotkey;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class InputBasedHotkeyListener implements IHotkeyListener {
    private final IntSupplier keyCodeGetter;
    private final Runnable onPressAction; // Срабатывает ОДИН РАЗ при физическом нажатии (debounced)
    private final Supplier<Boolean> contextValidator;

    private boolean isPhysicallyDown = false;

    public InputBasedHotkeyListener(IntSupplier keyCodeGetter, Runnable onPressAction) {
        this(keyCodeGetter, onPressAction, () -> true);
    }

    public InputBasedHotkeyListener(IntSupplier keyCodeGetter, Runnable onPressAction, Supplier<Boolean> contextValidator) {
        this.keyCodeGetter = Objects.requireNonNull(keyCodeGetter);
        this.onPressAction = Objects.requireNonNull(onPressAction);
        this.contextValidator = Objects.requireNonNull(contextValidator);
    }

    @Override
    public void tick() {}

    public void onRawKeyInput(int action, int key) {
        if (!contextValidator.get()) return;

        Minecraft client = Minecraft.getInstance();
        if (client == null) return;

        int currentKey = keyCodeGetter.getAsInt();
        if (currentKey <= 0 || key != currentKey) {
            // Если пришла информация о ДРУГОЙ клавише,
            // нам важно сбросить состояние нашей целевой, если она была зажата системно,
            // чтобы избежать рассинхрона фокуса окна. Но для простого toggle это не обязательно.
            return;
        }

        switch (action) {
            case InputConstants.PRESS:
                if (!isPhysicallyDown) {
                    // Переход UP -> DOWN. Выполняем действие.
                    onPressAction.run();
                    isPhysicallyDown = true;
                }
                break;

            case InputConstants.RELEASE:
                // Переход DOWN -> UP. Просто фиксируем состояние.
                // Именно этот кейс решает вашу проблему: теперь система знает, что кнопка отпущена.
                isPhysicallyDown = false;
                break;

            case InputConstants.REPEAT:
                // Игнорируем автоповтор от ОС
                break;
        }
    }

    public void resetState() {
        isPhysicallyDown = false;
    }
}
