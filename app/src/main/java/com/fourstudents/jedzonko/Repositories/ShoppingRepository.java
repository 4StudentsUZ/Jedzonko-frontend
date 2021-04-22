package com.fourstudents.jedzonko.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.fourstudents.jedzonko.Database.Dao.ShoppingDao;
import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Database.RoomDB;

import java.util.List;

public class ShoppingRepository {
    private ShoppingDao shoppingDao;
    private LiveData<List<Shopping>> allLiveDataShoppingList;


    public ShoppingRepository(Application application) {
        RoomDB db = RoomDB.getInstance(application);
        shoppingDao = db.shoppingDao();
        allLiveDataShoppingList = shoppingDao.getAllLiveData();
    }

    public LiveData<List<Shopping>> getAllLiveDataShoppingList() {
        return allLiveDataShoppingList;
    }

    public void insert(Shopping shopping) {
        RoomDB.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                shoppingDao.insert(shopping);
            }
        });
    }
}
