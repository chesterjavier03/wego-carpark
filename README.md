# Carpark API

A Spring Boot application that provides an API to find the nearest car parks with available lots in Singapore. This application integrates with Singapore's government data sources to provide real-time parking availability information.

## Features

- **Nearest Carpark Search**: Find carparks closest to a given latitude/longitude with available parking spots
- **Real-time Availability**: Integration with Singapore's carpark availability API
- **Coordinate Conversion**: Converts SVY21 coordinates to WGS84 (latitude/longitude)
- **Pagination Support**: Paginated results for efficient data retrieval
- **Data Import**: CSV import functionality for carpark information
- **Validation**: Input validation for coordinates and parameters
- **Error Handling**: Comprehensive error handling with meaningful responses
- **Health Checks**: Health endpoint for monitoring
- **Docker Support**: Complete Docker containerization

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL** (Production) / H2 (Testing)
- **Maven**
- **Docker & Docker Compose**
- **Lombok**
- **OpenCSV**
- **WebFlux** (for external API calls)
- **JUnit 5 & Mockito** (Testing)

## API Endpoints

### GET /carparks/nearest

Find nearest carparks with available lots.

**Parameters:**
- `latitude` (required): User's latitude (1.0 - 1.5 for Singapore)
- `longitude` (required): User's longitude (103.0 - 104.5 for Singapore)
- `page` (optional): Page number, defaults to 1
- `per_page` (optional): Results per page, defaults to 10, max 100

**Example Request:**
```bash
GET /carparks/nearest?latitude=1.37326&longitude=103.897&page=1&per_page=3
```

**Example Response:**
```json
[
  {
    "address": "BLK 401-413, 460-463 HOUGANG AVENUE 10",
    "latitude": 1.37429,
    "longitude": 103.896,
    "total_lots": 693,
    "available_lots": 182
  },
  {
    "address": "BLK 351-357 HOUGANG AVENUE 7",
    "latitude": 1.37234,
    "longitude": 103.899,
    "total_lots": 249,
    "available_lots": 143
  }
]
```

### GET /carparks/import/csv

Manual import carpark data from CSV file (development/testing).

### GET /carparks/import/availability

Manual update carpark availability from external API (development/testing).

### GET /carparks/health

Health check endpoint.

## Quick Start

### Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven 3.6+

### PostgreSQL

- user:   `carpark_user`
- pass:   `carpark_password`
- db:     `carpark_db`

### Running with Docker Compose (Recommended)

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd carparkapi
   ```

2. **Create the CSV data file:**
   Download the HDB carpark information CSV from:
   https://beta.data.gov.sg/datasets/d_23f946fa557947f93a8043bbef41dd09/view

   Place it at: `src/main/resources/data/hdb-carpark-information.csv`

3. **Start PostgreSQL**
   ```bash
   docker-compose up postgres
   ```

4. **Build Application JAR:**
   ```bash
   mvn clean install
   ```
5. **Start the application (and rebuild the Docker image if needed):**
   ```bash
   docker-compose up --build
   ```

5. **Test the API:**
   ```bash
   curl "http://localhost:8080/carparks/nearest?latitude=1.37326&longitude=103.897&page=1&per_page=3"
   ```

### Running Locally

1. **Start PostgreSQL:**
   ```bash
   docker-compose up postgres
   ```

2. **Build and Run the application:**
   ```bash
   ./mvnw clean package
   java -jar target/carparkapi-1.0.0.jar
   ```

## Project Structure

```
src/
├── main/
│   ├── java/com/wego/carparkapi/
│   │   ├── controller/         # REST controllers
│   │   ├── dto/                # Data transfer objects
│   │   ├── model/              # CSV models
│   │   ├── repository/         # Data access layer
│   │   ├── service/            # Business logic
│   │   ├── util/               # Configuration classes
│   └── resources/
│       ├── data/               # CSV data files
│       └── application.yml     # Configuration
└── test/                       # Unit and integration tests
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
app:
   carpark:
      csv:
         file-path: data/hdb-carpark-information.csv
      api:
         url: https://api.data.gov.sg/v1/transport/carpark-availability
         timeout: 10000
```

## Data Sources

### Carpark Information (CSV)
- **Source**: Singapore Government Data Portal
- **URL**: https://beta.data

## Testing

Run all tests:
```bash
./mvnw test jacoco:report
```

## Performance Considerations

### Database Optimization
- **Indexes**: Composite index on (latitude, longitude) for spatial queries
- **Query Optimization**: Native SQL with distance calculation in database
- **Connection Pooling**: HikariCP for efficient connection management

### Caching Strategy
- **Static Data**: Carpark information cached in database
- **API Calls**: Rate limiting and timeout configuration for external API
- **Future Enhancement**: Redis caching for frequently requested locations

### Scalability
- **Horizontal Scaling**: Stateless application design
- **Database Scaling**: Read replicas for query distribution
- **Load Balancing**: Application can run multiple instances behind load balancer

## Monitoring & Observability

### Health Checks
- **Endpoint**: `/carparks/health`
- **Checks**: Database connectivity and data availability
- **Integration**: Ready for Kubernetes liveness/readiness probes

### Logging
- **Framework**: SLF4J with Logback
- **Levels**: Configurable logging levels
- **Format**: Structured logging for better parsing

### Metrics
- **Actuator**: Spring Boot Actuator endpoints enabled
- **Endpoints**: `/actuator/health`, `/actuator/info`, `/actuator/metrics`


## Deployment

### Docker Production Build
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy Maven files for dependency resolution
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create logs directory
RUN mkdir -p /app/logs

# Copy the built jar from your local 'target' folder into the container
COPY target/carparkapi-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]

```

### Error Handling Examples
```bash
  # Missing parameters (400 Bad Request)
  curl "http://localhost:8080/carparks/nearest?latitude=1.37326"

  # Invalid coordinates (400 Bad Request)
  curl "http://localhost:8080/carparks/nearest?latitude=2.0&longitude=103.897"

  # Invalid page number (400 Bad Request)
  curl "http://localhost:8080/carparks/nearest?latitude=1.37326&longitude=103.897&page=0"
```

## Development & AI Tools Used

### AI-Assisted Development
This project was developed with assistance from AI tools as encouraged in the exercise

**Tools Used:**
- **Prompting Techniques**:
   - "Implement coordinate conversion from SVY21 to WGS84"

**Benefits Observed:**
- Documentation template generation

# Check application health
```bash
  curl http://localhost:8080/actuator/health
```

## Logs

- Application logs: `logs/carpark.log`

## Contact

👋 Chester Javier <br />
chesterjavier03@gmail.com <br />
[https://chesterjavier03.vercel.app/](https://chesterjavier03.vercel.app/)<br />
