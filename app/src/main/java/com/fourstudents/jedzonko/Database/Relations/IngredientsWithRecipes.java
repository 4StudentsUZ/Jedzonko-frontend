package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Ingredient;

import java.util.List;

public class IngredientsWithRecipes {
    @Embedded public Ingredient ingredient;
    @Relation(parentColumn = "recipeId", entityColumn = "recipeId")
    public List<Ingredient> ingredients;
}
