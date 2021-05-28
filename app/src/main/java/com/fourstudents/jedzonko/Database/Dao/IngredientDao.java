package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.IngredientProductCrossRef;

import java.util.List;

@Dao
public interface IngredientDao {
    @Insert
    void insert(Ingredient ingredient);

    @Delete
    void delete (Ingredient ingredient);

    @Update
    void update (Ingredient ingredient);

    @Query("SELECT * FROM ingredient")
    List<Ingredient> getAll();

    @Query("SELECT ingredientId FROM ingredient ORDER BY ingredientId DESC LIMIT 1")
    int getLastId();

    @Insert
    void insertIngredientWithProduct(IngredientProductCrossRef ingredientProductCrossRef);

}
