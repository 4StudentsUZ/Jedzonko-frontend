package com.fourstudents.jedzonko.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fourstudents.jedzonko.Database.Entities.Product;

import java.util.ArrayList;
import java.util.List;

public class IngredientsViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> ingredientList = new MutableLiveData<>(new ArrayList<>());

    public IngredientsViewModel() {
        System.out.println("CONSTRUCTOR");
        System.out.println("CCCCCCC");
        for (Product product : ingredientList.getValue()) {
            System.out.println(product.getName());
        }
        System.out.println("DDDDDD");
    }

    public LiveData<List<Product>> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Product> products) {
        System.out.println("SET");
        ingredientList.setValue(products);
    }

    public void removeIngredient(Product product) {
        System.out.println("REMOVE");
        List<Product> products = ingredientList.getValue();
        if (products == null) return;
        products.remove(product);
        ingredientList.setValue(products);
    }

    public void addIngredient(Product product) {
        List<Product> products = ingredientList.getValue();
        System.out.println("ADD");
        if (products == null) return;
        products.add(product);
        ingredientList.setValue(products);
    }

    public boolean hasIngredient(Product product) {
        System.out.println("CCCCCCC");
        for (Product p : ingredientList.getValue()) {
            System.out.println(p.getName());
        }
        System.out.println("DDDDDD");

        System.out.println("HAS");
        List<Product> products = ingredientList.getValue();
        if (products == null) return false;
        return products.contains(product);
    }

    public void clearIngredientList() {
        ingredientList.setValue(new ArrayList<>());
    }
}
