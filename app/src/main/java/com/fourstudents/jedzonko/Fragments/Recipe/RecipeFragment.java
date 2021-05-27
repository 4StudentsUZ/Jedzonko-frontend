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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Recipe.RecipeAdapter;

import com.fourstudents.jedzonko.Adapters.ShoppingList.ShoppingAdapter;
import com.fourstudents.jedzonko.Database.Entities.Recipe;

import com.fourstudents.jedzonko.Database.RoomDB;
import com.fourstudents.jedzonko.Fragments.Search.SearchFragment;
import com.fourstudents.jedzonko.Fragments.Shared.AddProductFragment;
import com.fourstudents.jedzonko.Other.Sorting.SortDialogFactory;
import com.fourstudents.jedzonko.Other.Sorting.SortListener;
import com.fourstudents.jedzonko.Other.Sorting.SortOrder;
import com.fourstudents.jedzonko.Other.Sorting.SortProperty;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Recipe.RecipeViewModel;
import com.fourstudents.jedzonko.ViewModels.ShoppingList.ShoppingViewModel;


import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment implements RecipeAdapter.OnRecipeListener, SortListener {

    public RecipeFragment() {
        super(R.layout.fragment_recipe);
    }

    RecyclerView recipeRV;
    RecipeAdapter recipeAdapter;
    RecipeViewModel recipeViewModel;

    private String queryText = "";
    private SortProperty sortProperty = SortProperty.Nothing;
    private SortOrder sortOrder = SortOrder.Ascending;

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
                queryText = newText;
                updateSearch();
                return false;
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_sort:
                    SortDialogFactory.getLocalSortDialog(requireContext(), getLayoutInflater(), sortProperty, sortOrder, RecipeFragment.this);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        });
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
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeAdapter = new RecipeAdapter(this);
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

    @Override
    public void onRecipeClick(int position) {
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ShowRecipeFragment showRecipeFragment = new ShowRecipeFragment();

        Bundle recipeBundle = new Bundle();
        Recipe recipe = recipeAdapter.getRecipe(position);
        recipeBundle.putSerializable("recipe", recipe);
        showRecipeFragment.setArguments(recipeBundle);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameLayout, showRecipeFragment, "ShowRecipeFragment")
                .addToBackStack("ShowRecipeFragment")
                .commit();
    }

    @Override
    public void onSortingChanged(SortProperty sortProperty, SortOrder sortOrder) {
        this.sortProperty = sortProperty;
        this.sortOrder = sortOrder;
        updateSearch();
    }

    private void updateSearch() {
        System.out.println(String.format("%s;%s;%s", queryText, sortProperty, sortOrder));
        recipeAdapter.getFilter().filter(String.format("%s;%s;%s", queryText, sortProperty, sortOrder));
    }
}

