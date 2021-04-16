package com.fourstudents.jedzonko;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolderClass> {
    Context context;
    List<Product> productList;
    List<Product> ingredientList = new ArrayList<>();
    boolean showTrashIcon;

    public ProductRecyclerViewAdapter(Context context, List<Product> productList, boolean showTrashIcon) {
        this.context = context;
        this.productList = productList;
        this.showTrashIcon = showTrashIcon;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        viewHolderClass.addImageView.setVisibility(View.INVISIBLE);
        if(showTrashIcon) view.findViewById(R.id.itemListDeleteView).setVisibility(View.VISIBLE);

            return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_recipes);
        Product product = productList.get(position);
        holder.textView.setText(product.getName());
        if(ingredientList.contains(product)) holder.addImageView.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showTrashIcon) {
                    Product ingriedient = productList.get(holder.getAbsoluteAdapterPosition());
                    if(ingredientList.contains(ingriedient)){
                        ingredientList.remove(ingriedient);
                        holder.addImageView.setVisibility(View.INVISIBLE);
                    }else{
                        ingredientList.add(ingriedient);
                        holder.addImageView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                Product product = productList.get(position);
                if (ingredientList.contains(product)) {
                    holder.addImageView.setVisibility(View.INVISIBLE);
                }
                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productList.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        ImageView deleteImageView;
        ImageView addImageView;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemListImageView);
            textView = itemView.findViewById(R.id.itemListTextView);
            deleteImageView = itemView.findViewById(R.id.itemListDeleteView);
            addImageView = itemView.findViewById(R.id.itemListAddView);
        }
    }

}
