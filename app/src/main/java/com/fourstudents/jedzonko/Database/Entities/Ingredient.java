package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredient")
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    public long ingredientId;

    public long recipeOwnerId;

    @ColumnInfo(name = "quantity")
    private String quantity;

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public long getRecipeId() {
        return recipeOwnerId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeOwnerId = recipeId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}