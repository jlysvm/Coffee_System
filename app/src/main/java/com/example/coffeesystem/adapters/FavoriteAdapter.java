package com.example.coffeesystem.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.coffeesystem.callbacks.RequestCallback;
import com.example.coffeesystem.models.Drink;
import com.example.coffeesystem.repository.FavoriteRepository;
import com.example.coffeesystem.repository.UserManager;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private static final FavoriteRepository favoriteRepository = new FavoriteRepository();
    private Context mContext;
    private List<Drink> allDrinks;
    private List<Drink> drinkList;

    public FavoriteAdapter(Context context, List<Drink> drinks, List<Drink> allDrinks) {
        this.mContext = context;
        this.drinkList = drinks;
        this.allDrinks = allDrinks;
    }

    @NonNull
    @Override
    public FavoriteAdapter.FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.card_favorite_drink, parent, false);
        return new FavoriteAdapter.FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.FavoriteViewHolder holder, int position) {
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

            favoriteButton.setVisibility(View.INVISIBLE);

            drinkName.setText(currentDrink.getName());
            drinkCategory.setText(currentDrink.getCategory());
            drinkDescription.setText(currentDrink.getDescription());
            drinkIngredients.setText(currentDrink.getIngredients());

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();

            new AlertDialog.Builder(context)
                .setTitle("Remove from Favorites")
                .setMessage("Are you sure you want to remove this drink from your favorites?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    favoriteRepository.removeFavoriteDrink(
                        UserManager.getInstance().getUser().getId(),
                        currentDrink.getId(),

                        new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                holder.itemView.post(() -> {
                                    currentDrink.setFavorited(false);
                                    int pos = holder.getBindingAdapterPosition();
                                    if (pos != RecyclerView.NO_POSITION) {
                                        allDrinks.remove(drinkList.remove(pos));
                                        notifyItemRemoved(pos);
                                    }
                                });
                            }

                            @Override
                            public void onError(int code) { }

                            @Override
                            public void onNetworkError(Exception e) { }
                        }
                    );
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public CardView drinkCard;
        public ImageView drinkImage;
        public TextView drinkName;
        public TextView drinkCategory;
        public ImageButton deleteButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            drinkCard = itemView.findViewById(R.id.drink_card);
            drinkImage = itemView.findViewById(R.id.drink_image);
            drinkName = itemView.findViewById(R.id.drink_name);
            drinkCategory = itemView.findViewById(R.id.drink_category);
            deleteButton = itemView.findViewById(R.id.delete_btn);
        }
    }
}
