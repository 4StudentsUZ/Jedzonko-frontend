package com.fourstudents.jedzonko.Fragments.Recipe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourstudents.jedzonko.Adapters.Recipe.TagAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.IngredientItemAdapter;
import com.fourstudents.jedzonko.Adapters.Shared.ProductAdapter;
import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipeWithIngredientsAndProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;

import java.util.ArrayList;
import java.util.List;


public class ShowRecipeFragment extends Fragment implements  TagAdapter.OnTagListener, IngredientItemAdapter.OnIngredientItemListener {
    RoomDB database;
    IngredientItemAdapter ingredientItemAdapter;
    TagAdapter tagAdapter;
    List<IngredientItem> ingredientItemList = new ArrayList<>();
    List<Tag> tagList= new ArrayList<>();

    public ShowRecipeFragment(){super(R.layout.fragment_show_recipe);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Przepis");
        toolbar.inflateMenu(R.menu.show_recipe);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireFragmentManager().popBackStack();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.action_edit_recipe)
                {
                   // tutaj odpalić fragment edycji
                }
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_recipe, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        database = RoomDB.getInstance(getActivity());
        ingredientItemAdapter = new IngredientItemAdapter(getContext(), this);
        tagAdapter = new TagAdapter(getContext(), tagList, this );
        RecyclerView ingredientRV = view.findViewById(R.id.showRecipeIngredientRV);
        RecyclerView tagRV = view.findViewById(R.id.showRecipeTagRV);

        Bundle bundle = getArguments();
        Recipe recipe= (Recipe) bundle.getSerializable("recipe");
        TextView recipeTitle = view.findViewById(R.id.showRecipeTitle);
        TextView recipeDescription = view.findViewById(R.id.showRecipeDescription);
        ImageView recipeImage = view.findViewById(R.id.imageView);
        recipeTitle.setText(recipe.getTitle());
        recipeDescription.setText(recipe.getDescription());
        byte[] data = recipe.getData();
        Bitmap recipePhoto = BitmapFactory.decodeByteArray(data,0,data.length);
        recipeImage.setImageBitmap(recipePhoto);

        getRecipeData(recipe, view);

        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(ingredientItemAdapter);
        ingredientItemAdapter.submitList(ingredientItemList);
        tagRV.setLayoutManager(new LinearLayoutManager(getContext()));
        tagRV.setAdapter(tagAdapter);
    }

    public void getRecipeData(Recipe recipe, View view){
        List<RecipesWithTags> recipesWithTags = database.recipeDao().getRecipesWithTags();
        List<RecipeWithIngredientsAndProducts> recipesWithIngredientsAndProducts = database.recipeDao().getRecipesWithIngredientsAndProducts();
        for(RecipesWithTags recipeWithTags : recipesWithTags){
            if(recipeWithTags.recipe.getRecipeId() == recipe.getRecipeId()){
                for(Tag tag: recipeWithTags.tags){
                    tagList.add(tag);
                }
            }
        }

        for(RecipeWithIngredientsAndProducts recipeWithIngredientsAndProducts : recipesWithIngredientsAndProducts){
            if(recipeWithIngredientsAndProducts.recipe.getRecipeId() == recipe.getRecipeId()){
                for(IngredientsWithProducts ingredientsWithProducts: recipeWithIngredientsAndProducts.ingredients)
                {
                    IngredientItem ingredientItem = new IngredientItem();
                    ingredientItem.quantity = ingredientsWithProducts.ingredient.getQuantity();
                    for(Product product: ingredientsWithProducts.products){
                        ingredientItem.setProduct(product);
                        ingredientItemList.add(ingredientItem);
                    }
                }
            }
        }

    }

    @Override
    public void onIngredientItemDeleteClick(int position) {

    }

    @Override
    public void onTextChange(int position, CharSequence s) {

    }

    @Override
    public void onTagClick(int position) {

    }
}