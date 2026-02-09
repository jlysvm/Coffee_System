package com.example.coffeesystem.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.coffeesystem.repository.DrinkRepository;
import com.example.coffeesystem.repository.FavoriteRepository;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {
    private static final DrinkRepository drinkRepository = new DrinkRepository();
    private static final FavoriteRepository favoriteRepository = new FavoriteRepository();
    private Context mContext;
    private List<Drink> drinkList;

    public DrinkAdapter(Context context, List<Drink> drinks) {
        this.mContext = context;
        this.drinkList = drinks;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.card_drink, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink currentDrink = drinkList.get(position);
        String imageFileName = currentDrink.getImage();

        holder.productName.setText(currentDrink.getName());
        holder.productCategory.setText(currentDrink.getCategory());

        GlideUrl glideUrl = new GlideUrl(
            BuildConfig.SUPABASE_URL+"/storage/v1/object/drink_images/"+imageFileName,
            new LazyHeaders.Builder()
                    .addHeader("apikey", BuildConfig.SUPABASE_KEY)
                    .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_KEY)
                    .build()
        );
        Glide.with(mContext).load(glideUrl).into(holder.productImage);

        holder.productCard.setOnClickListener(v -> {
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
            updateFavoriteButton(favoriteButton, currentDrink);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
    }

    private void updateFavoriteButton(Button favoriteButton, Drink currentDrink) {
        String addTxt = "ADD TO FAVORITES";
        String removeTxt = "REMOVE FROM FAVORITES";

        if (currentDrink.isFavorited()) {
            favoriteButton.setText(removeTxt);
            favoriteButton.setOnClickListener(v -> {
                favoriteRepository.removeFavoriteDrink(
                    LoginActivity.getAuthenticatedUser().getId(),
                    currentDrink.getId(),
                    new RequestCallback() {
                        @Override
                        public void onSuccess() {
                            currentDrink.setFavorited(false);
                            updateFavoriteButton(favoriteButton, currentDrink);
                        }

                        @Override
                        public void onError(int code) { }

                        @Override
                        public void onNetworkError(Exception e) { }
                    });
            });
        } else {
            favoriteButton.setText(addTxt);
            favoriteButton.setOnClickListener(v -> {
                favoriteRepository.addFavoriteDrink(
                    LoginActivity.getAuthenticatedUser().getId(),
                    currentDrink.getId(),
                    new RequestCallback() {
                        @Override
                        public void onSuccess() {
                            currentDrink.setFavorited(true);
                            updateFavoriteButton(favoriteButton, currentDrink);
                        }

                        @Override
                        public void onError(int code) { }

                        @Override
                        public void onNetworkError(Exception e) { }
                    });
            });
        }
    }


    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        CardView productCard;
        ImageView productImage;
        TextView productName, productCategory;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            productCard = itemView.findViewById(R.id.product_card);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productCategory = itemView.findViewById(R.id.product_category);
        }
    }
}