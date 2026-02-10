package com.example.coffeesystem.activities.admin;

import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeesystem.R;
import com.example.coffeesystem.activities.auth.LoginActivity;
import com.example.coffeesystem.adapters.AdminViewDrinksAdapter;
import com.example.coffeesystem.adapters.CategoryAdapter;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.models.Drink;
import com.example.coffeesystem.repository.CategoryRepository;
import com.example.coffeesystem.repository.DrinkRepository;
import com.example.coffeesystem.repository.FavoriteRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminViewDrinks extends AppCompatActivity {
    private List<Drink> allDrinks = null;
    private final List<Drink> displayedDrinks = new ArrayList<>();
    private String searchText = "";
    private String category = "All";
    private AdminViewDrinksAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_view_drinks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_view_drinks), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        FloatingActionButton createNewDrinkBtn = findViewById(R.id.add_drink_btn);
        createNewDrinkBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminViewDrinks.this, AdminCreateDrink.class);
            startActivity(intent);
        });

        CategoryRepository categoryRepository = new CategoryRepository();
        categoryRepository.getCategories(new FetchCallback<>() {
            @Override
            public void onSuccess(List<String> result) {
                result.add(0, "All");

                runOnUiThread(() -> {
                    RecyclerView categoryContainer = findViewById(R.id.categories_container);

                    CategoryAdapter categoryAdapter = new CategoryAdapter(AdminViewDrinks.this, result, category -> {
                        if (adapter == null || allDrinks == null) return;
                        AdminViewDrinks.this.category = category;
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
                    adapter = new AdminViewDrinksAdapter(AdminViewDrinks.this, displayedDrinks, result);
                    drinkCardContainer.setAdapter(adapter);
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
                if (adapter == null || allDrinks == null) return;
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

        adapter.notifyDataSetChanged();
    }
}
