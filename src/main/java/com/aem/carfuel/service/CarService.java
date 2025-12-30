package com.aem.carfuel.service;

import com.aem.carfuel.model.Car;
import com.aem.carfuel.model.FuelEntry;
import com.aem.carfuel.model.FuelStatistics;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CarService {

    private final Map<String, Car> carStorage = new HashMap<>();
    private final Map<String, FuelEntry> fuelEntryStorage = new HashMap<>();


    public Car createCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Car brand is required");
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Car model is required");
        }
        if (car.getYear() <= 1900 || car.getYear() > 2100) {
            throw new IllegalArgumentException("Invalid year. Must be between 1900 and 2100");
        }

        String id = UUID.randomUUID().toString();
        car.setId(id);

        if (car.getFuelEntries() == null) {
            car.setFuelEntries(new ArrayList<>());
        }

        carStorage.put(id, car);
        return car;
    }


    public List<Car> getAllCars() {
        return new ArrayList<>(carStorage.values());
    }


    public Optional<Car> getCarById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid car ID");
        }
        return Optional.ofNullable(carStorage.get(id));
    }


    public FuelEntry addFuelEntry(String carId, FuelEntry fuelEntry) {
        if (carId == null || carId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid car ID");
        }
        if (fuelEntry == null) {
            throw new IllegalArgumentException("Fuel entry cannot be null");
        }
        if (fuelEntry.getLiters() <= 0) {
            throw new IllegalArgumentException("Liters must be greater than 0");
        }
        if (fuelEntry.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (fuelEntry.getOdometer() < 0) {
            throw new IllegalArgumentException("Odometer cannot be negative");
        }

        Car car = carStorage.get(carId);
        if (car == null) {
            throw new IllegalArgumentException("Car with ID " + carId + " not found");
        }

        String fuelId = UUID.randomUUID().toString();
        fuelEntry.setId(fuelId);
        fuelEntry.setCarId(carId);

        fuelEntryStorage.put(fuelId, fuelEntry);
        car.addFuelEntry(fuelEntry);

        return fuelEntry;
    }


    public FuelStatistics getFuelStatistics(String carId) {
        if (carId == null || carId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid car ID");
        }

        Car car = carStorage.get(carId);
        if (car == null) {
            throw new IllegalArgumentException("Car with ID " + carId + " not found");
        }

        List<FuelEntry> entries = car.getFuelEntries();

        if (entries == null || entries.isEmpty()) {
            return new FuelStatistics(0.0, 0.0, 0.0);
        }

        double totalFuel = 0.0;
        double totalCost = 0.0;

        for (FuelEntry entry : entries) {
            totalFuel += entry.getLiters();
            totalCost += entry.getPrice();
        }

        double averageConsumption = 0.0;

        if (entries.size() >= 2) {
            List<FuelEntry> sortedEntries = new ArrayList<>(entries);
            sortedEntries.sort(Comparator.comparingInt(FuelEntry::getOdometer));

            int firstOdometer = sortedEntries.get(0).getOdometer();
            int lastOdometer = sortedEntries.get(sortedEntries.size() - 1).getOdometer();

            int distanceTraveled = lastOdometer - firstOdometer;

            if (distanceTraveled > 0) {
                averageConsumption = (totalFuel / distanceTraveled) * 100;
                averageConsumption = Math.round(averageConsumption * 100.0) / 100.0;
            }
        }

        return new FuelStatistics(totalFuel, totalCost, averageConsumption);
    }
}