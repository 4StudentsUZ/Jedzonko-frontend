package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping")
public class Shopping {
    @PrimaryKey(autoGenerate = true)
    private long shoppingId;

    @ColumnInfo(name = "name")
    private String name;

    public long getShoppingId() {
        return shoppingId;
    }

    public void setShoppingId(long shoppingId) {
        this.shoppingId = shoppingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}