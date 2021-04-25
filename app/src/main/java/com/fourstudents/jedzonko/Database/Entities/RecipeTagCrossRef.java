package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.Entity;

@Entity(tableName="RecipeTagCrossRef", primaryKeys = {"recipeId", "tagId"})
public class RecipeTagCrossRef {
    private long recipeId;
    private long tagId;

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
