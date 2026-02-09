package com.example.coffeesystem.models;

public class User {
    private final long id;
    private final String username;
    private final String email;
    private final String password;
    private final String role;

    public User(String username, String email, String password, String role) {
        this.id = 0;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(long id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}