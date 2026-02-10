package com.example.coffeesystem.activities.profile;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffeesystem.R;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserManager;

public class AccountOverview extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.account_overview);

        // Header Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.headerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPassword = findViewById(R.id.tvPassword);
        ImageButton btnTogglePassword = findViewById(R.id.btnTogglePassword);

        User user = UserManager.getInstance().getUser();

        if (user != null) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
            // Default masked state
            tvPassword.setText("••••••••");
        }

        btnTogglePassword.setOnClickListener(v -> {
            if (user == null) return;

            if (isPasswordVisible) {
                // Hide
                tvPassword.setText("••••••••");
                isPasswordVisible = false;
            } else {
                // Show
                tvPassword.setText(user.getPassword());
                isPasswordVisible = true;
            }
        });
    }
}