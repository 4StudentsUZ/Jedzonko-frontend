package com.fourstudents.jedzonko;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithRecipes;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.util.ArrayList;
import java.util.List;


public class AddRecipeFragment extends Fragment {
    RecyclerView recyclerView;
    public AddRecipeFragment(){super(R.layout.fragment_add_recipe);}
    RoomDB database;
    List<Product> productList = new ArrayList<>();
    List<Product> ingredientList = new ArrayList<>();
    ProductRecyclerViewAdapter adapter1;
    ProductRecyclerViewAdapter adapter;
    Button addIngredientButton;
    Dialog dialog;
    RecyclerView recyclerView1;
    EditText title;
    EditText description;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Dodaj przepis");
        toolbar.inflateMenu(R.menu.add_recipe);
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

                if(item.getItemId()==R.id.action_save_note)
                {
                    actionSaveRecipe();
                }


                return false;
            }
        });

        addIngredientButton = (Button) view.findViewById(R.id.addIngredientButton);
        database = RoomDB.getInstance(getActivity());
        title = view.findViewById(R.id.editTextTitle);
        description = view.findViewById(R.id.editTextDescription);
        recyclerView1 = view.findViewById(R.id.recyclerView);


        int width = WindowManager.LayoutParams.MATCH_PARENT;
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_ingredient);
        dialog.getWindow().setLayout(width,height);
        recyclerView=dialog.findViewById(R.id.recyclerViewProduct);

        productList.addAll(database.productDao().getAll());
        adapter= new ProductRecyclerViewAdapter(getContext(), productList, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                Button addProductButton = dialog.findViewById(R.id.addProductButton);
                Button addIngredientsButton = dialog.findViewById(R.id.addIngredientsButton);
                addIngredientsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ingredientList=adapter.ingredientList;
                        adapter1= new ProductRecyclerViewAdapter(getContext(),ingredientList, true);
                        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView1.setAdapter(adapter1);
                        recyclerView.setAdapter(adapter);
                        dialog.dismiss();
                    }
                });
                addProductButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(((ViewGroup) getView().getParent()).getId(), new AddProductFragment(), "AddProductFragment")
                                .replace(R.id.mainFrameLayout, new AddProductFragment(), "AddProductFragment")
                                .addToBackStack("AddProductFragment")
                                .commit();
                        dialog.dismiss();
                        productList.clear();
                    }
                });
            }
        });


    }


    boolean checkData(){
        if(title.getText().toString().equals("") || description.getText().toString().equals("")|| ingredientList.size()==0 ){
            Toast.makeText(getContext(),"Nie wprowadzono wszystkich danych", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void actionSaveRecipe() {

        if (checkData()) {
            byte data[] = {0x0F, 0x10, 0x0F, 0x11};

            Recipe recipe = new Recipe();
            recipe.setTitle(title.getText().toString().trim());
            recipe.setDescription(description.getText().toString().trim());
            recipe.setData(data);
            recipe.setAuthor("Me");
            database.recipeDao().insert(recipe);
            int recipeId = database.recipeDao().getLastId();
            int size = ingredientList.size();

            for (int i = 0; i < size; i++) {
                Ingredient ingredient = new Ingredient();
                ingredient.setProductId(ingredientList.get(i).getProductId());
                ingredient.setRecipeId(recipeId);
                database.ingredientDao().insert(ingredient);
            }
            title.setText("");
            description.setText("");
            ingredientList.clear();
            adapter1.notifyItemRangeRemoved(0, size);
            Toast.makeText(getContext(), "Dodano przepis", Toast.LENGTH_SHORT).show();
        }
    }
}

