package com.fourstudents.jedzonko;

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

import com.fourstudents.jedzonko.Database.Entities.Product;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ProductRecyclerViewAdapter extends ListAdapter<Product, ProductRecyclerViewAdapter.ViewHolderClass> {
    Context context;
    boolean showTrashIcon;
    private OnProductListener onProductListener;

    public ProductRecyclerViewAdapter(Context context, OnProductListener onProductListener) {
        super(new DiffUtil.ItemCallback<Product>() {
            @Override
            public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                return false;
            }
        });

        this.context = context;
        this.onProductListener = onProductListener;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view, onProductListener);
        viewHolderClass.addImageView.setVisibility(INVISIBLE);
            return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_recipes);
        Product product = getItem(position);
        holder.textView.setText(product.getName());
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        ImageView addImageView;
        OnProductListener onProductListener;

        public ViewHolderClass(@NonNull View itemView, OnProductListener onProductListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemListImageView);
            textView = itemView.findViewById(R.id.itemListTextView);
            addImageView = itemView.findViewById(R.id.itemListAddView);
            this.onProductListener = onProductListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onProductListener.onProductClick(getBindingAdapter(), getAbsoluteAdapterPosition());
            if(addImageView.getVisibility()==VISIBLE){
                addImageView.setVisibility(INVISIBLE);
            }else  addImageView.setVisibility(VISIBLE);

        }
    }

    public interface OnProductListener {
        void onProductClick(RecyclerView.Adapter adapter, int position);
    }

}
