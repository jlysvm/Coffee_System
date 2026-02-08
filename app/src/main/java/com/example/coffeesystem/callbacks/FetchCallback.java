package com.example.coffeesystem.callbacks;

public interface FetchCallback<T> {
    void onSuccess(T result);
    void onNotFound();
    void onError(int code);
    void onNetworkError(Exception e);
}
