package com.bobrrent.models;

public class Car {
    private String carId;
    private String brand;
    private String model;
    private double price;
    private String status;

    public Car(String carId, String brand, String model, double price, String status) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.status = status;
    }

    public String getCarId() { return carId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }
}