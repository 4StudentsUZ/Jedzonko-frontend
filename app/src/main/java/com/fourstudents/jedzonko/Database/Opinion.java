package com.fourstudents.jedzonko.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "opinion")
public class Opinion {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "comment")
    private String comment;

    @ColumnInfo(name = "rating")
    private int rating;

    @ColumnInfo(name = "recipe")
    private Recipe recipe;

    @ColumnInfo(name = "author")
    private User author;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
