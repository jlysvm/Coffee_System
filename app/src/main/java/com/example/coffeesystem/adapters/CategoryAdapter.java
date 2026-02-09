package com.example.coffeesystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeesystem.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context mContext;
    private List<String> categories;
    private static int activePosition = 0;

    public CategoryAdapter(Context context, List<String> categories) {
        this.mContext = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.text_category, parent, false);
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryName.setText(category);
        holder.categoryName.setSelected(position == activePosition);

        holder.categoryName.setOnClickListener(v -> {
            int clickedPosition = holder.getBindingAdapterPosition();
            int previous = activePosition;
            activePosition = clickedPosition;

            notifyItemChanged(previous);
            notifyItemChanged(activePosition);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static int getActivePosition() { return activePosition; }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.drink_category);
        }
    }
}
