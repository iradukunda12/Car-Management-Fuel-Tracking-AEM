# Car Fuel Management System

A comprehensive full-stack application for managing cars and tracking fuel consumption, built with Spring Boot (backend), Java Servlets, and a Command-Line Interface (CLI).

## Project Structure
```
Car-fuel-Management-Assessment/
├── src/main/java/com/aem/carfuel/          # Backend (Spring Boot)
│   ├── model/                               # Data models
│   │   ├── Car.java
│   │   ├── FuelEntry.java
│   │   └── FuelStatistics.java
│   ├── service/                             # Business logic
│   │   └── CarService.java
│   ├── controller/                          # REST API endpoints
│   │   └── CarController.java
│   ├── servlet/                             # Manual Servlet implementation
│   │   └── FuelStatsServlet.java
│   ├── ServletConfig.java
│   └── CarFuelManagementAssessmentApplication.java
├── car-fuel-cli/                            # CLI Module
│   └── src/main/java/com/aem/carfuel/cli/
│       ├── dto/
│       │   ├── CreateCarRequest.java
│       │   └── AddFuelRequest.java
│       ├── ApiClient.java
│       └── CarFuelCLI.java
├── pom.xml                                  # Backend Maven configuration
└── README.md
```

## Prerequisites

- **Java 17** or higher
- **Maven** (or use included Maven wrapper `./mvnw`)
- **Port 8080** must be available

## Installation & Running

### **Backend (Spring Boot API)**

#### Option 1: Using Maven Wrapper (Recommended)
```bash
# Navigate to project root
cd Car-fuel-Management-Assessment

# Clean and compile
./mvnw clean compile

# Run the backend
./mvnw spring-boot:run
```

#### Option 2: Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Locate `CarFuelManagementAssessmentApplication.java`
3. Right-click → Run 'CarFuelManagementAssessmentApplication.main()'

The backend will start on **http://localhost:8080**

#### Verify Backend is Running
Open browser and navigate to:
```
http://localhost:8080/api/cars
```
You should see: `[]` (empty array)

---

### **CLI Application**

**IMPORTANT:** Make sure the backend is running before starting the CLI!

#### Option 1: Using Maven (Recommended)
```bash
# Navigate to CLI module
cd car-fuel-cli

# Compile
../mvnw clean compile

# Run CLI
java -cp "target/classes:$HOME/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar:$HOME/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.15.2/jackson-annotations-2.15.2.jar:$HOME/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar" com.aem.carfuel.cli.CarFuelCLI
```

#### Option 2: Using IntelliJ IDEA
1. Navigate to `car-fuel-cli/src/main/java/com/aem/carfuel/cli/CarFuelCLI.java`
2. Right-click → Run 'CarFuelCLI.main()'

---

## 📖 CLI Usage

When you start the CLI, you'll see available commands:

### **Create a Car**
```bash
> create-car --brand Toyota --model Corolla --year 2018
```

**Output:**
```
Car created successfully!
----------------------------------------
ID:    abc-123-def-456
Brand: Toyota
Model: Corolla
Year:  2018
----------------------------------------
Note: Save this ID for adding fuel entries!
```

### **Add Fuel Entry**
```bash
> add-fuel --carId abc-123-def-456 --liters 40 --price 52.5 --odometer 45000
```

**Output:**
```
Fuel entry added successfully!
----------------------------------------
Entry ID:  xyz-789
Liters:    40.0 L
Price:     $52.50
Odometer:  45,000 km
----------------------------------------
```

### **View Fuel Statistics**
```bash
> fuel-stats --carId abc-123-def-456
```

**Output:**
```
========================================
     FUEL STATISTICS REPORT
========================================
Total Fuel:       85.00 L
Total Cost:       $111.25
Avg Consumption:  12.14 L/100km
========================================
```

### **List All Cars**
```bash
> list-cars
```

### **Exit**
```bash
> exit
```

---

## API Endpoints

### **REST API (Spring Boot)**

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cars` | Create a new car |
| GET | `/api/cars` | List all cars |
| POST | `/api/cars/{id}/fuel` | Add fuel entry |
| GET | `/api/cars/{id}/fuel/stats` | Get fuel statistics |

### **Servlet Endpoint**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/servlet/fuel-stats?carId={id}` | Get fuel statistics (Servlet implementation) |

---

## Testing with Postman

### **1. Create a Car**
**Request:**
- Method: `POST`
- URL: `http://localhost:8080/api/cars`
- Body (JSON):
```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018
}
```

**Response:** (Status: 201 Created)
```json
{
  "id": "abc-123-def-456",
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": []
}
```

