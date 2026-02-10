package com.example.coffeesystem.repository;

import com.example.coffeesystem.models.User;

public class UserManager {
    private static UserManager instance;
    private User currentUser;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) instance = new UserManager();
        return instance;
    }

    public void setUser(User user) {
        this.currentUser = user;
    }

    public User getUser() {
        return currentUser;
    }
}

