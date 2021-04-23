package com.fourstudents.jedzonko;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Shopping;

import java.util.List;

public class ShoppingRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingRecyclerViewAdapter.ViewHolder>{
    List<Shopping> listOfShoppingList;

    public ShoppingRecyclerViewAdapter() {}

    @NonNull
    @Override
    public ShoppingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new ShoppingRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingRecyclerViewAdapter.ViewHolder viewHolder, final int position) {
        Shopping shopping = listOfShoppingList.get(position);
        viewHolder.getTextView().setText(shopping.getName());
//        viewHolder.deleteImageView.setVisibility(View.VISIBLE);
        viewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void setProductList(final List<Shopping> listOfShoppingList) {
//        if (this.listOfShoppingList == null) {
            this.listOfShoppingList = listOfShoppingList;
            notifyItemRangeInserted(0, listOfShoppingList.size());
//        }
    }

    @Override
    public int getItemCount() {
        return listOfShoppingList == null ? 0 : listOfShoppingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView deleteImageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            deleteImageView = view.findViewById(R.id.itemListDeleteView);
            textView = view.findViewById(R.id.itemListTextView);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return deleteImageView;
        }
    }


}