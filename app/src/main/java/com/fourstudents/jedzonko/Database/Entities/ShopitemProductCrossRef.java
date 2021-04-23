package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.Entity;

@Entity(tableName="shopItemsProductCrossRef", primaryKeys = {"shopitemId", "productId"})
public class ShopitemProductCrossRef {
    public long shopitemId;
    public long productId;

    public long getShopitemId() {
        return shopitemId;
    }

    public void setShopitemId(long shopitemId) {
        this.shopitemId = shopitemId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }
}
