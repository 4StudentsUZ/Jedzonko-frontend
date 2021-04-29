package com.fourstudents.jedzonko.Fragments.Recipe;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Recipe.RecipeAdapter;

import com.fourstudents.jedzonko.Adapters.ShoppingList.ShoppingAdapter;
import com.fourstudents.jedzonko.Database.Entities.Recipe;

import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Recipe.RecipeViewModel;
import com.fourstudents.jedzonko.ViewModels.ShoppingList.ShoppingViewModel;


import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    public RecipeFragment() {
        super(R.layout.fragment_recipe);
    }

    RecyclerView recipeRV;
    RecipeAdapter recipeAdapter;

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Przepisy");
        toolbar.inflateMenu(R.menu.recipes);
        toolbar.getMenu();

        MenuItem search = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recipeAdapter.getFilter().filter(newText);
                return false;
            }
        });

//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                if(item.getItemId()==R.id.action_search)
//                {
//
//                }
//
//                return false;
//            }
//        });
    }



    @Override
    public void onResume() {
        super.onResume();
        initToolbar(requireView());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipeRV = view.findViewById(R.id.recipeRV);
        RecipeViewModel recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeAdapter = new RecipeAdapter();
        recipeRV.setAdapter(recipeAdapter);
        recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeViewModel.getAllLiveDataRecipeList().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null) {
                    recipeAdapter.setRecipeList(recipes);
                }
            }
        });

        view.findViewById(R.id.floatingActionButton_add_recipe).setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, new AddRecipeFragment(), "AddRecipeFragment")
                        .addToBackStack("AddRecipeFragment")
                        .commit()
        );
    }

}

