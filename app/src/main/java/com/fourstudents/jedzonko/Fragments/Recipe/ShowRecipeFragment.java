package com.fourstudents.jedzonko.Fragments.Recipe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fourstudents.jedzonko.Adapters.Recipe.ShowIngredientItemAdapter;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipeWithIngredientsAndProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.Network.Responses.ProductResponse;
import com.fourstudents.jedzonko.Network.Responses.RecipeResponse;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShowRecipeFragment extends Fragment implements Callback<ProductResponse>{
    RoomDB database;
    ShowIngredientItemAdapter showIngredientItemAdapter;
    List<IngredientItem> ingredientItemList = new ArrayList<>();
    Recipe recipe;
    MainActivity activity;
    JedzonkoService api;
    List<Long> productsId = new ArrayList<>();
    JsonArray tagsJsonArray;

    public ShowRecipeFragment(){super(R.layout.fragment_show_recipe);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Przepis");
        toolbar.inflateMenu(R.menu.show_recipe);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        activity = ((MainActivity) requireActivity());
        if (activity.token.length() > 0 && recipe.getRemoteId()==-1) toolbar.getMenu().getItem(0).setVisible(true);
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
                    FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    EditRecipeFragment editRecipeFragment = new EditRecipeFragment();

                    Bundle recipeBundle = new Bundle();
                    long recipeId = recipe.getRecipeId();
                    recipeBundle.putLong("recipeId", recipeId);
                    editRecipeFragment.setArguments(recipeBundle);
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainFrameLayout, editRecipeFragment, "EditRecipeFragment")
                            .addToBackStack("EditRecipeFragment")
                            .commit();
                }else if(item.getItemId()==R.id.action_share_recipe){
                    try {
                        shareRecipeMain();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        Bundle bundle = getArguments();
        recipe= (Recipe) bundle.getSerializable("recipe");
        initToolbar(view);
        database = RoomDB.getInstance(getActivity());
        showIngredientItemAdapter = new ShowIngredientItemAdapter(getContext());
        api = ((MainActivity) requireActivity()).api;

        RecyclerView ingredientRV = view.findViewById(R.id.showRecipeIngredientRV);

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
        ingredientRV.setAdapter(showIngredientItemAdapter);
        showIngredientItemAdapter.submitList(ingredientItemList);
    }

    public void getRecipeData(Recipe recipe, View view){
        List<RecipesWithTags> recipesWithTags = database.recipeDao().getRecipesWithTags();
        List<RecipeWithIngredientsAndProducts> recipesWithIngredientsAndProducts = database.recipeDao().getRecipesWithIngredientsAndProducts();
        TextView recipeTags = view.findViewById(R.id.showRecipeTagsString);
        String conctString ="";
        tagsJsonArray = new JsonArray();
        for(RecipesWithTags recipeWithTags : recipesWithTags){
            if(recipeWithTags.recipe.getRecipeId() == recipe.getRecipeId()){
                for(Tag tag: recipeWithTags.tags){
                    conctString = conctString + "" + tag.getName();
                    tagsJsonArray.add(tag.getName());
                }
            }
        }
        recipeTags.setText(conctString);

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
    public void onPause() {
        super.onPause();
        ingredientItemList.clear();
    }

    private void shareRecipeMain() throws InterruptedException {
        for (IngredientItem ingredientItem: ingredientItemList) {
            JsonObject object = new JsonObject();
            object.addProperty("name", ingredientItem.getProduct().getName());
            object.addProperty("barcode", ingredientItem.getProduct().getBarcode());
            byte[] data = Base64.getEncoder().encode(ingredientItem.getProduct().getData());
            object.addProperty("image", new String(data));
            Call<ProductResponse> call = api.addProduct(object);
            call.enqueue(this);
        }

    }

    private void shareRecipe(){
        JsonObject object = new JsonObject();

            object.addProperty("title", recipe.getTitle());
            object.addProperty("description", recipe.getDescription());
            JsonArray ingredientArray = new JsonArray();
            for (Long id:productsId) {
                ingredientArray.add(id);
            }
            object.add("ingredients", ingredientArray);

            JsonArray quantityArray = new JsonArray();
            for (IngredientItem ingredientItem: ingredientItemList) {
                quantityArray.add(ingredientItem.getQuantity());
            }
            object.add("quantities", quantityArray);
            object.add("tags", tagsJsonArray);

            byte[] data = Base64.getEncoder().encode(recipe.getData());
            object.addProperty("image", new String(data));

        Call<RecipeResponse> call = api.addRecipe(object);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    recipe.setRemoteId(response.body().getId());
                    database.recipeDao().update(recipe);
                    Toast.makeText(requireContext(), "Udostępniono Przepis!", Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                } else if (response.errorBody() != null) {
                    try {
                        Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


    @Override
    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
        if (response.isSuccessful()) {
            productsId.add(response.body().getId());
            if(productsId.size()==ingredientItemList.size()){
                shareRecipe();
            }
        } else if (response.errorBody() != null) {
            try {
                Toast.makeText(requireContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFailure(Call<ProductResponse> call, Throwable t) {

    }
}