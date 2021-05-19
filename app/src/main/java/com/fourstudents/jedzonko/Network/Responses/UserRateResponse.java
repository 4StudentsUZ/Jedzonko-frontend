package com.fourstudents.jedzonko.Network.Responses;

public class UserRateResponse {
    private final double rating;

    public UserRateResponse(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }
}
