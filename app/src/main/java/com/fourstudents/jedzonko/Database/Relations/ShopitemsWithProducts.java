package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Shopitem;
import com.fourstudents.jedzonko.Database.Entities.ShopitemProductCrossRef;

import java.util.List;

public class ShopitemsWithProducts {
    @Embedded public Shopitem shopitem;
    @Relation(parentColumn = "shopitemId",
    entityColumn = "productId",
    associateBy = @Junction(ShopitemProductCrossRef.class)
    )
    public List<Product> products;
}
