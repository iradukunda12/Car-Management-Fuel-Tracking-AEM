package com.aem.carfuel.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;

public class CarFuelCLI {

    private static final String API_BASE_URL = "http://localhost:8080";
    private static final ApiClient apiClient = new ApiClient(API_BASE_URL);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Car Fuel Management CLI");
        System.out.println("========================================");
        System.out.println();

        if (args.length > 0) {
            handleCommand(args);
        } else {
            interactiveMode();
        }
    }


    private static void interactiveMode() {
        printUsage();
        System.out.println();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                printUsage();
                continue;
            }

            if (input.isEmpty()) {
                continue;
            }

            String[] args = input.split("\\s+");
            handleCommand(args);
            System.out.println();
        }
    }


    private static void handleCommand(String[] args) {
        try {
            String command = args[0];

            switch (command) {
                case "create-car":
                    handleCreateCar(args);
                    break;
                case "add-fuel":
                    handleAddFuel(args);
                    break;
                case "fuel-stats":
                    handleFuelStats(args);
                    break;
                case "list-cars":
                    handleListCars();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
                    System.out.println("Type 'help' to see available commands.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    private static void handleCreateCar(String[] args) throws Exception {
        String brand = getArgValue(args, "--brand");
        String model = getArgValue(args, "--model");
        String yearStr = getArgValue(args, "--year");

        if (brand == null || model == null || yearStr == null) {
            System.out.println("Usage: create-car --brand <brand> --model <model> --year <year>");
            System.out.println("Example: create-car --brand Toyota --model Corolla --year 2018");
            return;
        }

        int year = Integer.parseInt(yearStr);

        String response = apiClient.createCar(brand, model, year);
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println("Car created successfully!");
        System.out.println("----------------------------------------");
        System.out.println("ID:    " + jsonNode.get("id").asText());
        System.out.println("Brand: " + jsonNode.get("brand").asText());
        System.out.println("Model: " + jsonNode.get("model").asText());
        System.out.println("Year:  " + jsonNode.get("year").asInt());
        System.out.println("----------------------------------------");
        System.out.println("Note: Save this ID for adding fuel entries!");
    }


    private static void handleAddFuel(String[] args) throws Exception {
        String carId = getArgValue(args, "--carId");
        String litersStr = getArgValue(args, "--liters");
        String priceStr = getArgValue(args, "--price");
        String odometerStr = getArgValue(args, "--odometer");

        if (carId == null || litersStr == null || priceStr == null || odometerStr == null) {
            System.out.println("Usage: add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
            System.out.println("Example: add-fuel --carId abc-123 --liters 40 --price 52.5 --odometer 45000");
            return;
        }

        double liters = Double.parseDouble(litersStr);
        double price = Double.parseDouble(priceStr);
        int odometer = Integer.parseInt(odometerStr);

        String response = apiClient.addFuel(carId, liters, price, odometer);
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println("Fuel entry added successfully!");
        System.out.println("----------------------------------------");
        System.out.println("Entry ID:  " + jsonNode.get("id").asText());
        System.out.println("Liters:    " + jsonNode.get("liters").asDouble() + " L");
        System.out.println("Price:     $" + String.format("%.2f", jsonNode.get("price").asDouble()));
        System.out.println("Odometer:  " + String.format("%,d", jsonNode.get("odometer").asInt()) + " km");
        System.out.println("----------------------------------------");
    }


    private static void handleFuelStats(String[] args) throws Exception {
        String carId = getArgValue(args, "--carId");

        if (carId == null) {
            System.out.println("Usage: fuel-stats --carId <id>");
            System.out.println("Example: fuel-stats --carId abc-123");
            return;
        }

        String response = apiClient.getFuelStatistics(carId);
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println("========================================");
        System.out.println("     FUEL STATISTICS REPORT");
        System.out.println("========================================");
        System.out.println("Total Fuel:       " + String.format("%.2f", jsonNode.get("totalFuelLiters").asDouble()) + " L");
        System.out.println("Total Cost:       $" + String.format("%.2f", jsonNode.get("totalCost").asDouble()));
        System.out.println("Avg Consumption:  " + String.format("%.2f", jsonNode.get("averageConsumptionPer100Km").asDouble()) + " L/100km");
        System.out.println("========================================");
    }


    private static void handleListCars() throws Exception {
        String response = apiClient.getAllCars();
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println("========================================");
        System.out.println("           ALL CARS");
        System.out.println("========================================");

        if (jsonNode.isArray() && jsonNode.size() > 0) {
            int count = 1;
            for (JsonNode car : jsonNode) {
                System.out.println("Car #" + count);
                System.out.println("  ID:           " + car.get("id").asText());
                System.out.println("  Brand:        " + car.get("brand").asText());
                System.out.println("  Model:        " + car.get("model").asText());
                System.out.println("  Year:         " + car.get("year").asInt());
                System.out.println("  Fuel Entries: " + car.get("fuelEntries").size());
                System.out.println("  -----------------------------");
                count++;
            }
            System.out.println("Total: " + jsonNode.size() + " car(s)");
        } else {
            System.out.println("No cars found.");
            System.out.println("Use 'create-car' command to add a car.");
        }
        System.out.println("========================================");
    }


    private static String getArgValue(String[] args, String argName) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(argName)) {
                return args[i + 1];
            }
        }
        return null;
    }


    private static void printUsage() {
        System.out.println("Available commands:");
        System.out.println();
        System.out.println("  create-car --brand <brand> --model <model> --year <year>");
        System.out.println("    Example: create-car --brand Toyota --model Corolla --year 2018");
        System.out.println();
        System.out.println("  add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
        System.out.println("    Example: add-fuel --carId abc-123 --liters 40 --price 52.5 --odometer 45000");
        System.out.println();
        System.out.println("  fuel-stats --carId <id>");
        System.out.println("    Example: fuel-stats --carId abc-123");
        System.out.println();
        System.out.println("  list-cars");
        System.out.println("    Lists all registered cars");
        System.out.println();
        System.out.println("  help");
        System.out.println("    Show this help message");
        System.out.println();
        System.out.println("  exit");
        System.out.println("    Exit the application");
        System.out.println();
    }
}