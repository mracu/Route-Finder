# Route Finder

Simple Spring Boot service that calculates a land route between two countries
using border information from a countries dataset.  
Countries are identified by the `cca3` code.

The application models countries as an unweighted graph and uses a
**Breadth-First Search (BFS)** algorithm to determine the shortest land route
(minimum number of border crossings).

---

## Requirements

- Java 21+
- Maven 3.9+
- (Optional) Docker + Docker Compose

---

## Build

Run verification and tests:

```bash
mvn clean verify
```

---

## Run (Local – Maven)

### Start the application:

```bash
mvn spring-boot:run
```

## Application URL:

```bash
http://localhost:8080
```

---

## Run with Docker Compose

### Start:

```bash
docker compose up --build
```

### Stop:

```bash
docker compose down
```
