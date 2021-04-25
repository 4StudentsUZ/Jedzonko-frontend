package com.fourstudents.jedzonko.Adapters.Recipe;

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

import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.R;

public class RecipeTagAdapter extends ListAdapter<Tag, RecipeTagAdapter.ViewHolderClass> {
    Context context;
    private final RecipeTagAdapter.OnRecipeTagListener onRecipeTagListener;

    public RecipeTagAdapter(Context context, OnRecipeTagListener onRecipeTagListener) {
        super(new DiffUtil.ItemCallback<Tag>() {
            @Override
            public boolean areItemsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Tag oldItem, @NonNull Tag newItem) {
                return oldItem == newItem;
            }
        });
        this.onRecipeTagListener=onRecipeTagListener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeTagAdapter.ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        RecipeTagAdapter.ViewHolderClass viewHolderClass = new RecipeTagAdapter.ViewHolderClass(view, onRecipeTagListener);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeTagAdapter.ViewHolderClass holder, int position) {
        Tag tag = getItem(position);
        holder.textView.setText(tag.getName());
        holder.deleteImage.setVisibility(View.VISIBLE);
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView textView;
        ImageView deleteImage;
       OnRecipeTagListener onRecipeTagListener;


        public ViewHolderClass(@NonNull View itemView, OnRecipeTagListener onRecipeTagListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemListTextView);
            deleteImage = itemView.findViewById(R.id.itemListDeleteView);
            this.onRecipeTagListener =onRecipeTagListener;

            deleteImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           onRecipeTagListener.onRecipeTagDeleteClick(getAbsoluteAdapterPosition());
        }
    }
    public interface OnRecipeTagListener {
        void onRecipeTagDeleteClick(int position);
    }
}

