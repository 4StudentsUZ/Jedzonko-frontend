package com.fourstudents.jedzonko.Database.Relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Entities.ShoppingProductCrossRef;

import java.util.List;

public class ShoppingsWithProducts {
    @Embedded
    public Shopping shopping;
    @Relation(
            parentColumn = "shoppingId",
            entityColumn = "prodcutId",
            associateBy = @Junction(ShoppingProductCrossRef.class)
    )
    public List<Product> products;
}

