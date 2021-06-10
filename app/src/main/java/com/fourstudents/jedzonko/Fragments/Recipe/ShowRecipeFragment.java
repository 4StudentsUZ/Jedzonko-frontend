
package com.fourstudents.jedzonko.Fragments.Recipe;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Shared.ShowIngredientItemAdapter;
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
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShowRecipeFragment extends Fragment implements Callback<ProductResponse>, ShowIngredientItemAdapter.OnIngredientItemListener {
    Dialog waitDialog;
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
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        toolbar.setOnMenuItemClickListener(clickedItem -> {

            if(clickedItem.getItemId()==R.id.action_edit_recipe)
            {
                FragmentTransaction ft =  getParentFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                EditRecipeFragment editRecipeFragment = new EditRecipeFragment();

                Bundle recipeBundle = new Bundle();
                long recipeId = recipe.getRecipeId();
                recipeBundle.putLong("recipeId", recipeId);
                editRecipeFragment.setArguments(recipeBundle);
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, editRecipeFragment, "EditRecipeFragment")
                        .addToBackStack("EditRecipeFragment")
                        .commit();
            }else if(clickedItem.getItemId()==R.id.action_share_recipe){
                try {
                    shareRecipeMain();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(clickedItem.getItemId()==R.id.action_delete_recipe){
                onDeleteRecipe();
            }
        return false;
    });
    }

    private void onDeleteRecipe() {
        androidx.appcompat.app.AlertDialog.Builder alertBuilder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());

        alertBuilder.setTitle(R.string.dialog_delete_recipe_title);
        alertBuilder.setMessage(R.string.dialog_delete_recipe_message);

        alertBuilder.setPositiveButton(R.string.delete_confirm, (dialog, which) -> {
            deleteRecipe();
        });

        alertBuilder.setNegativeButton(R.string.delete_not_confirm, (dialog, which) -> {
        });

        alertBuilder.show();
    }

    private void deleteRecipe() {
        database.recipeDao().deleteIngredients(recipe.getRecipeId());
        database.recipeDao().deleteTags(recipe.getRecipeId());
        database.recipeDao().delete(recipe);
        if (recipe.getRemoteId() != -1) {
            Call<Void> call = api.deleteRecipe(recipe.getRemoteId());
            openWaitDialog(call);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                    waitDialog.hide();
                    getParentFragmentManager().popBackStack();
                }

                @Override
                public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                    waitDialog.hide();
                    Toast.makeText(requireContext(), R.string.service_connect_error, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            getParentFragmentManager().popBackStack();
        }
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
        assert bundle != null;
        recipe= (Recipe) bundle.getSerializable("recipe");
        initToolbar(view);
        database = RoomDB.getInstance(getActivity());
        showIngredientItemAdapter = new ShowIngredientItemAdapter(getContext(), this);
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
        StringBuilder conctString = new StringBuilder();
        tagsJsonArray = new JsonArray();
        for(RecipesWithTags recipeWithTags : recipesWithTags){
            if(recipeWithTags.recipe.getRecipeId() == recipe.getRecipeId()){
                for(Tag tag: recipeWithTags.tags){
                    conctString.append(" ").append(tag.getName());
                    tagsJsonArray.add(tag.getName());
                }
            }
        }
        recipeTags.setText(conctString.toString());

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
            byte[] data;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                data = Base64.getEncoder().encode(ingredientItem.getProduct().getData());
            } else {
                data = android.util.Base64.encode(ingredientItem.getProduct().getData(), 0);
            }
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

        byte[] data;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            data = Base64.getEncoder().encode(recipe.getData());
        } else {
            data = android.util.Base64.encode(recipe.getData(), 0);
        }
        object.addProperty("image", new String(data));

        Call<RecipeResponse> call = api.addRecipe(object);
        openWaitDialog(call);
        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(@NotNull Call<RecipeResponse> call, @NotNull Response<RecipeResponse> response) {
                waitDialog.hide();
                if (response.isSuccessful()) {
                    assert response.body() != null;
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
            public void onFailure(@NotNull Call<RecipeResponse> call, @NotNull Throwable t) {
                waitDialog.hide();
                t.printStackTrace();
                Toast.makeText(requireContext(), R.string.service_connect_error, Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onResponse(@NotNull Call<ProductResponse> call, Response<ProductResponse> response) {
        if (response.isSuccessful()) {
            assert response.body() != null;
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
    public void onFailure(@NotNull Call<ProductResponse> call, @NotNull Throwable t) {
        Toast.makeText(requireContext(), R.string.service_connect_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onIngredientItemClick(int position) {

    }

    private void openWaitDialog(Call call) {
        if (waitDialog == null) {
            waitDialog = new Dialog(requireContext());
            waitDialog.setContentView(R.layout.dialog_loading);
            waitDialog.setCanceledOnTouchOutside(true);
            waitDialog.getWindow()
                    .setLayout(
                            WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT
                    );
        }

        waitDialog.setOnDismissListener(dialog -> call.cancel());
        waitDialog.setOnCancelListener(dialog -> call.cancel());

        waitDialog.show();
    }
}