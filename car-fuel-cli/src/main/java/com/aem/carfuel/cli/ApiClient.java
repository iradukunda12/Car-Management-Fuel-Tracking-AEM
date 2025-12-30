package com.aem.carfuel.cli;

import com.aem.carfuel.cli.dto.AddFuelRequest;
import com.aem.carfuel.cli.dto.CreateCarRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }


    public String createCar(String brand, String model, int year) throws Exception {
        CreateCarRequest request = new CreateCarRequest(brand, model, year);
        String jsonBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cars"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to create car. Status: " +
                    response.statusCode() + ", Body: " + response.body());
        }
    }


    public String addFuel(String carId, double liters, double price, int odometer)
            throws Exception {
        AddFuelRequest request = new AddFuelRequest(liters, price, odometer);
        String jsonBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cars/" + carId + "/fuel"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to add fuel. Status: " +
                    response.statusCode() + ", Body: " + response.body());
        }
    }


    public String getFuelStatistics(String carId) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cars/" + carId + "/fuel/stats"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to get statistics. Status: " +
                    response.statusCode() + ", Body: " + response.body());
        }
    }


    public String getAllCars() throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/cars"))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to get cars. Status: " +
                    response.statusCode() + ", Body: " + response.body());
        }
    }
}