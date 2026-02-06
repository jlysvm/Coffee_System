package com.example.coffeesystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DrinkAdapter extends RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder> {

    private List<Drink> drinkList;

    public DrinkAdapter(List<Drink> drinkList) {
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates your CardView layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new DrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkViewHolder holder, int position) {
        Drink currentDrink = drinkList.get(position);

        // Setting the data per drink
        holder.productName.setText(currentDrink.getName());
        holder.productCategory.setText(currentDrink.getCategory());
        holder.productImage.setImageResource(currentDrink.getImageResource());
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