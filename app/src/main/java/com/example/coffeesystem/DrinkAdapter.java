package com.example.coffeesystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {
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
                .inflate(R.layout.item_card, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink currentDrink = drinkList.get(position);
        int imageResourceId = mContext.getResources().getIdentifier(
                currentDrink.getImage(), "drawable", mContext.getPackageName()
        );

        holder.productName.setText(currentDrink.getName());
        holder.productCategory.setText(currentDrink.getCategory());
        holder.productImage.setImageResource(imageResourceId);
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