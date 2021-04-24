package com.fourstudents.jedzonko.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> implements Filterable {
    Context context;
    private List<Recipe> recipeList;
    private List<Recipe> recipeListFull;

    public RecipeRecyclerViewAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
        recipeListFull = new ArrayList<>(recipeList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Recipe recipe = recipeList.get(position);
        viewHolder.getTextView().setText(recipe.getTitle());

//        viewHolder.getImageView().setImageBitmap(recipe.getData());

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            // Define click listener for the ViewHolder's View
            imageView = (ImageView) view.findViewById(R.id.itemListImageView);
            textView = (TextView) view.findViewById(R.id.itemListTextView);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(recipeListFull);
            }else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Recipe item : recipeListFull)
                {
                    if(item.getTitle().toLowerCase().contains(filterPattern))
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
            recipeList.clear();
            recipeList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
