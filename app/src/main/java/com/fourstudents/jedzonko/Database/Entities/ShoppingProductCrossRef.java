package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "ShoppingProductCrossRef", primaryKeys = {"shoppingId", "productId"})
public class ShoppingProductCrossRef {
    public long shoppingId;
    public long productId;
    @ColumnInfo(name = "quantity")
    private String quantity;

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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

