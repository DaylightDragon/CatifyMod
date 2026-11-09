package org.daylight.catify.common.replacements.common;


public interface IConfigValue<T> {
    T get();
    T getCached();
    void set(T value);
    void save();
}
