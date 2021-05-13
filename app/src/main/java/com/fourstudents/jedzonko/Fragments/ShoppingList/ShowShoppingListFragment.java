package com.fourstudents.jedzonko.Fragments.ShoppingList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Recipe.ShowIngredientItemAdapter;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Entities.Tag;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipeWithIngredientsAndProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;
import com.fourstudents.jedzonko.Database.Relations.ShopitemsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.ShoppingWithShopitemsAndProducts;
import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Recipe.EditRecipeFragment;
import com.fourstudents.jedzonko.Other.IngredientItem;
import com.fourstudents.jedzonko.R;

import java.util.ArrayList;
import java.util.List;

public class ShowShoppingListFragment extends Fragment {
    RoomDB database;
    ShowIngredientItemAdapter showIngredientItemAdapter;
    List<IngredientItem> ingredientItemList = new ArrayList<>();
    Shopping shopping;

    public ShowShoppingListFragment(){super(R.layout.fragment_show_recipe);}

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Lista zakupów");
        toolbar.inflateMenu(R.menu.show_recipe);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_shopping_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view);
        database = RoomDB.getInstance(getActivity());
        showIngredientItemAdapter = new ShowIngredientItemAdapter(getContext());
        RecyclerView ingredientRV = view.findViewById(R.id.showShoppingListProductsRV);
        Bundle bundle = getArguments();
        shopping = (Shopping) bundle.getSerializable("shoppingList");
        getShoppingListData(shopping, view);
        TextView recipeTitle = view.findViewById(R.id.showShoppingListTitle);
        recipeTitle.setText(shopping.getName());
        ingredientRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientRV.setAdapter(showIngredientItemAdapter);
        showIngredientItemAdapter.submitList(ingredientItemList);

    }

    public void getShoppingListData(Shopping shopping, View view){
        List<ShoppingWithShopitemsAndProducts> shoppingWithShopitemsAndProducts =  database.shoppingDao().getShoppingsWithShopitemsAndProducts();
        for(ShoppingWithShopitemsAndProducts list : shoppingWithShopitemsAndProducts){
            if(list.shopping.getShoppingId() == shopping.getShoppingId()){
                for(ShopitemsWithProducts shopitemsWithProducts: list.shopitems)
                {
                    IngredientItem ingredientItem = new IngredientItem();
                    ingredientItem.quantity = shopitemsWithProducts.shopitem.getQuantity();
                    for(Product product: shopitemsWithProducts.products){
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


}
