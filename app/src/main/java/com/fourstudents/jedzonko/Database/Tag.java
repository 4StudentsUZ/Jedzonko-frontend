package com.fourstudents.jedzonko.Database;

import android.media.Image;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Set;

@Entity(tableName = "tag")
public class Tag {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "recipeId")
    private long recipeId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
