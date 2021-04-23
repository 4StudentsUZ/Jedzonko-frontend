package com.fourstudents.jedzonko.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fourstudents.jedzonko.Database.Entities.Product;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> mutableProductList = new MutableLiveData<>();

    public void setList(List<Product> products) {
        mutableProductList.setValue(products);
    }

    public LiveData<List<Product>> getList() {
        return mutableProductList;
    }
}
