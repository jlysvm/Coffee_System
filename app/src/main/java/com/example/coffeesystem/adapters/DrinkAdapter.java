package com.example.coffeesystem.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.coffeesystem.BuildConfig;
import com.example.coffeesystem.R;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.models.Drink;
import com.example.coffeesystem.repository.DrinkRepository;

import java.util.HashMap;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {
    private static final DrinkRepository drinkRepository = new DrinkRepository();
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
                .inflate(R.layout.card_drinkproduct, parent, false);
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
        Glide.with(mContext)
            .load(glideUrl)
            .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    public static class DrinkViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productCategory;

        public DrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productCategory = itemView.findViewById(R.id.product_category);
        }
    }
}