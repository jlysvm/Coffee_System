package com.example.coffeesystem.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.R;
import com.example.coffeesystem.activities.auth.LoginActivity;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.callbacks.RequestCallback;
import com.example.coffeesystem.models.Drink;
import com.example.coffeesystem.repository.CategoryRepository;
import com.example.coffeesystem.repository.DrinkRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminViewDrinksAdapter extends RecyclerView.Adapter<AdminViewDrinksAdapter.AdminDrinksViewHolder> {
    private static final DrinkRepository drinkRepository = new DrinkRepository();
    private Context mContext;
    private List<Drink> allDrinks;
    private List<Drink> drinkList;

    public AdminViewDrinksAdapter(Context context, List<Drink> drinks, List<Drink> allDrinks) {
        this.mContext = context;
        this.drinkList = drinks;
        this.allDrinks = allDrinks;
    }

    @NonNull
    @Override
    public AdminViewDrinksAdapter.AdminDrinksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.card_drink_crud, parent, false);
        return new AdminViewDrinksAdapter.AdminDrinksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewDrinksAdapter.AdminDrinksViewHolder holder, int position) {
        Drink currentDrink = drinkList.get(position);
        String imageFileName = currentDrink.getImage();

        holder.drinkName.setText(currentDrink.getName());
        holder.drinkCategory.setText(currentDrink.getCategory());

        GlideUrl glideUrl = new GlideUrl(
                BuildConfig.SUPABASE_URL+"/storage/v1/object/drink_images/"+imageFileName,
                new LazyHeaders.Builder()
                        .addHeader("apikey", BuildConfig.SUPABASE_KEY)
                        .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_KEY)
                        .build()
        );
        Glide.with(mContext).load(glideUrl).into(holder.drinkImage);

        holder.editButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.card_drink_edit_info);

            ImageView drinkImage = dialog.findViewById(R.id.drink_image);
            EditText drinkName = dialog.findViewById(R.id.drink_name);
            Spinner drinkCategory = dialog.findViewById(R.id.drink_category_spinner);
            EditText drinkDescription = dialog.findViewById(R.id.drink_description);
            EditText drinkIngredients = dialog.findViewById(R.id.drink_ingredients);
            Button saveButton = dialog.findViewById(R.id.save_btn);
            Button cancelButton = dialog.findViewById(R.id.cancel_btn);

            drinkName.setText(currentDrink.getName());
            drinkDescription.setText(currentDrink.getDescription());
            drinkIngredients.setText(currentDrink.getIngredients());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    v.getContext(),
                    R.layout.spinner_category_drink_edit_info,
                    new ArrayList<>()
            );
            adapter.setDropDownViewResource(R.layout.spinner_category_drink_edit_info);
            drinkCategory.setAdapter(adapter);

            new CategoryRepository().getCategories(new FetchCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> result) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        adapter.clear();
                        adapter.addAll(result);

                        int index = adapter.getPosition(currentDrink.getCategory());
                        if (index >= 0) {
                            drinkCategory.setSelection(index);
                        }
                    });
                }

                @Override
                public void onError(int code) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(v.getContext(), "Error loading categories", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onNotFound() {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(v.getContext(), "No categories found", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onNetworkError(Exception e) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(v.getContext(), "Network error loading categories", Toast.LENGTH_SHORT).show()
                    );
                }
            });

            saveButton.setOnClickListener(v1 -> {
                String updatedName = drinkName.getText().toString().trim();
                String updatedCategory = drinkCategory.getSelectedItem().toString();
                String updatedDescription = drinkDescription.getText().toString().trim();
                String updatedIngredients = drinkIngredients.getText().toString().trim();

                if (updatedCategory.equals("Select Category")) {
                    Toast.makeText(v1.getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentDrink.setName(updatedName);
                currentDrink.setCategory(updatedCategory);
                currentDrink.setDescription(updatedDescription);
                currentDrink.setIngredients(updatedIngredients);

                drinkRepository.updateDrink(currentDrink, new RequestCallback() {
                    @Override
                    public void onSuccess() {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            notifyItemChanged(holder.getBindingAdapterPosition());
                            Toast.makeText(v1.getContext(), "Drink updated successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    }

                    @Override
                    public void onError(int code) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(v1.getContext(), "Error updating drink: " + code, Toast.LENGTH_SHORT).show()
                        );
                    }

                    @Override
                    public void onNetworkError(Exception e) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(v1.getContext(), "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            });

            cancelButton.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();

            new AlertDialog.Builder(context)
                    .setTitle("Remove from Database")
                    .setMessage("Are you sure you want to remove this drink from the database?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        drinkRepository.deleteDrink(currentDrink.getId(), new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                int pos = holder.getBindingAdapterPosition();
                                drinkList.remove(pos);
                                notifyItemRemoved(pos);

                                Toast.makeText(context, "Drink removed successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(int code) {}

                            @Override
                            public void onNetworkError(Exception e) {}
                        });

                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        holder.drinkCard.setOnClickListener(v -> {
            Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.card_drink_info);

            ImageView drinkImage = dialog.findViewById(R.id.drink_image);
            TextView drinkName = dialog.findViewById(R.id.drink_name);
            TextView drinkCategory = dialog.findViewById(R.id.drink_category);
            TextView drinkDescription = dialog.findViewById(R.id.drink_description);
            TextView drinkIngredients = dialog.findViewById(R.id.drink_ingredients);
            Button favoriteButton = dialog.findViewById(R.id.favorite_btn);

            Glide.with(mContext).load(glideUrl).into(drinkImage);

            drinkName.setText(currentDrink.getName());
            drinkCategory.setText(currentDrink.getCategory());
            drinkDescription.setText(currentDrink.getDescription());
            drinkIngredients.setText(currentDrink.getIngredients());

            favoriteButton.setVisibility(View.INVISIBLE);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public static class AdminDrinksViewHolder extends RecyclerView.ViewHolder {
        public CardView drinkCard;
        public ImageView drinkImage;
        public TextView drinkName;
        public TextView drinkCategory;
        public ImageButton editButton;
        public ImageButton deleteButton;

        public AdminDrinksViewHolder(@NonNull View itemView) {
            super(itemView);
            drinkCard = itemView.findViewById(R.id.drink_card);
            drinkImage = itemView.findViewById(R.id.drink_image);
            drinkName = itemView.findViewById(R.id.drink_name);
            drinkCategory = itemView.findViewById(R.id.drink_category);
            editButton = itemView.findViewById(R.id.edit_btn);
            deleteButton = itemView.findViewById(R.id.delete_btn);
        }
    }
}
