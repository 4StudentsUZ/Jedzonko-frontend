package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Ingredient;
import com.fourstudents.jedzonko.Database.Entities.Shopitem;
import com.fourstudents.jedzonko.Database.Entities.ShopitemProductCrossRef;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ShopitemDao {
    @Insert(onConflict = REPLACE)
    void insert(Shopitem shopitem);

    @Delete
    void delete (Shopitem shopitem);

    @Update
    void update (Shopitem shopitem);

    @Query("SELECT * FROM shopitem")
    List<Ingredient> getAll();

    @Query("SELECT shopitemId FROM shopitem ORDER BY shopitemId DESC LIMIT 1")
    public int getLastId();

    @Insert
    void insertShopitemWithProduct(ShopitemProductCrossRef shopitemProductCrossRef);
}



