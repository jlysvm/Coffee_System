package com.example.coffeesystem.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeesystem.activities.dashboard.AdminDashboard;
import com.example.coffeesystem.activities.dashboard.UserDashboard;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.databinding.ActivityLoginBinding;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {

    private static User authenticatedUser;
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

        binding.guestLink.setOnClickListener(v ->
                Toast.makeText(this, "Continuing as Guest", Toast.LENGTH_SHORT).show()
        );
    }

    private void handleLogin() {
        String email = binding.usernameInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRepository repository = new UserRepository();

        repository.getUserByEmail(email, new FetchCallback<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    if (BCrypt.checkpw(password, user.getPassword())) {
                        authenticatedUser = user;
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                user.getRole().equalsIgnoreCase("USER") ?
                                        UserDashboard.class : AdminDashboard.class
                        );
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
