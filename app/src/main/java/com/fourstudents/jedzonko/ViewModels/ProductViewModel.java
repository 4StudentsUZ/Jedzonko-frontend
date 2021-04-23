package com.fourstudents.jedzonko.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.fourstudents.jedzonko.Database.Entities.Product;
import com.fourstudents.jedzonko.Repositories.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private final ProductRepository repository;
    private final LiveData<List<Product>> allLiveDataProductList;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = new ProductRepository(application);
        allLiveDataProductList = repository.getAllLiveDataProductList();
    }

    public LiveData<List<Product>> getAllLiveDataProductList() {
        return allLiveDataProductList;
    }

    public void insert(Product product) {
        repository.insert(product);
    }
}
