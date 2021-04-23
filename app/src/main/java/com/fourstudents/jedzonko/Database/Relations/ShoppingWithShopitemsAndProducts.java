package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Entities.Shopitem;
import com.fourstudents.jedzonko.Database.Entities.Shopping;

import java.util.List;

public class ShoppingWithShopitemsAndProducts {
    @Embedded public Shopping shopping;
    @Relation(entity = Shopitem.class,
    parentColumn = "shoppingId",
    entityColumn = "shoppingOwnerId")
    public List<ShopitemsWithProducts> shopitems;
}
