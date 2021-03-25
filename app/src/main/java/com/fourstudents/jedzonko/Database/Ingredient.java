package com.fourstudents.jedzonko.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Set;

@Entity(tableName = "ingredient")
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    private long ingredientId;

    @ColumnInfo(name = "recipeId")
    private long recipeId;

    @ColumnInfo(name = "productId")
    private long productId;

    public long getId() {
        return ingredientId;
    }

    public void setId(long id) {
        this.ingredientId = ingredientId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProduct(Set<Product> product) {
        this.productId = productId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }
}
