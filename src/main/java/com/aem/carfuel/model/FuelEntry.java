package com.aem.carfuel.model;

public class FuelEntry {

    private String id;
    private String carId;
    private double liters;
    private double price;
    private int odometer;


    public FuelEntry() {
    }

    public FuelEntry(String id, String carId, double liters, double price, int odometer) {
        this.id = id;
        this.carId = carId;
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
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


    @Override
    public String toString() {
        return "FuelEntry{" +
                "id='" + id + '\'' +
                ", carId='" + carId + '\'' +
                ", liters=" + liters +
                ", price=" + price +
                ", odometer=" + odometer +
                '}';
    }
}