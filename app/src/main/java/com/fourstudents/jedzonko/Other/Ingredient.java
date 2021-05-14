package com.fourstudents.jedzonko.Other;

public class Ingredient {
    private int id;
    private String name;
    private String barcode;
    private String image;
    private Author author;

    public Ingredient(int id, String name, String barcode, String image, Author author) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.image = image;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
