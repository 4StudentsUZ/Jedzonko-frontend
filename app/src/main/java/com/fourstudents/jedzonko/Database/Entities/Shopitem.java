package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopitem")
public class Shopitem {
    @PrimaryKey(autoGenerate = true)
    private long shopitemId;

    private long shoppingOwnerId;

    @ColumnInfo(name = "quantity")
    private String quantity;

    public long getShoppingOwnerId() {
        return shoppingOwnerId;
    }

    public void setShoppingOwnerId(long shoppingOwnerId) {
        this.shoppingOwnerId = shoppingOwnerId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public long getShopitemId() {
        return shopitemId;
    }

    public void setShopitemId(long shopitemId) {
        this.shopitemId = shopitemId;
    }
}