package com.fourstudents.jedzonko.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insert(Product product);

    @Delete
    void delete (Product product);

    @Update
    void update (Product product);

    @Query("SELECT * FROM product")
    List<Product> getAll();

    @Query("SELECT productId FROM product ORDER BY productId DESC LIMIT 1")
    int getLastId();

    @Query("SELECT * FROM product")
    LiveData<List<Product>> getAllLiveData();
}
