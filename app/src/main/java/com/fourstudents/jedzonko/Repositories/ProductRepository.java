package com.fourstudents.jedzonko.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.fourstudents.jedzonko.Database.Dao.ProductDao;
import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.util.List;

public class ProductRepository {
    private final ProductDao productDao;
    private final LiveData<List<Product>> allLiveDataProductList;


    public ProductRepository(Application application) {
        RoomDB db = RoomDB.getInstance(application);
        productDao = db.productDao();
        allLiveDataProductList = productDao.getAllLiveData();
    }

    public LiveData<List<Product>> getAllLiveDataProductList() {
        return allLiveDataProductList;
    }

    public void insert(Product product) {
        RoomDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                productDao.insert(product);
            }
        });
    }
}
