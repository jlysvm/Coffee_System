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

import com.example.coffeesystem.R;
import com.example.coffeesystem.callbacks.FetchCallback;
import com.example.coffeesystem.models.Drink;
import com.example.coffeesystem.repository.DrinkRepository;

import java.util.HashMap;
import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {
    private static final DrinkRepository drinkRepository = new DrinkRepository();
    private HashMap<String, Bitmap> imageCache = new HashMap<>();
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

        holder.productName.setText(currentDrink.getName());
        holder.productCategory.setText(currentDrink.getCategory());

        String imageFileName = currentDrink.getImage();

        if (imageCache.containsKey(imageFileName)) {
            holder.productImage.setImageBitmap(imageCache.get(imageFileName));
            return;
        }

        holder.productImage.setTag(imageFileName);

        drinkRepository.getDrinkImage(imageFileName, new FetchCallback<>() {
            @Override
            public void onSuccess(Bitmap result) {
                if (imageFileName.equals(holder.productImage.getTag())) {
                    holder.productImage.setImageBitmap(result);
                    imageCache.put(imageFileName, result);
                }
            }

            @Override
            public void onNotFound() {
//                holder.productImage.setImageResource(R.drawable.image_not_found);
            }

            @Override
            public void onError(int code) {
//                holder.productImage.setImageResource(R.drawable.image_not_found);
            }

            @Override
            public void onNetworkError(Exception e) {
//                holder.productImage.setImageResource(R.drawable.image_not_found);
            }
        });
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