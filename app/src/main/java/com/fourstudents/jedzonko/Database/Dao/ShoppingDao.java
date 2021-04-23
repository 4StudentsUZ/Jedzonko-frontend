package com.fourstudents.jedzonko.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.Entities.ShoppingProductCrossRef;
import com.fourstudents.jedzonko.Database.Relations.ShoppingsWithProducts;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ShoppingDao {
    @Insert(onConflict = REPLACE)
    void insert(Shopping shopping);

    @Delete
    void delete(Shopping shopping);

    @Update
    void update(Shopping shopping);

    @Insert
    void insertShoppingWithProduct(ShoppingProductCrossRef shoppingProductCrossRef);

    @Query("SELECT shoppingId FROM Shopping ORDER BY shoppingId DESC LIMIT 1")
    public int getLastId();

    @Transaction
    @Query("SELECT * FROM Shopping")
    List<ShoppingsWithProducts> getShoppingsWithProducts();

    @Transaction
    @Query("SELECT * FROM Shopping")
    List<Shopping> getAll();

    @Transaction
    @Query("SELECT * FROM Shopping")
    LiveData<List<Shopping>> getAllLiveData();
}
