package com.fourstudents.jedzonko.Fragments.Search;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Fragments.Recipe.AddRecipeFragment;
import com.fourstudents.jedzonko.Fragments.Recipe.ShowRecipeFragment;
import com.fourstudents.jedzonko.Fragments.Recipe.ShowRemoteRecipeFragment;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.Network.Responses.RecipeResponse;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Recipe.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements RecipeAdapter.OnRecipeListener, Callback<List<RecipeResponse>> {

    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    RecyclerView recipeRV;
    RecipeAdapter recipeAdapter;
    RecipeViewModel recipeViewModel;
    JedzonkoService api;
    List<RecipeResponse> remoteRecipes = new ArrayList<>();
    List<Recipe> recipes = new ArrayList<>();

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Przepisy");
        toolbar.inflateMenu(R.menu.recipes);
        toolbar.getMenu();

        MenuItem search = toolbar.getMenu().findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    }
    @Override
    public void onResume() {
        super.onResume();
        initToolbar(requireView());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle(R.string.title_search);
        recipeRV = view.findViewById(R.id.recipeRV);
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeAdapter = new RecipeAdapter(this);
        recipeRV.setAdapter(recipeAdapter);
        recipeRV.setLayoutManager(new LinearLayoutManager(getContext()));
        api = ((MainActivity) requireActivity()).api;
        Call<List<RecipeResponse>> call = api.getRecipes();
        call.enqueue(this);

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
        ShowRemoteRecipeFragment showRemoteRecipeFragment = new ShowRemoteRecipeFragment();

        Bundle recipeBundle = new Bundle();
        for (RecipeResponse remoteRecipe: remoteRecipes) {
            if(remoteRecipe.getId()==recipeAdapter.getRecipe(position).getRecipeId()) recipeBundle.putSerializable("remoteRecipe", remoteRecipe);
        }

        showRemoteRecipeFragment.setArguments(recipeBundle);
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFrameLayout, showRemoteRecipeFragment, "ShowRemoteRecipeFragment")
                .addToBackStack("ShowRemoteRecipeFragment")
                .commit();
    }

    @Override
    public void onResponse(Call<List<RecipeResponse>> call, Response<List<RecipeResponse>> response) {
        remoteRecipes.addAll(response.body());
        //Toast.makeText(getContext(),"Załadowano",Toast.LENGTH_SHORT).show();
        convertRecipes();
        recipeAdapter.setRecipeList(recipes);
    }

    @Override
    public void onFailure(Call<List<RecipeResponse>> call, Throwable t) {

    }
    private  void convertRecipes(){
        for (RecipeResponse remoteRecipe: remoteRecipes) {
            Recipe recipe = new Recipe();
            recipe.setTitle(remoteRecipe.getTitle());
            recipe.setDescription(remoteRecipe.getDescription());
            recipe.setRecipeId(remoteRecipe.getId());
            recipes.add(recipe);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        remoteRecipes.clear();
        recipes.clear();
    }
}