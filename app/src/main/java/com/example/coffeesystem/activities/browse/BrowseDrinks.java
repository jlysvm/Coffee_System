package com.example.coffeesystem.activities.browse;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeesystem.R;
import com.example.coffeesystem.adapters.CategoryAdapter;
import com.example.coffeesystem.adapters.DrinkAdapter;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.models.Drink;
import com.example.coffeesystem.repository.CategoryRepository;
import com.example.coffeesystem.repository.DrinkRepository;

import java.util.ArrayList;
import java.util.List;

public class BrowseDrinks extends AppCompatActivity {
    private List<Drink> allDrinks = null;
    private List<Drink> displayedDrinks = new ArrayList<>();
    private DrinkAdapter drinkAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_browse_drinks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.browse_drinks), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.getCategories(new FetchCallback<>() {
            @Override
            public void onSuccess(List<String> result) {
                result.add(0, "All");

                runOnUiThread(() -> {
                    RecyclerView categoryContainer = findViewById(R.id.categories_container);
                    categoryContainer.setLayoutManager(
                        new LinearLayoutManager(BrowseDrinks.this, LinearLayoutManager.HORIZONTAL, false)
                    );

                    CategoryAdapter categoryAdapter = new CategoryAdapter(BrowseDrinks.this, result,category -> {
                        if (drinkAdapter == null) return;
                        displayedDrinks.clear();

                        if (category.equalsIgnoreCase("All")) {
                            displayedDrinks.addAll(allDrinks);
                        }
                        else {
                            for (Drink drink : allDrinks) {
                                if (category.equalsIgnoreCase(drink.getCategory()))
                                    displayedDrinks.add(drink);
                            }
                        }

                        drinkAdapter.notifyDataSetChanged();
                    });

                    categoryContainer.setAdapter(categoryAdapter);
                });
            }

            @Override
            public void onNotFound() {

            }

            @Override
            public void onError(int code) {

            }

            @Override
            public void onNetworkError(Exception e) {

            }
        });

        DrinkRepository drinkRepository = new DrinkRepository();
        drinkRepository.getAllDrinks(new FetchCallback<>() {
            @Override
            public void onSuccess(List<Drink> result) {
                allDrinks = result;
                displayedDrinks.addAll(result);

                runOnUiThread(() -> {
                    RecyclerView drinkCardContainer = findViewById(R.id.drinks_card_container);
                    drinkCardContainer.setLayoutManager(
                        new GridLayoutManager(BrowseDrinks.this, 2)
                    );

                    drinkAdapter = new DrinkAdapter(BrowseDrinks.this, displayedDrinks);
                    drinkCardContainer.setAdapter(drinkAdapter);
                });
            }

            @Override
            public void onNotFound() {

            }

            @Override
            public void onError(int code) {

            }

            @Override
            public void onNetworkError(Exception e) {

            }
        });

        ImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());
    }
}