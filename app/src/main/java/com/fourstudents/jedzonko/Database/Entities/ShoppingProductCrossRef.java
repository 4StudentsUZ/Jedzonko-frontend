package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.Entity;

@Entity(primaryKeys = {"shoppingId", "productId"})
public class ShoppingProductCrossRef {
    private long shoppingId;
    private long productId;

    public long getShoppingId() {
        return shoppingId;
    }

    public void setShoppingId(long shoppingId) {
        this.shoppingId = shoppingId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}

