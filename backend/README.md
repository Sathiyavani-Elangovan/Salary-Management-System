# Salary Management System - Backend

## Technology Stack

- **Framework**: Micronaut 4.x
- **Language**: Java 17
- **Database**: SQLite 3.x
- **ORM**: Micronaut Data JPA with Hibernate
- **Build Tool**: Gradle 8.x
- **Testing**: JUnit 5 + Mockito

## Prerequisites

- Java 17 or higher
- Gradle 8.x (or use the Gradle wrapper)

## Getting Started

### 1. Build the Project

```bash
./gradlew build
```

### 2. Run the Application

```bash
./gradlew run
```

The application will start on `http://localhost:8080`

### 3. Database Seeding

On first startup, the application automatically:
- Seeds the database with 10,000 employee records
- Creates a default HR user: `hruser` / `hr123`

The SQLite database file will be created at `./data/employees.db`

## API Endpoints

### Authentication

- `POST /api/auth/login` - User login
  - Body: `{ "username": "hruser", "password": "hr123" }`
  - Returns: JWT token and user details
- `POST /api/auth/logout` - User logout
  - Creates audit log entry
- `GET /api/auth/validate` - Validate current token

### Employee Management

- `GET /api/employees` - List all employees (paginated)
  - Query params: `page`, `size`, `sort`
- `GET /api/employees/{id}` - Get employee by ID
- `GET /api/employees/search` - Search employees
  - Query params: `searchTerm`, `department`, `country`, `minSalary`, `maxSalary`, `page`, `size`
- `POST /api/employees` - Create new employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee (soft delete)
- `POST /api/employees/bulk-salary-adjustment` - Bulk salary adjustment
  - Body: List of employee IDs
  - Query param: `percentage`

### Analytics

- `GET /api/analytics/overview` - Get dashboard analytics
  - Returns: total employees, salary statistics, department breakdown, country breakdown, salary distribution

### Audit Trail

- `GET /api/audit` - Get all audit logs (paginated)
  - Query params: `page`, `size`
- `GET /api/audit/entity/{type}/{id}` - Get audit logs for specific entity
- `GET /api/audit/user/{userId}` - Get audit logs for specific user
- `GET /api/audit/action/{action}` - Get audit logs by action type (LOGIN, LOGOUT, CREATE, UPDATE, DELETE)

## Running Tests

```bash
./gradlew test
```

View test reports in `build/reports/tests/test/index.html`

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/acme/salary/
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── service/        # Business logic
│   │   │   ├── repository/     # Data access layer
│   │   │   ├── model/          # JPA entities
│   │   │   ├── dto/            # Data transfer objects
│   │   │   ├── util/           # Utilities (data seeder)
│   │   │   └── Application.java
│   │   └── resources/
│   │       └── application.yml # Configuration
│   └── test/
│       └── java/com/acme/salary/
│           └── service/        # Unit tests
├── build.gradle                # Gradle configuration
└── README.md
```

## Configuration

Edit `src/main/resources/application.yml` to configure:
- Server port
- Database connection
- CORS settings
- Logging levels

## Features

- ✅ Full CRUD operations for employee management
- ✅ Advanced search and filtering
- ✅ Paginated API responses
- ✅ Salary analytics and statistics
- ✅ Bulk salary adjustments
- ✅ Input validation with Bean Validation
- ✅ Soft delete for employees
- ✅ Automatic database seeding (10,000 employees)
- ✅ Comprehensive unit tests
- ✅ CORS enabled for frontend integration

## Development

### Adding New Endpoints

1. Create DTO in `dto/` package
2. Add method to appropriate service
3. Create controller endpoint
4. Write unit tests

### Database Schema

The `Employee` entity automatically creates the following table with indexes:
- Primary key: UUID
- Unique indexes: employee_code, email
- Composite index: department + country
- Single index: salary

## Production Considerations

For production deployment, consider:
- Migrate to PostgreSQL or MySQL
- Add authentication/authorization (OAuth2, JWT)
- Implement caching (Redis)
- Add API rate limiting
- Enable HTTPS
- Set up monitoring and logging
- Containerize with Docker

## License

Copyright © 2026 ACME Corporation
