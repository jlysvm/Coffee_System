package com.example.coffeesystem.callbacks;

import com.example.coffeesystem.models.User;

public interface UserFetchCallback {
    void onSuccess(User user);
    void onNotFound();
    void onError(int code);
    void onNetworkError(Exception e);
}
