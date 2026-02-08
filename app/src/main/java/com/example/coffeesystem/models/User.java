package com.example.coffeesystem.models;

public class User {
    private final String username;
    private final String email;
    private final String password;
    private final int roleID;

    public User(String username, String email, String password, int roleID) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleID = roleID;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getRoleID() { return roleID; }
}