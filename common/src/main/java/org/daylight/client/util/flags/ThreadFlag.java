package org.daylight.client.util.flags;

public final class ThreadFlag<T> {
    private final ThreadLocal<T> value = new ThreadLocal<>();

    public void set(T value) {
        this.value.set(value);
    }

    public T get() {
        return value.get();
    }

    public T consume() {
        T value = this.value.get();
        this.value.remove();
        return value;
    }

    public boolean isSet() {
        return value.get() != null;
    }

    public void clear() {
        value.remove();
    }
}
