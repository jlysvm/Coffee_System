package com.example.coffeesystem.activities.drinks;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private final List<Drink> displayedDrinks = new ArrayList<>();
    private String searchText = "";
    private String category = "All";
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

        ImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.getCategories(new FetchCallback<>() {
            @Override
            public void onSuccess(List<String> result) {
                result.add(0, "All");

                runOnUiThread(() -> {
                    RecyclerView categoryContainer = findViewById(R.id.categories_container);

                    CategoryAdapter categoryAdapter = new CategoryAdapter(BrowseDrinks.this, result,category -> {
                        if (drinkAdapter == null || allDrinks == null) return;
                        BrowseDrinks.this.category = category;
                        updateDisplayedDrinks();
                    });

                    categoryContainer.setAdapter(categoryAdapter);
                });
            }

            @Override
            public void onNotFound() {}

            @Override
            public void onError(int code) {}

            @Override
            public void onNetworkError(Exception e) {}
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
            public void onNotFound() {}

            @Override
            public void onError(int code) {}

            @Override
            public void onNetworkError(Exception e) {}
        });

        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (drinkAdapter == null || allDrinks == null) return;
                searchText = s.toString();
                updateDisplayedDrinks();
            }
        });
    }

    private void updateDisplayedDrinks() {
        displayedDrinks.clear();
        boolean categoryIsAll = category.equalsIgnoreCase("All");

        if (searchText.isEmpty()) {
            if (categoryIsAll) {
                displayedDrinks.addAll(allDrinks);
            }
            else {
                for (Drink drink : allDrinks) {
                    if (category.equalsIgnoreCase(drink.getCategory()))
                        displayedDrinks.add(drink);
                }
            }
        }
        else {
            if (categoryIsAll) {
                for (Drink drink : allDrinks) {
                    if (drink.getName().toLowerCase().contains(searchText.toLowerCase()))
                        displayedDrinks.add(drink);
                }
            }
            else {
                for (Drink drink : allDrinks) {
                    if (category.equalsIgnoreCase(drink.getCategory()) &&
                        drink.getName().toLowerCase().contains(searchText.toLowerCase()))

                        displayedDrinks.add(drink);
                }
            }
        }

        drinkAdapter.notifyDataSetChanged();
    }
}