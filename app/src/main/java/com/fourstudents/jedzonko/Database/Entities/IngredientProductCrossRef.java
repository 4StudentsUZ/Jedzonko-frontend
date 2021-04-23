package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.Entity;

@Entity(tableName = "IngredientProductCrossRef",primaryKeys = {"ingredientId", "productId"})
public class IngredientProductCrossRef {
    public long ingredientId;
    public long productId;

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}

