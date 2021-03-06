package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Shopitem;
import com.fourstudents.jedzonko.Database.Entities.ShopitemProductCrossRef;

import java.util.List;

@Dao
public interface ShopitemDao {
    @Insert
    void insert(Shopitem shopitem);

    @Delete
    void delete (Shopitem shopitem);

    @Update
    void update (Shopitem shopitem);

    @Query("SELECT * FROM shopitem")
    List<Shopitem> getAll();

    @Query("SELECT shopitemId FROM shopitem ORDER BY shopitemId DESC LIMIT 1")
    int getLastId();

    @Insert
    void insertShopitemWithProduct(ShopitemProductCrossRef shopitemProductCrossRef);
}



