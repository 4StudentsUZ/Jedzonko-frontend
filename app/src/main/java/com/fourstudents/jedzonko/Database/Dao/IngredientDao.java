package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithProducts;
import com.fourstudents.jedzonko.Database.Relations.IngredientsWithRecipes;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface IngredientDao {
    @Insert(onConflict = REPLACE)
    void insert(Ingredient ingredient);

    @Delete
    void delete(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Transaction
    @Query("SELECT * FROM ingredient")
    public List<IngredientsWithProducts> getIngredientsWithProducts();

    @Transaction
    @Query("SELECT * FROM ingredient")
    public List<IngredientsWithRecipes> getIngredientsWithRecipes();
}
