package com.fourstudents.jedzonko.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.RecipeTagCrossRef;
import com.fourstudents.jedzonko.Database.Relations.RecipeWithIngredientsAndProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;

import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * from recipe r WHERE remoteId = :remoteId LIMIT 1")
    Recipe findByRemoteId(Long remoteId);

    @Insert
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Query("SELECT recipeId FROM recipe ORDER BY recipeId DESC LIMIT 1")
    int getLastId();

    @Transaction
    @Query("SELECT * FROM recipe")
    List<Recipe> getAll();

    @Transaction
    @Query("SELECT * FROM Recipe")
    List<RecipeWithIngredientsAndProducts> getRecipesWithIngredientsAndProducts();

    @Transaction
    @Query("SELECT * FROM Recipe")
    List<RecipesWithTags> getRecipesWithTags();

    @Insert
    void insertRecipeWithTag(RecipeTagCrossRef recipeTagCrossRef);

    @Query("SELECT * FROM Recipe")
    LiveData<List<Recipe>> getAllLiveData();

    @Query("DELETE FROM ingredient WHERE recipeOwnerId = :recipeOwnerId")
    public void deleteIngredients(long recipeOwnerId);

    @Query("DELETE FROM RecipeTagCrossRef WHERE recipeId = :recipeId")
    public void deleteTags(long recipeId);



}
