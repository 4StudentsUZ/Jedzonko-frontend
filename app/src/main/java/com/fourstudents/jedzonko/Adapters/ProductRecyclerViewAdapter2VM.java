package com.fourstudents.jedzonko.Adapters;

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

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ProductRecyclerViewAdapter2VM extends RecyclerView.Adapter<ProductRecyclerViewAdapter2VM.ViewHolderClass> {
    Context context;
    List<Product> productList;
    boolean showTrashIcon;
    private OnProductListener onProductListener;

//    public ProductRecyclerViewAdapter2VM(Context context, List<Product> productList, OnProductListener onProductListener) {
//        this.context = context;
//        this.productList = productList;
//        this.onProductListener = onProductListener;
//        notifyDataSetChanged();
//    }

    public ProductRecyclerViewAdapter2VM() {}

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
//        ViewHolderClass viewHolderClass = new ViewHolderClass(view, onProductListener);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);
        viewHolderClass.addImageView.setVisibility(INVISIBLE);
            return viewHolderClass;
    }

    public void setProductList(final List<Product> products) {
        if (productList == null) {
            productList = products;
            notifyItemRangeInserted(0, products.size());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        holder.imageView.setImageResource(R.drawable.ic_recipes);
        Product product = productList.get(position);
        holder.textView.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        ImageView addImageView;
//        OnProductListener onProductListener;

//        public ViewHolderClass(@NonNull View itemView, OnProductListener onProductListener) {
        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.itemListImageView);
            textView = itemView.findViewById(R.id.itemListTextView);
            addImageView = itemView.findViewById(R.id.itemListAddView);
//            this.onProductListener = onProductListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            onProductListener.onProductClick(getAbsoluteAdapterPosition());
            if(addImageView.getVisibility()==VISIBLE){
                addImageView.setVisibility(INVISIBLE);
            }else  addImageView.setVisibility(VISIBLE);

        }
    }

    public interface OnProductListener {
        void onProductClick(int position);
    }

}
