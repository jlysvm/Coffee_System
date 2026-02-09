package com.example.coffeesystem.callbacks;

@FunctionalInterface
public interface EventCallback<V> {
    void onEvent(V view);
}
