package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "RecipeProductCrossRef", primaryKeys = {"productId", "recipeId"})
public class RecipeProductCrossRef {
    public long productId;
    public long recipeId;
    @ColumnInfo(name = "quantity")
    private String quantity;
    
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}