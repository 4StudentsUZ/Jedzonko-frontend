package com.fourstudents.jedzonko.ViewModels.ShoppingList;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.fourstudents.jedzonko.Database.Entities.Shopping;
import com.fourstudents.jedzonko.Repositories.ShoppingList.ShoppingRepository;

import java.util.List;

public class ShoppingViewModel extends AndroidViewModel {

    private final ShoppingRepository repository;
    private final LiveData<List<Shopping>> allLiveDataShoppingList;

    public ShoppingViewModel(@NonNull Application application) {
        super(application);
        repository = new ShoppingRepository(application);
        allLiveDataShoppingList = repository.getAllLiveDataShoppingList();
    }

    public LiveData<List<Shopping>> getAllLiveDataShoppingList() {
        return allLiveDataShoppingList;
    }

    public void insert(Shopping shopping) {
        repository.insert(shopping);
    }
}
