package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Recipe;
import com.fourstudents.jedzonko.Database.Entities.RecipeProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.ShoppingProductCrossRef;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithProducts;
import com.fourstudents.jedzonko.Database.Relations.RecipesWithTags;
import com.fourstudents.jedzonko.Database.Relations.ShoppingsWithProducts;

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

    @Query("SELECT recipeId FROM recipe ORDER BY recipeId DESC LIMIT 1")
    public int getLastId();

    @Transaction
    @Query("SELECT * FROM recipe")
    List<Recipe> getAll();

    @Insert
    void insertRecipeWithProduct(RecipeProductCrossRef recipeProductCrossRef);
    @Transaction
    @Query("SELECT * FROM Recipe")
    public List<RecipesWithProducts> getRecipesWithProducts();
}
