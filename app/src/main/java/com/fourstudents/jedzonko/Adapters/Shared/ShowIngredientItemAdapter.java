package com.fourstudents.jedzonko.Adapters.Shared;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;

public class ShowIngredientItemAdapter extends ListAdapter<IngredientItem, ShowIngredientItemAdapter.ViewHolderClass> {
    Context context;
    private final OnIngredientItemListener onIngredientItemListener;

    public ShowIngredientItemAdapter(Context context, OnIngredientItemListener onIngredientItemListener) {
        super(new DiffUtil.ItemCallback<IngredientItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull IngredientItem oldItem, @NonNull IngredientItem newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull IngredientItem oldItem, @NonNull IngredientItem newItem) {
                return oldItem == newItem;
            }
        });

        this.context = context;
        this.onIngredientItemListener = onIngredientItemListener;
    }

    @NonNull
    @Override
    public ShowIngredientItemAdapter.ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_item_list, parent, false);
        ShowIngredientItemAdapter.ViewHolderClass viewHolderClass = new ShowIngredientItemAdapter.ViewHolderClass(view, onIngredientItemListener);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ShowIngredientItemAdapter.ViewHolderClass holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_recipes);
        IngredientItem ingredientItem = getItem(position);
        holder.textView.setText(ingredientItem.product.getName());
        holder.countTextView.setVisibility(View.VISIBLE);
        holder.countTextView.setText(ingredientItem.getQuantity());
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView textView;
        TextView countTextView;
        OnIngredientItemListener onIngredientItemListener;

        public ViewHolderClass(@NonNull View itemView, OnIngredientItemListener onIngredientItemListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemListImageView);
            textView = itemView.findViewById(R.id.itemListTextView);
            countTextView = itemView.findViewById(R.id.itemListCountText);
            this.onIngredientItemListener = onIngredientItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onIngredientItemListener.onIngredientItemClick(getAbsoluteAdapterPosition());
        }
    }
    public interface OnIngredientItemListener {
        void onIngredientItemClick(int position);
    }
}