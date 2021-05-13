package com.fourstudents.jedzonko.Network.Responses;

public class ProductResponse {
    private  final  long id;
    private final String name;
    private final String barcode;
    private final String image;

    public ProductResponse(long id, String name, String barcode, String image) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getImage() {
        return image;
    }
}
