# System Architecture - Employee Salary Management System

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      User (HR Manager)                       │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ HTTPS
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Angular Frontend                          │
│  ┌─────────────┬──────────────┬──────────────────────────┐ │
│  │ Components  │   Services   │  State Management (RxJS) │ │
│  ├─────────────┼──────────────┼──────────────────────────┤ │
│  │ Dashboard   │ EmployeeAPI  │  Observable Streams      │ │
│  │ List View   │ AnalyticsAPI │  Subject/BehaviorSubject │ │
│  │ Details     │ ReportAPI    │  Error Handling          │ │
│  │ Forms       │ HttpClient   │  Loading States          │ │
│  └─────────────┴──────────────┴──────────────────────────┘ │
│                    Port: 4200                                │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ HTTP REST API
                           ▼
┌─────────────────────────────────────────────────────────────┐
│               Micronaut Backend (Java)                       │
│  ┌─────────────────────────────────────────────────────────┐│
│  │              Controller Layer                            ││
│  │  - EmployeeController (CRUD)                            ││
│  │  - AnalyticsController (Dashboard)                       ││
│  │  - ReportController (Export)                             ││
│  └─────────────────────┬───────────────────────────────────┘│
│                        │                                     │
│  ┌─────────────────────▼───────────────────────────────────┐│
│  │              Service Layer                               ││
│  │  - EmployeeService (Business Logic)                     ││
│  │  - AnalyticsService (Calculations)                       ││
│  │  - ReportService (Data Export)                           ││
│  │  - ValidationService (Data Validation)                   ││
│  └─────────────────────┬───────────────────────────────────┘│
│                        │                                     │
│  ┌─────────────────────▼───────────────────────────────────┐│
│  │         Repository Layer (Micronaut Data)                ││
│  │  - EmployeeRepository                                    ││
│  │  - JPA Criteria Queries                                  ││
│  │  - Custom Query Methods                                  ││
│  └─────────────────────┬───────────────────────────────────┘│
│                        │                                     │
│                    Port: 8080                                │
└────────────────────────┼─────────────────────────────────────┘
                         │
                         │ JDBC
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   SQLite Database                            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                  Employee Table                        │  │
│  │  - id (UUID, PK)                                       │  │
│  │  - employee_code (String, Unique, Indexed)            │  │
│  │  - first_name, last_name                              │  │
│  │  - email (Unique)                                      │  │
│  │  - department, country, job_title                      │  │
│  │  - salary, currency                                    │  │
│  │  - date_joined, experience_years                       │  │
│  │  - gender, performance_rating                          │  │
│  │  - created_at, updated_at, is_active                   │  │
│  └───────────────────────────────────────────────────────┘  │
│                  File: employees.db                          │
└─────────────────────────────────────────────────────────────┘
```

---

## Technology Stack Details

### Backend Stack
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Language | Java | 17+ | Core programming language |
| Framework | Micronaut | 4.x | Application framework |
| ORM | Micronaut Data JPA | Latest | Database access |
| Database | SQLite | 3.x | Data persistence |
| Build Tool | Gradle | 8.x | Build automation |
| Testing | JUnit 5 + Mockito | Latest | Unit testing |
| Validation | Bean Validation | JSR-380 | Input validation |

### Frontend Stack
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Language | TypeScript | 5.x | Type-safe JavaScript |
| Framework | Angular | 17+ | SPA framework |
| UI Library | Angular Material | 17+ | UI components |
| Charts | Chart.js + ng2-charts | Latest | Data visualization |
| State Management | RxJS | 7.x | Reactive programming |
| HTTP Client | Angular HttpClient | Built-in | API communication |
| Testing | Jasmine + Karma | Built-in | Unit testing |

---

## API Design

### REST API Endpoints

#### Employee Management
```
GET    /api/employees              - List employees (paginated, sortable, filterable)
GET    /api/employees/{id}         - Get employee by ID
POST   /api/employees              - Create new employee
PUT    /api/employees/{id}         - Update employee
DELETE /api/employees/{id}         - Delete employee (soft delete)
GET    /api/employees/search       - Search employees
```

#### Analytics
```
GET    /api/analytics/overview     - Dashboard statistics
GET    /api/analytics/salary-distribution - Salary histogram data
GET    /api/analytics/by-department - Department-wise breakdown
GET    /api/analytics/by-country   - Country-wise breakdown
GET    /api/analytics/trends       - Time-series trends
```

#### Reporting
```
GET    /api/reports/export         - Export filtered data as CSV
POST   /api/reports/generate       - Generate custom report
```

### Request/Response Examples

**GET /api/employees?page=0&size=20&sort=lastName,asc&department=Engineering**

Response:
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "employeeCode": "EMP001",
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@acme.com",
      "department": "Engineering",
      "country": "United States",
      "jobTitle": "Senior Software Engineer",
      "salary": 125000.00,
      "currency": "USD",
      "dateJoined": "2020-01-15",
      "gender": "Male",
      "experienceYears": 8,
      "performanceRating": "Exceeds Expectations",
      "isActive": true
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 10000,
  "totalPages": 500
}
```

