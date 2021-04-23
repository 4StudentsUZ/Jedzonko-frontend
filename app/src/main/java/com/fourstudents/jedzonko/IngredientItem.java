package com.fourstudents.jedzonko;

import com.fourstudents.jedzonko.Database.Entities.Product;

public class IngredientItem {
    public Product product;
    public String quantity;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
