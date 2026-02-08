package com.example.coffeesystem.activities.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeesystem.callbacks.InsertCallback;
import com.example.coffeesystem.databinding.ActivitySignupBinding;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupButton.setOnClickListener(v -> handleSignUp());
        binding.loginLink.setOnClickListener(v -> finish());
    }

    private void handleSignUp() {
        String username = binding.usernameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
            TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.setError("Please enter a valid email address");
            binding.emailInput.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.confirmPasswordInput.setError("Passwords do not match!");
            binding.confirmPasswordInput.requestFocus();
            return;
        }
        if (password.length() < 8) {
            binding.passwordInput.setError("Password must be at least 8 characters");
            binding.passwordInput.requestFocus();
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(username, email, hashedPassword, "USER");
        UserRepository repository = new UserRepository();

        repository.insertUser(newUser, new InsertCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(SignUpActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(int code) {
                runOnUiThread(() ->
                    Toast.makeText(SignUpActivity.this, "Sign up failed: " + code, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNetworkError(Exception e) {
                runOnUiThread(() ->
                    Toast.makeText(SignUpActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}