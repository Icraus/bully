package com.icraus;

@FunctionalInterface
public interface CallableEvent<C, T>{
    T execute(C caller);
}
