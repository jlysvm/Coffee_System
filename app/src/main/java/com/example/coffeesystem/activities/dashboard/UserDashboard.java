package com.example.coffeesystem.activities.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.coffeesystem.R;
import com.example.coffeesystem.activities.auth.LoginActivity;
import com.example.coffeesystem.activities.drinks.BrowseDrinks;
import com.example.coffeesystem.activities.drinks.FavoriteDrinks;
import com.example.coffeesystem.activities.profile.ProfileSetting;

public class UserDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppCompatButton profileBtn = findViewById(R.id.btnMyProfile);
        AppCompatButton browseDrinksBtn = findViewById(R.id.btnRounded);
        AppCompatButton myFavoritesBtn = findViewById(R.id.btnViewDrink1);

        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, ProfileSetting.class);
            startActivity(intent);
        });
        browseDrinksBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, BrowseDrinks.class);
            startActivity(intent);
        });

        if ("GUEST".equalsIgnoreCase(LoginActivity.getAuthenticatedUser().getRole())) {
            myFavoritesBtn.setVisibility(View.GONE);
        }
        else {
            myFavoritesBtn.setOnClickListener(v -> {
                Intent intent = new Intent(UserDashboard.this, FavoriteDrinks.class);
                startActivity(intent);
            });
        }

        TextView username = findViewById(R.id.textView2);
        username.setText(LoginActivity.getAuthenticatedUser().getUsername());
    }
}