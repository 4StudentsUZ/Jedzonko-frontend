package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Recipe;

import java.util.List;

public class RecipeWithIngredientsAndProducts {
    @Embedded
    public Recipe recipe;
    @Relation(
            entity = Ingredient.class,
            parentColumn = "recipeId",
            entityColumn = "recipeOwnerId"
    )
    public List<IngredientsWithProducts> ingredients;
}

