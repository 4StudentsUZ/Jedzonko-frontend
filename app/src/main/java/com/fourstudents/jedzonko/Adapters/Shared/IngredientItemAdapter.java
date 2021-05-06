package com.fourstudents.jedzonko.Adapters.Shared;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;

public class IngredientItemAdapter extends ListAdapter<IngredientItem, IngredientItemAdapter.ViewHolderClass> {
    Context context;
    private final OnIngredientItemListener onIngredientItemListener;

    public IngredientItemAdapter(Context context, OnIngredientItemListener onIngredientItemListener) {
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
        this.onIngredientItemListener=onIngredientItemListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view, onIngredientItemListener);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_recipes);
        IngredientItem ingredientItem = getItem(position);
        holder.textView.setText(ingredientItem.product.getName());
        holder.editText.setVisibility(View.VISIBLE);
        holder.deleteImage.setVisibility(View.VISIBLE);
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onIngredientItemListener.onTextChange(position, s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                holder.editText.getText();
            }
        });
       if(ingredientItem.getQuantity()!=null && !ingredientItem.getQuantity().equals("")) holder.editText.setText(ingredientItem.getQuantity()); // <--- TUTAJ
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements  View.OnClickListener{
        ImageView imageView;
        TextView textView;
        ImageView deleteImage;
        EditText editText;
        OnIngredientItemListener onIngredientItemListener;


        public ViewHolderClass(@NonNull View itemView, OnIngredientItemListener onIngredientItemListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemListImageView);
            textView = itemView.findViewById(R.id.itemListTextView);
            editText = itemView.findViewById(R.id.itemListEditText);
            deleteImage = itemView.findViewById(R.id.itemListDeleteView);
            this.onIngredientItemListener = onIngredientItemListener;

            deleteImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onIngredientItemListener.onIngredientItemDeleteClick(getAbsoluteAdapterPosition());
        }
    }
    public interface OnIngredientItemListener {
        void onIngredientItemDeleteClick(int position);
        void onTextChange(int position, CharSequence s);
    }
}
