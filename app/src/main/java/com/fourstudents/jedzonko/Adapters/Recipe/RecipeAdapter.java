package com.fourstudents.jedzonko.Adapters.Recipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.ShoppingList.ShoppingAdapter;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements Filterable {
    private List<Recipe> recipeList;
    private List<Recipe> recipeListFull;
    private final OnRecipeListener onRecipeListener;


    public RecipeAdapter(OnRecipeListener onRecipeListener) {
        recipeList = new ArrayList<>();
        recipeListFull = new ArrayList<>();
        this.onRecipeListener=onRecipeListener;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list, parent, false);
        return new RecipeAdapter.ViewHolder(view, onRecipeListener);

    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.ViewHolder viewHolder, final int position) {
        Recipe recipe = recipeList.get(position);
        viewHolder.getTextView().setText(recipe.getTitle());
        viewHolder.getTagView().setText(recipe.getDescription());

//        viewHolder.getTextView().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

       // viewHolder.getImageView().setImageBitmap(recipe.getData());

    }

    public void setRecipeList(final List<Recipe> recipeList) {

        this.recipeList = recipeList;
        recipeListFull = new ArrayList<>(recipeList);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        private final TextView textView;
        private final TextView tagView;
        private final ImageView imageView;
        OnRecipeListener onRecipeListener;

        public ViewHolder(View view, OnRecipeListener onRecipeListener) {
            super(view);

            // Define click listener for the ViewHolder's View
            imageView = (ImageView) view.findViewById(R.id.itemListImageView);
            textView = (TextView) view.findViewById(R.id.itemListTextView);
            tagView = (TextView) view.findViewById(R.id.itemListTagView);
            this.onRecipeListener = onRecipeListener;
            itemView.setOnClickListener(this);
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getTagView() {
            return tagView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        @Override
        public void onClick(View v) {
            onRecipeListener.onRecipeClick(getAbsoluteAdapterPosition());
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
    public Recipe getRecipe(int position){
        return recipeList.get(position);
    }

    public interface OnRecipeListener {
        void onRecipeClick(int position);
    }
}
