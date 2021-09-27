package com.icraus.utils;

@FunctionalInterface
public interface CallableEvent<C, T>{
    T execute(C caller);
}