**GET /api/analytics/overview**

Response:
```json
{
  "totalEmployees": 10000,
  "totalPayroll": 850000000.00,
  "averageSalary": 85000.00,
  "medianSalary": 78000.00,
  "salaryRange": {
    "min": 35000.00,
    "max": 250000.00
  },
  "departmentBreakdown": [
    {"department": "Engineering", "count": 3500, "avgSalary": 95000},
    {"department": "Sales", "count": 2000, "avgSalary": 75000}
  ],
  "countryBreakdown": [
    {"country": "United States", "count": 4500, "avgSalary": 105000},
    {"country": "India", "count": 2500, "avgSalary": 55000}
  ]
}
```

---

## Data Flow

### 1. Employee Creation Flow
```
User fills form → Angular validates → POST /api/employees → 
Controller receives DTO → Service validates business rules → 
Repository saves to DB → Response with created employee → 
UI updates and shows success message
```

### 2. Dashboard Load Flow
```
User navigates to dashboard → Component calls AnalyticsService → 
Multiple parallel API calls (overview, distribution, charts) → 
Services aggregate data → RxJS combines responses → 
Charts render with Chart.js → Loading spinner dismissed
```

### 3. Search Flow
```
User types in search box → Debounce 300ms → 
GET /api/employees/search?q=term → 
Repository executes LIKE query with indexes → 
Results paginated and returned → Table updates
```

---

## Security Considerations

### Current Implementation (Assessment Scope)
- **Input Validation**: Bean Validation on all DTOs
- **SQL Injection Prevention**: Parameterized queries via JPA
- **Error Handling**: Generic error messages, no stack traces exposed
- **CORS**: Configured for localhost development

### Production Enhancements (Out of Scope)
- OAuth2/JWT authentication
- Role-based access control (RBAC)
- Rate limiting and throttling
- HTTPS enforcement
- PII encryption at rest
- Audit logging
- CSRF protection

---

## Performance Considerations

### Database Optimizations
- **Indexes**: Created on frequently queried columns
  - `employee_code` (unique index)
  - `email` (unique index)
  - `department`, `country` (composite index)
  - `salary` (range queries)
- **Pagination**: Server-side pagination to handle 10K records
- **Query Optimization**: JPA Criteria API for dynamic queries
- **Connection Pooling**: HikariCP for efficient connection management

### Frontend Optimizations
- **Lazy Loading**: Feature modules loaded on demand
- **Virtual Scrolling**: For large lists (alternative to pagination)
- **Debouncing**: Search input debounced to reduce API calls
- **Caching**: HTTP interceptor with caching strategy
- **OnPush Change Detection**: For improved rendering performance
- **Ahead-of-Time (AOT) Compilation**: Faster initial load

### Caching Strategy
- **In-Memory Cache**: Analytics data cached for 5 minutes
- **Browser Cache**: Static assets cached
- **ETag Support**: Conditional requests for unchanged data

---

## Testing Strategy

