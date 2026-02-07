package com.example.coffeesystem.callbacks;

public interface InsertCallback {
    void onSuccess();
    void onError(int code);
    void onNetworkError(Exception e);
}
