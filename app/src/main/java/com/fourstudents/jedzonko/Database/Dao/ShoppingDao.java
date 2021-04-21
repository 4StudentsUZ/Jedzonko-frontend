package com.fourstudents.jedzonko.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Shopping;
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

    @Transaction
    @Query("SELECT * FROM Shopping")
    public List<ShoppingsWithProducts> getShoppingsWithProducts();
}