### **2. Add Fuel Entry**
**Request:**
- Method: `POST`
- URL: `http://localhost:8080/api/cars/abc-123-def-456/fuel`
- Body (JSON):
```json
{
  "liters": 40,
  "price": 52.5,
  "odometer": 45000
}
```

### **3. Get Statistics (REST API)**
**Request:**
- Method: `GET`
- URL: `http://localhost:8080/api/cars/abc-123-def-456/fuel/stats`

### **4. Get Statistics (Servlet)**
**Request:**
- Method: `GET`
- URL: `http://localhost:8080/servlet/fuel-stats?carId=abc-123-def-456`

---

## 🏛️ Architecture

### **Three-Layer Architecture**
```
┌─────────────────────────────────────┐
│   Presentation Layer                │
│   - REST Controller (@RestController)│
│   - Servlet (HttpServlet)           │
│   - CLI (ApiClient)                 │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Business Logic Layer              │
│   - CarService (@Service)           │
│   - Validations                     │
│   - Statistics calculation          │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│   Data Layer                        │
│   - HashMap (in-memory storage)     │
│   - Car, FuelEntry models           │
└─────────────────────────────────────┘
```

### **Key Design Decisions**

1. **HashMap for Storage:**
   - O(1) constant-time access by ID
   - Suitable for assignment requirements (in-memory)
   - Easy to demonstrate CRUD operations

2. **UUID for IDs:**
   - Globally unique
   - Security: unpredictable
   - Scalable for distributed systems

3. **Service Layer Reuse:**
   - Same `CarService` used by REST Controller, Servlet, and CLI
   - Demonstrates separation of concerns
   - Single source of truth for business logic

4. **HttpClient for CLI:**
   - Java 11+ modern HTTP client
   - Clean API for REST calls
   - Follows best practices

---

## 🔍 Data Storage

### **In-Memory Storage Structure**
```java
// CarService.java
private Map<String, Car> carStorage = new HashMap<>();
private Map<String, FuelEntry> fuelEntryStorage = new HashMap<>();
```

**Why two HashMaps?**
- `carStorage`: Stores all cars with UUID as key
- `fuelEntryStorage`: Stores all fuel entries globally
- Each `Car` object also maintains a list of its fuel entries

---

## Statistics Calculation

### **Average Fuel Consumption Formula**
```
Average Consumption (L/100km) = (Total Fuel / Distance Traveled) × 100

Where:
- Total Fuel = Sum of all fuel entries
- Distance Traveled = Last Odometer - First Odometer
```

**Example:**
```
Fuel Entry 1: 40L at 45000 km
Fuel Entry 2: 45L at 45700 km

Total Fuel = 40 + 45 = 85L
Distance = 45700 - 45000 = 700 km
Average = (85 / 700) × 100 = 12.14 L/100km
```

**Note:** Requires at least 2 fuel entries to calculate average consumption.

---

## 🛠️ Technologies Used

- **Backend:** Spring Boot 4.0.1, Java 17
- **Web Server:** Embedded Tomcat
- **JSON Processing:** Jackson (ObjectMapper)
- **HTTP Client:** Java 11+ HttpClient
- **Build Tool:** Maven
- **Architecture:** Three-layer (MVC)

---

## Features Implemented

### **Part 1: REST API (Spring Boot)**
✅ Create car  
✅ List all cars  
✅ Add fuel entry  
✅ Calculate fuel statistics  
✅ Input validation  
✅ Error handling with proper HTTP status codes  
✅ JSON request/response  

### **Part 2: Servlet**
✅ Manual HTTP handling  
✅ Query parameter parsing  
✅ Manual JSON conversion  
✅ Manual status codes  
✅ Shares same service layer  

### **Part 3: CLI Application**
✅ Separate Maven module  
✅ HttpClient for API communication  
✅ Command-line interface  
✅ Interactive mode  
✅ Error handling  

---

## Troubleshooting

### **Port 8080 Already in Use**
```bash
# Kill process on port 8080 (Mac/Linux)
lsof -ti:8080 | xargs kill -9

# Kill process on port 8080 (Windows)
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### **Maven Command Not Found**
Use the Maven wrapper instead:
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

### **CLI Cannot Connect to Backend**
Make sure:
1. Backend is running on port 8080
2. Check backend logs for errors
3. Test backend in browser: `http://localhost:8080/api/cars`

---

## 📧 Contact

For questions or issues, contact: iradukundakvn8@gmail.com

---

## 📄 License

This project was created as part of a technical assessment.
