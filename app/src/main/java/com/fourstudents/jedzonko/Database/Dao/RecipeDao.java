package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {
    @Insert(onConflict = REPLACE)
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Transaction

    @Query("SELECT * FROM recipe")
    List<Recipe> getAll();

//    @Query("SELECT * FROM recipe")
//    public List<RecipesWithTags> getRecipesWithTags();

    @Query("SELECT recipeId FROM recipe ORDER BY recipeId DESC")
    public int getLastId();

}
