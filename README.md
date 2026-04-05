# Finance Dashboard Backend

A Spring Boot backend for a finance dashboard system that manages users, financial records, dashboard analytics, and backend role-based access control.

This project was built to match the assignment requirements around:

- User and role management
- Financial records CRUD
- Filtering and pagination
- Dashboard summary APIs
- Role-based access control
- Validation and error handling
- Persistent storage

## Tech Stack

- Java 21
- Spring Boot 3.4
- Spring Web
- Spring Data JPA
- Spring Security
- H2 database
- Springdoc OpenAPI / Swagger UI
- JUnit 5 + MockMvc

## Why This Design

The goal was to keep the submission clear and easy to run locally while still demonstrating sound backend architecture.

- `Spring Boot` provides clean API layering and fast setup for REST, validation, persistence, and security.
- `H2 file-based storage` keeps the project simple to run with no external database setup while still providing actual persistence.
- `Mock header-based authentication` avoids adding full JWT complexity while still enforcing backend authorization rules in a real way.
- `Swagger UI` and a `Postman collection` make the API easy to review during evaluation.

## Role Model

- `VIEWER`
  - Can read dashboard summary data
  - Cannot access financial records CRUD
  - Cannot manage users
- `ANALYST`
  - Can read financial records
  - Can access dashboard summary data
  - Cannot create, update, or delete records
  - Cannot manage users
- `ADMIN`
  - Full access to user management
  - Full access to financial record CRUD
  - Full access to dashboard summary data

## Mock Authentication

To keep local setup simple, the API uses the request header `X-User-Id` to simulate authentication.

Seeded users:

- `1` -> `Alice Admin` (`ADMIN`)
- `2` -> `Brian Analyst` (`ANALYST`)
- `3` -> `Vera Viewer` (`VIEWER`)

Example:

```bash
curl -H "X-User-Id: 1" http://localhost:8080/api/users
```

## Project Structure

```text
src/main/java/com/finance/dashboard
|- config
|- controller
|- dto
|- exception
|- model
|- repository
|- security
|- service
```

## Main Features

### 1. User Management

- Create users
- List users
- Update users
- Manage role and active/inactive status
- Prevent duplicate emails
- Prevent an admin from deactivating themselves

### 2. Financial Records

Each record contains:

- `amount`
- `type` (`INCOME` or `EXPENSE`)
- `category`
- `recordDate`
- `notes`
- `createdAt`
- `createdByName`

Supported operations:

- Create record
- List records
- Update record
- Delete record
- Filter by `type`, `category`, `from`, and `to`
- Pagination through Spring `Pageable`

### 3. Dashboard Summary API

The summary endpoint returns:

- Total income
- Total expenses
- Net balance
- Category-wise totals
- Monthly trends
- Recent activity

### 4. Validation and Error Handling

- Request body validation using Jakarta Validation
- Standard HTTP status codes
- Structured JSON errors
- Protection against invalid filters and invalid record operations

## API Endpoints

### Health

- `GET /api/health`

### Users

- `GET /api/users`
- `GET /api/users/me`
- `POST /api/users`
- `PUT /api/users/{id}`

### Financial Records

- `GET /api/records`
- `POST /api/records`
- `PUT /api/records/{id}`
- `DELETE /api/records/{id}`

### Dashboard

- `GET /api/dashboard/summary`

## Example Requests

### Get dashboard summary as viewer

```bash
curl -H "X-User-Id: 3" http://localhost:8080/api/dashboard/summary
```

### Get records as analyst

```bash
curl -H "X-User-Id: 2" "http://localhost:8080/api/records?type=EXPENSE&from=2026-01-01&to=2026-12-31"
```

### Create a record as admin

```bash
curl -X POST http://localhost:8080/api/records \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "amount": 150.75,
    "type": "EXPENSE",
    "category": "Utilities",
    "recordDate": "2026-04-01",
    "notes": "Electricity bill"
  }'
```

## How To Run

### Requirements

- Java 21
- Maven 3.9+

### Start the application

```bash
mvn spring-boot:run
```

The API will be available at:

- `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

If you open the H2 console, use:

- JDBC URL: `jdbc:h2:file:./data/finance-db;AUTO_SERVER=TRUE`
- Username: `sa`
- Password: empty

## How To Test

```bash
mvn test
```

## Implementation Summary

1. I started with the domain model: users, roles, statuses, record types, and financial records.
2. I added JPA repositories to persist users and records in H2.
3. I created DTOs so the API contract stays separate from the entity model.
4. I implemented service classes for user management, record management, filtering, and dashboard aggregation.
5. I added Spring Security with a custom header authentication filter based on `X-User-Id`.
6. I configured endpoint-level access control so each role has clearly defined capabilities.
7. I added global exception handling for clean validation and error responses.
8. I seeded sample users and financial data for quick evaluation.
9. I added Swagger/OpenAPI support and a Postman collection for easier review.
10. I wrote integration tests for the highest-value access-control and write flows.

## Assumptions and Trade-offs

- Authentication is mocked using `X-User-Id` instead of JWT to keep the assignment simple and evaluator-friendly.
- H2 is used for persistence to avoid external setup and keep the project runnable in minutes.
- Category totals aggregate across all matching records regardless of income/expense split to keep the main summary compact.
- User delete was intentionally not added because user status management already demonstrates lifecycle control and avoids audit ambiguity.

## Demo Resources

- Demo walkthrough: `docs/demo-script.md`
- Submission form answers: `docs/submission-form.md`
- Postman collection: `docs/postman_collection.json`
- Long implementation summary: `docs/implementation-summary.md`
