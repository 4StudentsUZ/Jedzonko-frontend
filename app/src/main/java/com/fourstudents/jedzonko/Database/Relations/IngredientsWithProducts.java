package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Product;

import java.util.List;

public class IngredientsWithProducts {
    @Embedded public Ingredient ingredient;
    @Relation(
            parentColumn = "ingredientId",
            entityColumn = "ingredientId"
    )
    public List<Product> products;
}
