package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.IngredientProductCrossRef;
import com.fourstudents.jedzonko.Database.Entities.Product;

import java.util.List;

public class IngredientsWithProducts {
    @Embedded
    public Ingredient ingredient;
    @Relation(
            parentColumn = "ingredientId",
            entityColumn = "productId",

            associateBy = @Junction(IngredientProductCrossRef.class)
    )
    public List<Product> products;
}

