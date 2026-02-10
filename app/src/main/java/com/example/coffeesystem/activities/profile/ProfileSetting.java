package com.example.coffeesystem.activities.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffeesystem.R;
import com.example.coffeesystem.activities.auth.LoginActivity;
import com.example.coffeesystem.repository.UserManager;

public class ProfileSetting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_setting);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back Button
        ImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        // Navigation: Edit Profile
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(ProfileSetting.this, EditProfile.class))
        );

        // Navigation: Account Overview
        Button btnAccountOverview = findViewById(R.id.btnAccountOverview);
        btnAccountOverview.setOnClickListener(v ->
                startActivity(new Intent(ProfileSetting.this, AccountOverview.class))
        );

        // Navigation: Logout
        Button logoutBtn = findViewById(R.id.btnLogout);
        logoutBtn.setOnClickListener(v -> {
            UserManager.getInstance().setUser(null);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}