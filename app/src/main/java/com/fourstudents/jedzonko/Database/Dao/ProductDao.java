package com.fourstudents.jedzonko.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.Entities.Shopping;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

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

    @Query("SELECT * FROM product")
    LiveData<List<Product>> getAllLiveData();
}
