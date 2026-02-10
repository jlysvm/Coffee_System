package com.example.coffeesystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeesystem.R;
import com.example.coffeesystem.activities.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay for 3 seconds (3000 milliseconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start Login Activity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);

            // Close Splash Activity so the user can't go back to it
            finish();
        }, 3000);
    }
}
