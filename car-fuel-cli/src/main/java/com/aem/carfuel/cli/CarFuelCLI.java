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
        while (true) {
            showMenu();
            System.out.print("Enter choice (1-5): ");
            String choice = scanner.nextLine().trim();
            System.out.println();

            if (choice.equals("5") || choice.equalsIgnoreCase("exit") || choice.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye! ");
                break;
            }

            switch (choice) {
                case "1":
                    menuCreateCar();
                    break;
                case "2":
                    menuAddFuel();
                    break;
                case "3":
                    menuViewStats();
                    break;
                case "4":
                    menuListCars();
                    break;
                default:
                    System.out.println("❌ Invalid choice. Please enter 1-5.");
            }

            System.out.println();
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            System.out.println("\n");
        }
    }


    private static void showMenu() {
        System.out.println("========================================");
        System.out.println("           MAIN MENU                    ");
        System.out.println("========================================");
        System.out.println("  1. Create New Car                     ");
        System.out.println("  2. Add Fuel Entry                     ");
        System.out.println("  3. View Fuel Statistics               ");
        System.out.println("  4. List All Cars                      ");
        System.out.println("  5. Exit                               ");
        System.out.println("========================================");
        System.out.println();
    }


    private static void menuCreateCar() {
        System.out.println("----------------------------------------");
        System.out.println("        CREATE NEW CAR");
        System.out.println("----------------------------------------");
        System.out.println();

        System.out.print("Brand: ");
        String brand = scanner.nextLine().trim();

        System.out.print("Model: ");
        String model = scanner.nextLine().trim();

        System.out.print("Year: ");
        String yearStr = scanner.nextLine().trim();

        try {
            int year = Integer.parseInt(yearStr);
            String response = apiClient.createCar(brand, model, year);
            JsonNode jsonNode = objectMapper.readTree(response);

            System.out.println();
            System.out.println("Car created successfully!");
            System.out.println("----------------------------------------");
            System.out.println("ID:    " + jsonNode.get("id").asText());
            System.out.println("Brand: " + jsonNode.get("brand").asText());
            System.out.println("Model: " + jsonNode.get("model").asText());
            System.out.println("Year:  " + jsonNode.get("year").asInt());
            System.out.println("----------------------------------------");
            System.out.println("Note: Save this ID for fuel entries!");

        } catch (NumberFormatException e) {
            System.out.println("Error: Year must be a number!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static void menuAddFuel() {
        System.out.println("----------------------------------------");
        System.out.println("        ADD FUEL ENTRY");
        System.out.println("----------------------------------------");
        System.out.println();

        System.out.print("Car ID: ");
        String carId = scanner.nextLine().trim();

        System.out.print("Liters: ");
        String litersStr = scanner.nextLine().trim();

        System.out.print("Price: ");
        String priceStr = scanner.nextLine().trim();

        System.out.print("Odometer (km): ");
        String odometerStr = scanner.nextLine().trim();

        try {
            double liters = Double.parseDouble(litersStr);
            double price = Double.parseDouble(priceStr);
            int odometer = Integer.parseInt(odometerStr);

            String response = apiClient.addFuel(carId, liters, price, odometer);
            JsonNode jsonNode = objectMapper.readTree(response);

            System.out.println();
            System.out.println("Fuel entry added successfully!");
            System.out.println("----------------------------------------");
            System.out.println("Entry ID:  " + jsonNode.get("id").asText());
            System.out.println("Liters:    " + jsonNode.get("liters").asDouble() + " L");
            System.out.println("Price:     $" + String.format("%.2f", jsonNode.get("price").asDouble()));
            System.out.println("Odometer:  " + String.format("%,d", jsonNode.get("odometer").asInt()) + " km");
            System.out.println("----------------------------------------");

        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static void menuViewStats() {
        System.out.println("----------------------------------------");
        System.out.println("        FUEL STATISTICS");
        System.out.println("----------------------------------------");
        System.out.println();

        System.out.print("Car ID: ");
        String carId = scanner.nextLine().trim();

        try {
            String response = apiClient.getFuelStatistics(carId);
            JsonNode jsonNode = objectMapper.readTree(response);

            System.out.println();
            System.out.println("========================================");
            System.out.println("     FUEL STATISTICS REPORT");
            System.out.println("========================================");
            System.out.println("Total Fuel:       " + String.format("%.2f", jsonNode.get("totalFuelLiters").asDouble()) + " L");
            System.out.println("Total Cost:       $" + String.format("%.2f", jsonNode.get("totalCost").asDouble()));
            System.out.println("Avg Consumption:  " + String.format("%.2f", jsonNode.get("averageConsumptionPer100Km").asDouble()) + " L/100km");
            System.out.println("========================================");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static void menuListCars() {
        System.out.println("----------------------------------------");
        System.out.println("        ALL CARS");
        System.out.println("----------------------------------------");
        System.out.println();

        try {
            String response = apiClient.getAllCars();
            JsonNode jsonNode = objectMapper.readTree(response);

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
                System.out.println("Tip: Create a car using option 1!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
                    printUsage();
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
            return;
        }

        int year = Integer.parseInt(yearStr);
        String response = apiClient.createCar(brand, model, year);
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println(" Car created successfully!");
        System.out.println("  ID: " + jsonNode.get("id").asText());
        System.out.println("  Brand: " + jsonNode.get("brand").asText());
        System.out.println("  Model: " + jsonNode.get("model").asText());
        System.out.println("  Year: " + jsonNode.get("year").asInt());
    }


    private static void handleAddFuel(String[] args) throws Exception {
        String carId = getArgValue(args, "--carId");
        String litersStr = getArgValue(args, "--liters");
        String priceStr = getArgValue(args, "--price");
        String odometerStr = getArgValue(args, "--odometer");

        if (carId == null || litersStr == null || priceStr == null || odometerStr == null) {
            System.out.println("Usage: add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
            return;
        }

        double liters = Double.parseDouble(litersStr);
        double price = Double.parseDouble(priceStr);
        int odometer = Integer.parseInt(odometerStr);

        String response = apiClient.addFuel(carId, liters, price, odometer);
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println(" Fuel entry added successfully!");
        System.out.println("  Entry ID: " + jsonNode.get("id").asText());
        System.out.println("  Liters: " + jsonNode.get("liters").asDouble() + " L");
        System.out.println("  Price: $" + jsonNode.get("price").asDouble());
        System.out.println("  Odometer: " + jsonNode.get("odometer").asInt() + " km");
    }


    private static void handleFuelStats(String[] args) throws Exception {
        String carId = getArgValue(args, "--carId");

        if (carId == null) {
            System.out.println("Usage: fuel-stats --carId <id>");
            return;
        }

        String response = apiClient.getFuelStatistics(carId);
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println("========================================");
        System.out.println("   Fuel Statistics");
        System.out.println("========================================");
        System.out.println("Total fuel: " + jsonNode.get("totalFuelLiters").asDouble() + " L");
        System.out.println("Total cost: $" + String.format("%.2f", jsonNode.get("totalCost").asDouble()));
        System.out.println("Average consumption: " + jsonNode.get("averageConsumptionPer100Km").asDouble() + " L/100km");
        System.out.println("========================================");
    }


    private static void handleListCars() throws Exception {
        String response = apiClient.getAllCars();
        JsonNode jsonNode = objectMapper.readTree(response);

        System.out.println("========================================");
        System.out.println("   All Cars");
        System.out.println("========================================");

        if (jsonNode.isArray() && jsonNode.size() > 0) {
            for (JsonNode car : jsonNode) {
                System.out.println("ID: " + car.get("id").asText());
                System.out.println("  Brand: " + car.get("brand").asText());
                System.out.println("  Model: " + car.get("model").asText());
                System.out.println("  Year: " + car.get("year").asInt());
                System.out.println("  Fuel Entries: " + car.get("fuelEntries").size());
                System.out.println("---");
            }
        } else {
            System.out.println("No cars found.");
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
        System.out.println("  add-fuel --carId <id> --liters <liters> --price <price> --odometer <odometer>");
        System.out.println("  fuel-stats --carId <id>");
        System.out.println("  list-cars");
    }
}