package com.fourstudents.jedzonko.Network.Responses;

import com.fourstudents.jedzonko.Other.Author;
import com.fourstudents.jedzonko.Other.Ingredient;

import java.io.Serializable;
import java.util.List;
public class RecipeResponse implements Serializable {
    private final int id;
    private final String title;
    private final String description;
    private final Author author;
    private final List<Ingredient> ingredients;
    private final List<String> quantities;
    private final List<String> tags;
    private final String image;


    public RecipeResponse(int id, String title, String description, Author author, List<Ingredient> ingredients, List<String> quantities, List<String> tags, String image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.ingredients = ingredients;
        this.quantities=quantities;
        this.tags=tags;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Author getAuthor() {
        return author;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getImage() {
        return image;
    }

    public List<String> getQuantities() {
        return quantities;
    }

    public List<String> getTags() {
        return tags;
    }
}
