package com.fourstudents.jedzonko;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter{
    Context context;
    private List<Product> productList;
    List<Product> ingredientList= new ArrayList<>();
    boolean deleteVisibility;

    public ProductRecyclerViewAdapter(Context context, List<Product> productList, boolean deleteVisibility) {
        this.context = context;
        this.productList=productList;
        this.deleteVisibility=deleteVisibility;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        ViewHolderClass viewHolderClass= new ViewHolderClass(view);
        viewHolderClass.addImageView.setVisibility(View.INVISIBLE);
        if(deleteVisibility) view.findViewById(R.id.itemListDeleteView).setVisibility(View.VISIBLE);

            return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass)holder;
       viewHolderClass.imageView.setImageResource(R.drawable.ic_recipes);
        Product product = productList.get(position);
        viewHolderClass.textView.setText(product.getName());
        if(ingredientList.contains(product)) viewHolderClass.addImageView.setVisibility(View.VISIBLE);
            viewHolderClass.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!deleteVisibility) {
                    Product d = productList.get(viewHolderClass.getAbsoluteAdapterPosition());
                    if(ingredientList.contains(d)){
                        ingredientList.remove(d);
                        viewHolderClass.addImageView.setVisibility(View.INVISIBLE);
                    }else{
                        ingredientList.add(d);
                        viewHolderClass.addImageView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        viewHolderClass.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolderClass.getAbsoluteAdapterPosition();
                productList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,productList.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        ImageView deleteImageView;
        ImageView addImageView;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            imageView= (ImageView) itemView.findViewById(R.id.itemListImageView);
            textView = (TextView) itemView.findViewById(R.id.itemListTextView);
            deleteImageView = (ImageView) itemView.findViewById(R.id.itemListDeleteView);
            addImageView = (ImageView) itemView.findViewById(R.id.itemListAddView);
        }
    }

}
