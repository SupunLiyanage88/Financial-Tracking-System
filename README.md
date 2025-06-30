# Financial Tracking System

# Note: This is a deployment copy of the Financial Tracking System. The original repository is maintained in a private GitHub Classroom repository and is not publicly accessible.

About This Version
Purpose: Public hosting for demonstration and deployment

Status: Mirrors the production-ready version of the application

Development: All active development occurs in the private classroom repository

A RESTful API for tracking personal or business finances built with Spring Boot and Java 17.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Database Configuration](#database-configuration)
- [API Documentation](#api-documentation)
- [Running Tests](#running-tests)
- [Test Environment Setup](#test-environment-setup)
- [Deployment](#deployment)

## Prerequisites

- Java 17 JDK
- Maven 3.8+
- MongoDB
- Git

## Setup Instructions

### 1. Clone the repository

```bash
git clone https://github.com/SupunLiyanage88/financial-tracking-system.git
cd financial-tracking-system
```

### 2. Build the application

Using Maven:
```bash
mvn clean install
```


### 3. Run the application

Using Maven:
```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080

## Database Configuration

Configure your database connection in `src/main/resources/application.properties`:

```properties
spring.application.name=financeTrackerSystem
spring.data.mongodb.uri=mongodb+srv://admin:admin12@cluster01.bsefg.mongodb.net/FT_db?retryWrites=true&w=majority&appName=Cluster01
```

## API Documentation

### Authentication Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| POST | `/api/auth/register` | Register a new user | `{"username": "user", "password": "pass", "email": "user@example.com"}` | `{"id": 1, "username": "user", "email": "user@example.com", "token": "jwt_token"}` |
| POST | `/api/auth/login` | Login a user | `{"username": "user", "password": "pass"}` | `{"token": "jwt_token"}` |

### Transaction Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/get-all-transactions` | Get all transactions | - | List of transactions |
| GET | `/api/get-my-transactions` | Get transaction by ID | - | Transaction details |
| POST | `/api/add-user-transaction` | Create a new transaction | `{"amount": 100.00, "description": "Grocery", "category": "EXPENSE", "date": "2025-03-11"}` | Created transaction |
| PUT | `/api/update-transaction/{transactionId}` | Update a transaction | `{"amount": 150.00, "description": "Updated Grocery", "category": "EXPENSE", "date": "2025-03-11"}` | Updated transaction |
| DELETE | `/api/delete-transaction/{transactionId}/{verify}` | Delete a transaction | - | Status message |

### Category Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/categories` | Get all categories | - | List of categories |
| GET | `/api/categories/{id}` | Get category by ID | - | Category details |
| POST | `/api/categories` | Create a new category | `{"name": "Groceries", "type": "EXPENSE"}` | Created category |
| PUT | `/api/categories/{id}` | Update a category | `{"name": "Food", "type": "EXPENSE"}` | Updated category |
| DELETE | `/api/categories/{id}` | Delete a category | - | Status message |

### Goals Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/categories` | Get all goals | - | List of goals |
| GET | `/api/get-goals/{username}` | Get goals by ID | - | goals details |
| POST | `/api/create-goal` | Create a new goals | `{"name": "Groceries", "type": "EXPENSE"}` | Created goals |
| PUT | `/api/update-goal/{goalId}` | Update a goal | `{"name": "Food", "type": "EXPENSE"}` | Updated goal |
| DELETE | `/api/delete/{goalId}` | Delete a goal | - | Status message |


### Admin Dashboard Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/admin/users` | Admin Dashboard | - | Users all details |

### User Dashboard Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/user-dashboard/summary` | User Summery | - | Users all summery |
| GET | `/api/user-dashboard/transactions` | User Transaction | - | Users all transaction |


### Exchange Currency Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/exchange-my-transactions` | Exchange Currency | - | Exchanged Currency |

### Budget Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/get-all-budgets` | Get all budget | - | List of budget |
| GET | `/api/check-budget` | Check budget status | - | Check budget status |
| GET | `/api/budget-recommends` | Get budget recommends | - | Get budget recommends |
| GET | `/api/get-user-budget` | Get budget by ID | - | budget details |
| POST | `/api/create-budget` | Create a new budget | `{"name": "Groceries", "type": "EXPENSE"}` | Created budget |
| PUT | `/api/update-budget/{budgetId}` | Update a budget | `{"name": "Food", "type": "EXPENSE"}` | Updated goal |
| DELETE | `/api/delete-budget/{budgetId}/{verify}` | Delete a budget | - | Status message |




### Subscription Endpoints

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/categories` | Get all subscription | - | List of subscription |
| GET | `/api/categories/{id}` | Get subscription by ID | - | subscription details |
| POST | `/api/add-subscription` | Create a new subscription | `{"name": "Groceries", "type": "EXPENSE"}` | Created subscription |
| PUT | `/api/update-subscription/{subscriptionId}` | Update a subscription | `{"name": "Food", "type": "EXPENSE"}` | Updated subscription |
| DELETE | `/api/delete-subscription/{subscriptionId}/{verify}` | Delete a subscription | - | Status message |
| Email Test | `/api/test-send-email/{subscriptionId}` | Email Subscription | - | Status message |

### Report Endpoints

| Method | URL | Description | Request Params | Response |
|--------|-----|-------------|--------------|----------|
| GET | `/api/report-generate` | Get monthly summary | `?year=2025&month=3` | Monthly report data |
| GET | `/api/report-generate` | Get category summary | `?startDate=2025-01-01&endDate=2025-03-31` | Category breakdown |
| GET | `/api/report-generate` | Get spending trends | `?months=6` | Trend data for last 6 months |

## Running Tests

### Unit Tests

Unit test available for

\src\test\java\com\financeTracker\financeTrackerSystem\service\AdminDashboardServiceTest.java
\src\test\java\com\financeTracker\financeTrackerSystem\service\AuthServiceTest.java
\src\test\java\com\financeTracker\financeTrackerSystem\service\BudgetServiceTest.java
\src\test\java\com\financeTracker\financeTrackerSystem\service\CategoryServiceTest.java
\src\test\java\com\financeTracker\financeTrackerSystem\service\CurrencyExchangeServiceTest.java
\src\test\java\com\financeTracker\financeTrackerSystem\service\JWTServiceTest.java
\src\test\java\com\financeTracker\financeTrackerSystem\service\ReportServiceTest.java
\src\test\java\com\financeTracker\financeTrackerSystem\service\SubscriptionServiceTest.java

Using Maven:
```bash
mvn test
```


### Integration Tests

Using Maven:
```bash
mvn verify
```


### Performance Tests

```bash
mvn gatling:test
```

## Test Environment Setup

### Integration Test Configuration

Create a test configuration file at `src/test/resources/application-test.properties`:

```properties
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
spring.jpa.hibernate.ddl-auto=create-drop
```

### Performance Test Setup

1. Install Gatling for performance testing
2. Configure test scenarios in `src/test/resources/gatling/simulations`
3. Run with the Maven Gatling plugin

