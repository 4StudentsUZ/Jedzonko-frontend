package com.fourstudents.jedzonko.Adapters.ShoppingList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Recipe.RecipeAdapter;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> implements Filterable  {
    List<Shopping> listOfShoppingList;
    List<Shopping> listOfShoppingListFull;
    private final OnShoppingListListener onShoppingListListener;

    public ShoppingAdapter(OnShoppingListListener onShoppingListListener) {

        listOfShoppingListFull = new ArrayList<>();
        listOfShoppingList = new ArrayList<>();
        this.onShoppingListListener=onShoppingListListener;
    }



    @NonNull
    @Override
    public ShoppingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new ShoppingAdapter.ViewHolder(view, onShoppingListListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingAdapter.ViewHolder viewHolder, final int position) {
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
            listOfShoppingListFull = new ArrayList<>(listOfShoppingList);
            notifyDataSetChanged();

//        }
    }

    @Override
    public int getItemCount() {
        return listOfShoppingList == null ? 0 : listOfShoppingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private final TextView textView;
        private final ImageView deleteImageView;
        OnShoppingListListener onShoppingListListener;

        public ViewHolder(View view, OnShoppingListListener onShoppingListListener) {
            super(view);
            // Define click listener for the ViewHolder's View
            deleteImageView = view.findViewById(R.id.itemListDeleteView);
            textView = view.findViewById(R.id.itemListTextView);
            this.onShoppingListListener = onShoppingListListener;
            itemView.setOnClickListener(this);

            deleteImageView.setOnClickListener(this);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return deleteImageView;
        }

        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.itemListDeleteView)
            {
                onShoppingListListener.onShoppingListDeleteClick(getAbsoluteAdapterPosition());
            }else{
                onShoppingListListener.onShoppingListClick(getAbsoluteAdapterPosition());
            }

        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Shopping> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(listOfShoppingListFull);
            }else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Shopping item : listOfShoppingListFull)
                {
                    if(item.getName().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {


            listOfShoppingList.clear();
            listOfShoppingList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public Shopping getShoppingList(int position){
        return listOfShoppingList.get(position);
    }

    public interface OnShoppingListListener {
        void onShoppingListDeleteClick(int position);
        void onShoppingListClick(int position);
    }


}