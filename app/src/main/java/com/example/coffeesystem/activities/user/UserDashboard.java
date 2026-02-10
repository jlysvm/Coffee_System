package com.example.coffeesystem.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.example.coffeesystem.activities.user.BrowseDrinks;
import com.example.coffeesystem.activities.user.FavoriteDrinks;
import com.example.coffeesystem.activities.profile.ProfileSetting; // Make sure to import this
import com.example.coffeesystem.models.User;
import com.example.coffeesystem.repository.UserManager;

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

        // 1. Get User from Singleton
        User currentUser = UserManager.getInstance().getUser();

        // 2. Safety Check: If user is null, go back to Login to prevent crash
        if (currentUser == null) {
            Toast.makeText(this, "Session expired, please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        AppCompatButton browseDrinksBtn = findViewById(R.id.btnRounded);
        AppCompatButton myFavoritesBtn = findViewById(R.id.btnViewDrink1);
        AppCompatButton myProfileBtn = findViewById(R.id.btnMyProfile); // Ensure ID matches XML

        browseDrinksBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, BrowseDrinks.class);
            startActivity(intent);
        });

        // Safe check using currentUser variable
        if ("GUEST".equalsIgnoreCase(currentUser.getRole())) {
            myFavoritesBtn.setVisibility(View.GONE);
        } else {
            myFavoritesBtn.setOnClickListener(v -> {
                Intent intent = new Intent(UserDashboard.this, FavoriteDrinks.class);
                startActivity(intent);
            });
        }

        // Setup Profile Button
        myProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboard.this, ProfileSetting.class);
            startActivity(intent);
        });

        TextView username = findViewById(R.id.textView2);
        username.setText(currentUser.getUsername());
    }
}