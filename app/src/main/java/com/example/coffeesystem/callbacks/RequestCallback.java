package com.example.coffeesystem.callbacks;

public interface RequestCallback {
    void onSuccess();
    void onError(int code);
    void onNetworkError(Exception e);
}
