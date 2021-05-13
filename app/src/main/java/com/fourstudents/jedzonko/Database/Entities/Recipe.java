package com.fourstudents.jedzonko.Database.Entities;

import android.media.Image;

import androidx.room.Entity;


import androidx.room.*;

import java.io.Serializable;
import java.util.Set;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "recipe")
public class Recipe implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long  recipeId;

    @ColumnInfo(name = "remoteId")
    private long remoteId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] data;

    public long getRecipeId() {
          return recipeId;
    }

    public void setRecipeId(long recipeId) {
          this.recipeId = recipeId;
    }

    public String getTitle() {
          return title;
    }

    public void setTitle(String title) {
          this.title = title;
    }

    public String getDescription() {
          return description;
    }

    public void setDescription(String description) {
          this.description = description;
    }

    public byte[] getData() {
          return data;
    }

    public void setData(byte[] data) {
              this.data = data;
       }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }
}

