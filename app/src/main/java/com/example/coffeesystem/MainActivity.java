package com.example.coffeesystem;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            mAuth.signInAnonymously()
                .addOnSuccessListener(this, authResult -> {
                    Log.d("Firebase", "Teammate signed in silently: " + authResult.getUser().getUid());
                })
                .addOnFailureListener(this, e -> {
                    Log.e("Firebase", "Sign-in failed. Check SHA-1 and Console settings!", e);
                });
        } else {
            Log.d("Firebase", "Already signed in as: " + currentUser.getUid());
        }

        findViewById(R.id.product_recycler_view).setAdapter(new DrinkAdapter());
    }
}