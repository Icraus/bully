package com.icraus.utils;

@FunctionalInterface
public interface ValueObserverListener<T> {
    void onValueChanged(T oldValue, T newValue);
}
