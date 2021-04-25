package com.fourstudents.jedzonko.Adapters.Shared;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolderClass> {
    Context context;
    List<Product> productList;
    private final OnProductListener onProductListener;

    public ProductAdapter(Context context, List<Product> productList, OnProductListener onProductListener) {
        this.context = context;
        this.productList = productList;
        this.onProductListener = onProductListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view, onProductListener);
            return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_recipes);
        Product product = productList.get(position);
        holder.textView.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        OnProductListener onProductListener;

        public ViewHolderClass(@NonNull View itemView, OnProductListener onProductListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemListImageView);
            textView = itemView.findViewById(R.id.itemListTextView);
            this.onProductListener = onProductListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProductListener.onProductClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnProductListener {
        void onProductClick(int position);
    }

}
