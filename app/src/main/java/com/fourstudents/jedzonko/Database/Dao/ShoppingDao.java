package com.fourstudents.jedzonko.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Relations.ShoppingWithShopitemsAndProducts;

import java.util.List;

@Dao
public interface ShoppingDao {
    @Insert
    void insert(Shopping shopping);

    @Delete
    void delete(Shopping shopping);

    @Update
    void update(Shopping shopping);

    @Query("SELECT shoppingId FROM Shopping ORDER BY shoppingId DESC LIMIT 1")
    int getLastId();

    @Transaction
    @Query("SELECT * FROM Shopping")
    List<Shopping> getAll();

    @Transaction
    @Query("SELECT * FROM Shopping")
    LiveData<List<Shopping>> getAllLiveData();

    @Transaction
    @Query("SELECT * FROM Shopping")
    List<ShoppingWithShopitemsAndProducts> getShoppingsWithShopitemsAndProducts();

    @Query("DELETE FROM shopitem WHERE shoppingOwnerId = :shoppingOwnerId")
    public void deleteShopitems(long shoppingOwnerId);

}
