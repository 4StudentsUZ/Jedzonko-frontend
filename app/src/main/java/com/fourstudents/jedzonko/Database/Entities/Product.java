package com.fourstudents.jedzonko.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Set;

@Entity(tableName = "product")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public long productId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "barcode")
    private String barcode;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] data;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
