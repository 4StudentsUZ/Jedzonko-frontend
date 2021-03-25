package com.fourstudents.jedzonko.Database;

import android.media.Image;

import androidx.room.Entity;


import androidx.room.*;

import java.util.Set;

@Entity(tableName = "recipe")
 public class Recipe{
        @PrimaryKey(autoGenerate = true)
        private long recipeId;

        @ColumnInfo(name = "title")
        private String title;

        @ColumnInfo(name = "description")
        private String description;


    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }
}