### Backend Testing
```
Unit Tests (JUnit 5)
├── Service Layer Tests (Business Logic)
│   ├── EmployeeServiceTest
│   ├── AnalyticsServiceTest
│   └── ReportServiceTest
├── Repository Tests (Data Access)
│   └── EmployeeRepositoryTest
└── Controller Tests (API Endpoints)
    ├── EmployeeControllerTest
    └── AnalyticsControllerTest

Integration Tests
├── Full API endpoint tests
├── Database transaction tests
└── Seed data validation
```

### Frontend Testing
```
Unit Tests (Jasmine)
├── Component Tests
│   ├── DashboardComponent
│   ├── EmployeeListComponent
│   └── EmployeeFormComponent
├── Service Tests
│   ├── EmployeeApiService
│   └── AnalyticsApiService
└── Pipe/Directive Tests

E2E Tests (Cypress - optional)
└── Critical user flows
```

### Test Coverage Goals
- **Backend**: Minimum 80% code coverage
- **Frontend**: Minimum 70% code coverage
- **Critical Paths**: 100% coverage

---

## Deployment Architecture

### Development Environment
```
localhost:4200 (Angular Dev Server) 
    ↓
localhost:8080 (Micronaut Application)
    ↓
./data/employees.db (SQLite file)
```

### Build & Run Commands
```bash
# Backend
cd backend
./gradlew run

# Frontend  
cd frontend
npm install
ng serve

# Seed Data
./gradlew seedData
```

---

## Error Handling Strategy

### Backend Error Handling
```java
@Error(global = true)
public class GlobalExceptionHandler {
    @Error(status = HttpStatus.NOT_FOUND)
    public HttpResponse<?> notFound(EmployeeNotFoundException e) {
        return HttpResponse.notFound(new ErrorResponse(e.getMessage()));
    }
    
    @Error(status = HttpStatus.BAD_REQUEST)
    public HttpResponse<?> validationError(ConstraintViolationException e) {
        return HttpResponse.badRequest(new ValidationErrorResponse(e));
    }
}
```

### Frontend Error Handling
```typescript
// HTTP Interceptor for global error handling
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        // Show user-friendly error message
        this.snackBar.open(this.getErrorMessage(error), 'Close', {duration: 5000});
        return throwError(() => error);
      })
    );
  }
}
```

---

## Design Patterns Used

1. **Layered Architecture**: Controller → Service → Repository
2. **Dependency Injection**: Micronaut DI, Angular DI
3. **Repository Pattern**: Data access abstraction
4. **DTO Pattern**: Data transfer between layers
5. **Builder Pattern**: Complex object construction
6. **Strategy Pattern**: Different reporting strategies
7. **Observer Pattern**: RxJS observables for state management
8. **Singleton Pattern**: Services in Angular
9. **Factory Pattern**: Entity creation with validation

---

## Monitoring & Observability (Future)

### Metrics to Track
- API response times (p50, p95, p99)
- Database query performance
- Error rates by endpoint
- User session duration
- Most accessed features

### Tools
- Micrometer (metrics)
- SLF4J + Logback (logging)
- Azure Application Insights (production)

---

## CI/CD Pipeline (Future)

```yaml
Build → Test → Quality Gate → Package → Deploy

1. Code commit
2. Run tests (backend + frontend)
3. Check code coverage (>80%)
4. SonarQube analysis
5. Build Docker image
6. Deploy to staging
7. Run E2E tests
8. Manual approval for production
9. Deploy to production
10. Health check verification
```

---

## Scalability Considerations

### Current Limitations (10K employees)
- SQLite suitable for read-heavy workload
- Single-server deployment sufficient
- In-memory caching effective

### Future Scaling (100K+ employees)
- Migrate to PostgreSQL or Azure SQL
- Implement Redis for distributed caching
- Add read replicas for analytics queries
- Introduce message queue for async operations
- Containerize with Kubernetes
- CDN for frontend assets

---

## Conclusion

This architecture provides a solid foundation for a production-grade salary management system while remaining focused on demonstrating core engineering skills. The design is intentionally pragmatic, balancing simplicity with extensibility, and showcases modern development practices using AI-assisted tooling.
