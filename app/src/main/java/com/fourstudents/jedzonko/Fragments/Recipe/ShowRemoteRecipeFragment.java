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
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.Responses.RecipeResponse;
import com.fourstudents.jedzonko.Other.Ingredient;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ShowRemoteRecipeFragment extends Fragment {
    RatingBar ratingBar;
    Button rateButton;
    RecipeResponse remoteRecipe;
    ShowIngredientItemAdapter showIngredientItemAdapter;
    List<IngredientItem> ingredientItemList = new ArrayList<>();
    public ShowRemoteRecipeFragment() {super(R.layout.fragment_show_remote_recipe);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Przepis");
        toolbar.inflateMenu(R.menu.show_recipe);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getMenu().getItem(1).setVisible(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireFragmentManager().popBackStack();
            }
        });
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_remote_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        remoteRecipe= (RecipeResponse) bundle.getSerializable("remoteRecipe");
        initToolbar(view);
        ratingBar = view.findViewById(R.id.ratingBar);
        TextView recipeTitle = view.findViewById(R.id.showRecipeTitle);
        TextView recipeDescription = view.findViewById(R.id.showRecipeDescription);
        ImageView recipeImage = view.findViewById(R.id.imageView);
        recipeTitle.setText(remoteRecipe.getTitle());
        recipeDescription.setText(remoteRecipe.getDescription());

        RecyclerView ingredientRV = view.findViewById(R.id.showRecipeIngredientRV);
        showIngredientItemAdapter = new ShowIngredientItemAdapter(getContext());

        for (Ingredient ingredient:remoteRecipe.getIngredients()) {
            IngredientItem ingredientItem = new IngredientItem();
            Product product = new Product();
            product.setName(ingredient.getName());
            ingredientItem.setProduct(product);
            ingredientItemList.add(ingredientItem);
        }

        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(showIngredientItemAdapter);
        showIngredientItemAdapter.submitList(ingredientItemList);

        byte[] decoded = Base64.getDecoder().decode(remoteRecipe.getImage());
        Bitmap recipePhoto = BitmapFactory.decodeByteArray(decoded,0,decoded.length);
        recipeImage.setImageBitmap(recipePhoto);

        rateButton = view.findViewById(R.id.rateButton);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ratingBar.getRating()==0){
                    Toast.makeText(getContext(),"Brak oceny", Toast.LENGTH_SHORT).show();
                }else{
                    String r= String.valueOf(ratingBar.getRating());
                    Toast.makeText(getContext(),"Oceniłeś na: " +r, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}