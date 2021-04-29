package com.fourstudents.jedzonko.ViewModels.Recipe;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Repositories.Recipe.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {
    private final RecipeRepository repository;
    private final LiveData<List<Recipe>> allLiveDataRecipeList;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        repository = new RecipeRepository(application);
        allLiveDataRecipeList = repository.getAllLiveDataRecipeList();
    }

    public LiveData<List<Recipe>> getAllLiveDataRecipeList() {
        return allLiveDataRecipeList;
    }

    public void insert(Recipe recipe) {
        repository.insert(recipe);
    }
}
