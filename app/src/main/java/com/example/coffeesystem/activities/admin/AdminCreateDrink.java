package com.example.coffeesystem.activities.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;

import com.example.coffeesystem.R;
import com.example.coffeesystem.callbacks.RequestCallback;
import com.example.coffeesystem.models.Drink;
import com.example.coffeesystem.repository.CategoryRepository;
import com.example.coffeesystem.repository.DrinkRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminCreateDrink extends AppCompatActivity {
    private final CategoryRepository categoryRepository = new CategoryRepository();
    private final DrinkRepository drinkRepository = new DrinkRepository();

    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ImageView uploadedImage;
    private MaterialButton uploadFileBtn;
    private EditText etProductName, etDescription, etIngredients, etFileName;
    private Spinner etCatId;
    private Button btnAdd;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_drink);

        uploadedImage = findViewById(R.id.uploaded_image);
        uploadFileBtn = findViewById(R.id.upload_file);
        etProductName = findViewById(R.id.etProductName);
        etDescription = findViewById(R.id.etDescription);
        etIngredients = findViewById(R.id.etIngredients);
        etFileName = findViewById(R.id.etFileName);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(AdminCreateDrink.this)
                .setTitle("Cancel")
                .setMessage("Are you sure you want to cancel creating this drink?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
        });

        uploadFileBtn.setOnClickListener(v -> openImagePicker());
        btnAdd.setOnClickListener(v -> createDrink());

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        uploadedImage.setImageURI(selectedImageUri);
                    }
                }
        );

        etCatId = findViewById(R.id.spinner_category);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_category_create_drink,
                new ArrayList<>()
        );
        adapter.setDropDownViewResource(R.layout.spinner_category_create_drink);
        etCatId.setAdapter(adapter);

        categoryRepository.getCategories(new com.example.coffeesystem.callbacks.FetchCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                runOnUiThread(() -> {
                    adapter.clear();
                    adapter.add("Select Category");
                    adapter.addAll(result);
                    etCatId.setSelection(0);
                });
            }

            @Override
            public void onError(int code) {
                runOnUiThread(() ->
                    Toast.makeText(AdminCreateDrink.this, "Error fetching categories: " + code, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNotFound() {
                runOnUiThread(() ->
                    Toast.makeText(AdminCreateDrink.this, "No categories found", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNetworkError(Exception e) {
                runOnUiThread(() ->
                    Toast.makeText(AdminCreateDrink.this, "Network error fetching categories", Toast.LENGTH_SHORT).show()
                );
            }
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Drink Image"));
    }

    private void createDrink() {
        String name = etProductName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String ingredients = etIngredients.getText().toString().trim();
        String category = etCatId.getSelectedItem().toString().trim();
        String fileName = etFileName.getText().toString().trim();

        if (name.isEmpty()) {
            etProductName.setError("Drink name is required");
        }
        else if (selectedImageUri == null && fileName.isEmpty()) {
            createNewDrink(name, description, "backup.png", category, ingredients);
        }
        else if (fileName.isEmpty()) {
            Toast.makeText(this, "File name field is required", Toast.LENGTH_SHORT).show();
        }
        else if (selectedImageUri == null) {
            createNewDrink(name, description, fileName, category, ingredients);
        }
        else {
            createDrinkWithNewImage(this, name, description, fileName, category, ingredients);
        }
    }

    private void createDrinkWithNewImage(String name, String description, String fileName, String category, String ingredients) {
        drinkRepository.uploadDrinkImage(this, selectedImageUri, fileName, new RequestCallback() {
            @Override
            public void onSuccess() {
                createNewDrink(name, description, fileName, category, ingredients);
                AdminCreateDrink.this.finish();
            }

            @Override
            public void onError(int code) {
                new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(AdminCreateDrink.this, "Error uploading image: " + code, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNetworkError(Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(AdminCreateDrink.this, "Network error uploading image", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void createNewDrink(String name, String description, String fileName, String category, String ingredients) {
        Drink newDrink = new Drink(
            name, description, fileName,
            category, ingredients, false
        );

        drinkRepository.createDrink(newDrink, new RequestCallback() {
            @Override
            public void onSuccess() {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(AdminCreateDrink.this, "Drink created successfully!", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(int code) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(AdminCreateDrink.this, "Error creating drink: " + code, Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onNetworkError(Exception e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(AdminCreateDrink.this, "Network error creating drink", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
