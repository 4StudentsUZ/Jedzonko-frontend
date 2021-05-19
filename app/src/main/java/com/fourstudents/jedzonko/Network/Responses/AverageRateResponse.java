package com.fourstudents.jedzonko.Network.Responses;

public class AverageRateResponse {
    private final double average;

    public AverageRateResponse(int average) {
        this.average = average;
    }

    public double getAverage() {
        return average;
    }
}
