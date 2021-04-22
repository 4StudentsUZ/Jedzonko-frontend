package com.fourstudents.jedzonko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Shopping;

import java.util.List;

public class ShoppingRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingRecyclerViewAdapter.ViewHolder>{
    Context context;
    private List<Shopping> shoppingListList;

    public ShoppingRecyclerViewAdapter(Context context, List<Shopping> shoppingListList) {
        this.context = context;
        this.shoppingListList = shoppingListList;
    }

    @NonNull
    @Override
    public ShoppingRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);

        return new ShoppingRecyclerViewAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingRecyclerViewAdapter.ViewHolder viewHolder, final int position) {
        Shopping shopping = shoppingListList.get(position);
        viewHolder.getTextView().setText(shopping.getName());
        viewHolder.deleteImageView.setVisibility(View.VISIBLE);


        viewHolder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return shoppingListList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView deleteImageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            deleteImageView = (ImageView) view.findViewById(R.id.itemListDeleteView);
            textView = (TextView) view.findViewById(R.id.itemListTextView);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return deleteImageView;
        }
    }


}