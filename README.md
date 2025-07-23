# Carpark API

API for finding the nearest car parks in Singapore with real-time availability, built with Spring Boot.

## How to Run

1. Start the PostgreSQL database:
```
docker-compose up -d postgres
```
2. Build the application JAR:
```
mvn clean install
```
3. Start the application (and rebuild the Docker image if needed):
```
docker-compose up -d --build
```

- App: http://localhost:8080
- PostgreSQL: ocalhost:5432
```
user:   carpark_user
pass:   carpark_password
db:     carpark_db
```

## Features
- Find nearest car parks with available lots
- Import car park data from CSV
- Scheduled and manual update of car park availability from public API
- PostgreSQL database support
- Dockerized for easy deployment
- Health and metrics endpoints

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose
- Maven
- OpenCSV

## Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17 (for local builds)
- Maven (for local builds)

### Running with Docker Compose

```
git clone <repo-url>
cd carparkapi

# Start PostgreSQL
docker-compose up -d postgres

# Build app jar
mvn clean install

# Start the application
docker-compose up --build
```

### Running Locally (without Docker)

1. Start PostgreSQL locally with the same credentials as above.
2. Import the schema/data if needed.
3. Build and run:
   ```
   ./mvnw clean package
   java -jar target/carparkapi-1.0.0.jar
   ```

## Configuration

Configuration is managed via `src/main/resources/application.yml` and environment variables:

- `SPRING_DATASOURCE_URL` (default: jdbc:postgresql://localhost:5432/carpark_db)
- `SPRING_DATASOURCE_USERNAME` (default: carpark_user)
- `SPRING_DATASOURCE_PASSWORD` (default: carpark_password)
- `SPRING_JPA_HIBERNATE_DDL_AUTO` (default: update)

## Data Import

Initial car park data is loaded from:
- `src/main/resources/data/hdb-carpark-information.csv`

CSV columns:
```
car_park_no,address,x_coord,y_coord,car_park_type,type_of_parking_system,short_term_parking,free_parking,night_parking,car_park_decks,gantry_height,car_park_basement
```

To manually import:
```
GET /carparks/import/csv
```

## API Endpoints

### Find Nearest Carparks
```
GET /carparks/nearest?latitude=1.3&longitude=103.8&page=1&per_page=10
```
- Returns a list of car parks sorted by distance with available lots.

### Manual Data Import
```
GET /carparks/import/csv
```
- Imports car park data from the CSV file.

### Manual Availability Update
```
GET /carparks/import/availability
```
- Triggers an immediate update of car park availability from the public API.

### Health Check
```
GET /carparks/health
```
- Returns service health and number of carparks with availability.

## Testing

Run all tests:
```
./mvnw test
```

## Logs

- Application logs: `logs/carpark.log`

## Contact

👋 Chester Javier <br />
chesterjavier03@gmail.com <br />
[https://chesterjavier03.vercel.app/](https://chesterjavier03.vercel.app/)<br />
