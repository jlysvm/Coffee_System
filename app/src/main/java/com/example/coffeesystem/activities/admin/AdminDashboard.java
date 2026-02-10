package com.example.coffeesystem.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffeesystem.R;
import com.example.coffeesystem.activities.auth.LoginActivity;
import com.example.coffeesystem.activities.profile.ProfileSetting;
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserManager;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        User currentUser = UserManager.getInstance().getUser();

        if (currentUser == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView usernameView = findViewById(R.id.textView2);

        String username = UserManager.getInstance().getUser().getUsername();
        usernameView.setText(username);

        AppCompatButton btnMyProfile = findViewById(R.id.btnMyProfile);
        btnMyProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, ProfileSetting.class);
            startActivity(intent);
        });

        AppCompatButton btnViewDrinks = findViewById(R.id.btnViewDrinks);
        btnViewDrinks.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminViewDrinks.class);
            startActivity(intent);
        });




    }
}

