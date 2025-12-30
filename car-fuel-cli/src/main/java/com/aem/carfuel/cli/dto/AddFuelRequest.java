package com.aem.carfuel.cli.dto;

public class AddFuelRequest {
    private double liters;
    private double price;
    private int odometer;

    public AddFuelRequest() {
    }

    public AddFuelRequest(double liters, double price, int odometer) {
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
    }

    public double getLiters() {
        return liters;
    }

    public void setLiters(double liters) {
        this.liters = liters;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }
}