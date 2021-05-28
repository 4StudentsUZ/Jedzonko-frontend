package com.fourstudents.jedzonko.Fragments.Search;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fourstudents.jedzonko.Adapters.Recipe.RecipeAdapter;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Fragments.Recipe.AddRecipeFragment;
import com.fourstudents.jedzonko.Fragments.Recipe.ShowRemoteRecipeFragment;
import com.fourstudents.jedzonko.MainActivity;
import com.fourstudents.jedzonko.Network.JedzonkoService;
import com.fourstudents.jedzonko.Network.Responses.RecipeResponse;
import com.fourstudents.jedzonko.Other.Sorting.SortDialogFactory;
import com.fourstudents.jedzonko.Other.Sorting.SortListener;
import com.fourstudents.jedzonko.Other.Sorting.SortOrder;
import com.fourstudents.jedzonko.Other.Sorting.SortProperty;
import com.fourstudents.jedzonko.R;
import com.fourstudents.jedzonko.ViewModels.Recipe.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements RecipeAdapter.OnRecipeListener, Callback<List<RecipeResponse>>, SortListener {
    public SearchFragment() {
        super(R.layout.fragment_search);
    }

    RecyclerView recipeRV;
    RecipeAdapter recipeAdapter;
    RecipeViewModel recipeViewModel;
    JedzonkoService api;
    List<RecipeResponse> remoteRecipes = new ArrayList<>();
    List<Recipe> recipes = new ArrayList<>();

    private String queryText = "";
    private SortProperty sortProperty = SortProperty.Nothing;
    private SortOrder sortOrder = SortOrder.Ascending;

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.custom_toolbar);
        toolbar.setTitle("Wyszukaj przepisy");
        toolbar.inflateMenu(R.menu.recipes_remote);
        toolbar.getMenu();

        MenuItem search = toolbar.getMenu().findItem(R.id.action_search);

        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryText = query;
                sendQuery();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            queryText = "";
            sendQuery();
            return false;
        });

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_sort:
                    SortDialogFactory.getSortDialog(requireContext(), getLayoutInflater(), sortProperty, sortOrder, SearchFragment.this);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        });

        if (!queryText.isEmpty()) {
            searchView.setQuery(queryText, false);
        }
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
        sendQuery();
    }

    @Override
    public void onRecipeClick(int position) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ShowRemoteRecipeFragment showRemoteRecipeFragment = new ShowRemoteRecipeFragment();

        Bundle recipeBundle = new Bundle();
        for (RecipeResponse remoteRecipe : remoteRecipes) {
            if (remoteRecipe.getId() == recipeAdapter.getRecipe(position).getRecipeId())
                recipeBundle.putSerializable("remoteRecipe", remoteRecipe);
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
        if (response.body() == null) return;

        remoteRecipes = new ArrayList<>(response.body());
        convertRecipes();
        recipeAdapter.setRecipeList(recipes);
    }

    @Override
    public void onFailure(Call<List<RecipeResponse>> call, Throwable t) {
    }

    private void convertRecipes() {
        recipes.clear();
        for (RecipeResponse remoteRecipe : remoteRecipes) {
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

    @Override
    public void onSortingChanged(SortProperty sortProperty, SortOrder sortOrder) {
        this.sortProperty = sortProperty;
        this.sortOrder = sortOrder;
        sendQuery();
    }

    private void sendQuery() {
        String sortString = "";
        String directionString = "";

        switch (sortProperty) {
            case Nothing:
                sortString = "";
                break;
            case Title:
                sortString = "title";
                break;
            case Rating:
                sortString = "rating";
                break;
            case CreationDate:
                sortString = "creationDate";
                break;
        }

        switch (sortOrder) {
            case Ascending:
                directionString = "asc";
                break;
            case Descending:
                directionString = "desc";
                break;
        }

        Call<List<RecipeResponse>> call = api.queryRecipes(queryText, sortString, directionString);
        call.enqueue(SearchFragment.this);
    }
}