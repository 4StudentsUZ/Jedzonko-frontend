package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tag")
public class Tag {
    @PrimaryKey(autoGenerate = true)
    private long tagId;

    @ColumnInfo(name = "name")
    private String name;

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
