package com.example.coffeesystem.activities.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeesystem.R;
import com.example.coffeesystem.models.User;

public class AuthActivity extends AppCompatActivity {

    private static User authenticatedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }

    public static User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public static void setAuthenticatedUser(User authenticatedUser) {
        AuthActivity.authenticatedUser = authenticatedUser;
    }
}
