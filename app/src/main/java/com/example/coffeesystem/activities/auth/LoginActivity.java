package com.example.coffeesystem.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeesystem.activities.admin.AdminDashboard;
import com.example.coffeesystem.activities.user.UserDashboard;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.databinding.ActivityLoginBinding;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserRepository;
import com.example.coffeesystem.repository.UserManager;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(v -> handleLogin());

        binding.signUpLink.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );

        binding.guestLink.setOnClickListener(v -> {
            // Guest has no real password, so we just set a placeholder
            User guestUser = new User(
                    0, // ID 0 for guest
                    "Guest",
                    "No email provided",
                    "No password provided",
                    "GUEST"
            );
            UserManager.getInstance().setUser(guestUser);
            Toast.makeText(this, "Continuing as Guest", Toast.LENGTH_SHORT).show();

            // Adjust destination based on your actual package structure
            Intent intent = new Intent(LoginActivity.this, com.example.coffeesystem.activities.user.UserDashboard.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleLogin() {
        String email = binding.usernameInput.getText().toString().trim();
        String passwordInput = binding.passwordInput.getText().toString().trim(); // This is the PLAIN TEXT

        if (email.isEmpty() || passwordInput.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRepository repository = new UserRepository();

        repository.getUserByEmail(email, new FetchCallback<User>() {
            @Override
            public void onSuccess(User dbUser) {
                runOnUiThread(() -> {
                    // Check if the input matches the hash in the DB
                    if (BCrypt.checkpw(passwordInput, dbUser.getPassword())) {

                        // FIX IS HERE:
                        // Instead of saving 'dbUser' (which has the hash),
                        // we create a new User object with the 'passwordInput' (plain text).

                        User sessionUser = new User(
                                dbUser.getId(),
                                dbUser.getUsername(),
                                dbUser.getEmail(),
                                passwordInput, // <--- Store the PLAIN TEXT password in memory
                                dbUser.getRole()
                        );

                        UserManager.getInstance().setUser(sessionUser);

                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Route to correct dashboard
                        Class<?> targetActivity;
                        if (dbUser.getRole().equalsIgnoreCase("USER")) {
                            targetActivity = com.example.coffeesystem.activities.user.UserDashboard.class;
                        } else {
                            targetActivity = com.example.coffeesystem.activities.admin.AdminDashboard.class;
                        }

                        Intent intent = new Intent(LoginActivity.this, targetActivity);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNotFound() {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(int code) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Server error: " + code, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNetworkError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}