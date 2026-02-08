package com.example.coffeesystem.activities.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeesystem.R;
import com.example.coffeesystem.callbacks.UserFetchCallback;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserRepository;

import org.mindrot.jbcrypt.BCrypt;

public class Login extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private Button loginButton;
    private TextView signUpLink, guestLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signUpLink = findViewById(R.id.signUpLink);
        guestLink = findViewById(R.id.guestLink);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        signUpLink.setOnClickListener(v -> {
            // todo: navigate to signup screen
            Toast.makeText(Login.this, "Navigate to Sign Up", Toast.LENGTH_SHORT).show();
        });

        guestLink.setOnClickListener(v -> {
            // todo: authenticate as guest
            Toast.makeText(Login.this, "Continuing as Guest", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String email = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        UserRepository repository = new UserRepository();

        repository.getUserByEmail(email, new UserFetchCallback() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    if (BCrypt.checkpw(password, user.getPassword())) {
                        Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // TODO: navigate to next screen
                    } else {
                        Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNotFound() {
                runOnUiThread(() ->
                    Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(int code) {
                runOnUiThread(() ->
                    Toast.makeText(Login.this, "Server error: " + code, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNetworkError(Exception e) {
                runOnUiThread(() ->
                    Toast.makeText(Login.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

}