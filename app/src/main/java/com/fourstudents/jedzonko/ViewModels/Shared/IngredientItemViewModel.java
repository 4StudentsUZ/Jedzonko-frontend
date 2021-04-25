package com.fourstudents.jedzonko.ViewModels.Shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fourstudents.jedzonko.Other.IngredientItem;

import java.util.ArrayList;
import java.util.List;

public class IngredientItemViewModel extends ViewModel {
    private final MutableLiveData<List<IngredientItem>> ingredientItemList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<IngredientItem>> getIngredientItemList(){return ingredientItemList;}

    public List<IngredientItem> getIngredientItemsList(){return ingredientItemList.getValue();}

    public void setIngredientItemList(List<IngredientItem> ingredientItems){
        ingredientItemList.setValue(ingredientItems);
    }
    public int getIngredientItemsListSize(){return ingredientItemList.getValue().size();}

    public void removeIngredientItem(IngredientItem ingredientItem){
        List<IngredientItem> ingredientItems = ingredientItemList.getValue();
        if(ingredientItems==null) return;
        ingredientItems.remove(ingredientItem);
        ingredientItemList.setValue(ingredientItems);
    }

    public boolean isQuantityFilled(){
        List<IngredientItem> ingredientItems = ingredientItemList.getValue();
        if(ingredientItems==null) return false;
        for (IngredientItem ingredientItem: ingredientItems) {
           if(ingredientItem.quantity==null || ingredientItem.quantity.equals("")) return false;
        }
        return true;
    }

    public void addIngredientItem(IngredientItem ingredientItem){
        List<IngredientItem> ingredientItems = ingredientItemList.getValue();
        if(ingredientItems==null) return;
        ingredientItems.add(ingredientItem);
        ingredientItemList.setValue(ingredientItems);
    }

    public IngredientItem getIngredientItem(int position){
        List<IngredientItem> ingredientItems = ingredientItemList.getValue();
        IngredientItem ingredientItem = ingredientItems.get(position);
        return ingredientItem;
    }


    public boolean hasIngredientItem(IngredientItem ingredientItem) {

        List<IngredientItem> ingredientItems = ingredientItemList.getValue();
        if (ingredientItems == null) return false;
        for (IngredientItem ingredient:ingredientItems) {
            if(ingredient.product==ingredientItem.product) return true;
        }
        return false;
    }

    public void clearIngredientItemList() {
        ingredientItemList.setValue(new ArrayList<>());
    }

}
