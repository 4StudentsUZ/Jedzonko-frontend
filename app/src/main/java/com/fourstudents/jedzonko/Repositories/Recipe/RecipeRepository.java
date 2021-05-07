package com.fourstudents.jedzonko.Repositories.Recipe;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.fourstudents.jedzonko.Database.Dao.RecipeDao;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.util.List;

public class RecipeRepository {
    private final RecipeDao recipeDao;
    private final LiveData<List<Recipe>> allLiveDataRecipeList;

    private final List<RecipesWithTags> recipesWithTagsRecipeList;

    public RecipeRepository(Application application) {
        RoomDB db = RoomDB.getInstance(application);
        recipeDao = db.recipeDao();
        allLiveDataRecipeList = recipeDao.getAllLiveData();
        recipesWithTagsRecipeList = recipeDao.getRecipesWithTags();
    }

    public LiveData<List<Recipe>> getAllLiveDataRecipeList() {
        return allLiveDataRecipeList;
    }

    public List<RecipesWithTags> getRecipesWithTagsRecipeList() {
        return recipesWithTagsRecipeList;
    }

    public void insert(Recipe recipe) {
        RoomDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                recipeDao.insert(recipe);
            }
        });
    }
}
