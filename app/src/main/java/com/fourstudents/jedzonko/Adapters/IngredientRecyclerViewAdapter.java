package com.fourstudents.jedzonko.Adapters;

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
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.R;

import java.util.List;

public class IngredientRecyclerViewAdapter extends RecyclerView.Adapter<IngredientRecyclerViewAdapter.ViewHolderClass> {
    Context context;
    List<Product> ingredientList;


    public IngredientRecyclerViewAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.ingredientList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_recipes);
        Product product = ingredientList.get(position);
        holder.textView.setText(product.getName());
        holder.editText.setVisibility(View.VISIBLE);
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                holder.editText.getText();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        ImageView addImageView;
        EditText editText;


        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemListImageView);
            textView = itemView.findViewById(R.id.itemListTextView);
            addImageView = itemView.findViewById(R.id.itemListAddView);
            editText = itemView.findViewById(R.id.itemListEditText);
        }

    }

    public void updateData(List<Product> ingredientList1) {
        ingredientList.clear();
        ingredientList.addAll(ingredientList1);
        notifyDataSetChanged();
    }
}

