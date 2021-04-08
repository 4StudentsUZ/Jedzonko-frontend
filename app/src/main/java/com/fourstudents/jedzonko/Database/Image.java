package com.fourstudents.jedzonko.Database;

import androidx.room.Entity;
import androidx.room.*;

@Entity(tableName = "image")
public class Image {
    @PrimaryKey (autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "data")
    private byte[] data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
