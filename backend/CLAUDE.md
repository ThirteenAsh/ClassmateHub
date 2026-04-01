# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ClassmateHub Backend** - A complete classmate management system backend built with Spring Boot 3, MyBatis, and Sa-Token authentication.

**Current Version**: v1.0.2  
**Java Version**: 17  
**Framework**: Spring Boot 3.5.10

## Quick Start Commands

### Build & Run
```bash
# Clean and build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the built JAR
java -jar target/backend-1.0.2.jar

# Quick start script (Windows)
RunBackend.bat
```

### Development
```bash
# Compile the project
mvn clean compile

# Run with hot reload (devtools not configured, restart required)
mvn spring-boot:run

# Run tests
mvn test

# Run a single test
mvn test -Dtest=ClassName#methodName
```

### Database
```bash
# Initialize database (run db.sql)
mysql -u root -p classmatehub < db.sql
```

## Project Architecture

### Layered Architecture
```
Controller → Service → Mapper → Database
```

### Core Components

**Controllers** (`src/main/java/com/thirteenash/controller/`)
- `AuthController` - User authentication (login, register, logout, password change)
- `ClassController` - Class management operations
- `StudentProfileController` - Student information CRUD
- `ProfileController` - Personal center management
- `SquareController` - Public square functionality
- `AdminController` - Admin operations

**Services** (`src/main/java/com/thirteenash/service/`)
- Interface-based service layer with `impl/` containing implementations
- `StpInterfaceImpl` - Sa-Token permission implementation

**Mappers** (`src/main/java/com/thirteenash/mapper/`)
- MyBatis mapper interfaces for database operations
- XML mappings in `src/main/resources/mapper/`

**Entities** (`src/main/java/com/thirteenash/entity/`)
- `Users` - User entity with authentication fields
- `Clazz` - Class entity
- `StudentProfile` - Student profile entity

**DTOs** (`src/main/java/com/thirteenash/dto/`)
- Request DTOs for API input validation
- Response DTOs for API output formatting

**Common Components** (`src/main/java/com/thirteenash/common/`)
- `GlobalExceptionHandler` - Centralized exception handling
- `Result` - Unified API response format
- `BusinessException` - Custom business exceptions

## Configuration

### Database Configuration
**File**: `src/main/resources/application.yml`
- MySQL database: `jdbc:mysql://localhost:3306/classmatehub`
- Default credentials: root/123456
- Connection pool: HikariCP with 20 max pool size

### API Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### API Endpoints Structure
All APIs use `/api/v1` prefix:
- `/api/v1/auth/*` - Authentication endpoints
- `/api/v1/classes/*` - Class management
- `/api/v1/students/*` - Student profile management
- `/api/v1/square` - Public square
- `/api/v1/admin/*` - Admin operations

## Security & Authentication

**Sa-Token** implementation provides:
- Token-based authentication
- Role-based access control (user/admin roles)
- Session management

**Authentication Flow**:
1. User login → Token generation
2. Token validation via Sa-Token interceptor
3. Permission checks for role-based access

## Database Schema

### Tables
- `users` - User authentication and role data
- `clazz` - Class information
- `student_profile` - Student detailed profiles

### Key Relationships
- `users.classId` → `clazz.id`
- `student_profile.user_id` → `users.id`
- `student_profile.clazz_id` → `clazz.id`

## Development Guidelines

### Code Style
- **Indentation**: 4 spaces
- **Method length**: < 50 lines
- **Class length**: < 500 lines
- **Naming**: PascalCase for classes, camelCase for methods/variables

### Exception Handling
- Use `BusinessException` for business logic errors
- All exceptions handled by `GlobalExceptionHandler`
- Never catch and silently return errors in business code

### Response Format
```json
{
  "code": 200,
  "message": "Success message",
  "data": { ... }
}
```

### MyBatis Best Practices
- Mapper interfaces in `com.thirteenash.mapper`
- XML mappings in `resources/mapper/`
- Entity aliases configured in `application.yml`
- CamelCase field mapping enabled

## Common Development Tasks

### Adding a New API Endpoint
1. Create request/response DTOs in `dto/`
2. Add controller method in appropriate controller
3. Create service interface and implementation
4. Add mapper method and XML SQL
5. Test via Swagger UI

### Database Changes
1. Modify `db.sql` for schema changes
2. Update corresponding entity classes
3. Adjust mapper XML files
4. Rebuild and test

### Adding Authentication
- Use `@SaCheckLogin` for login-required endpoints
- Use `@SaCheckRole("admin")` for admin-only access
- Custom permissions in `StpInterfaceImpl`

## Testing
- Unit tests: `src/test/java/`
- Run with: `mvn test`
- MyBatis provides integration testing support

## Troubleshooting

### Common Issues
- **Database connection**: Check MySQL service and credentials in `application.yml`
- **Port conflict**: Change `server.port` in `application.yml`
- **MyBatis errors**: Enable debug logging in `application.yml`
- **Authentication failures**: Verify Sa-Token configuration in `SaTokenConfigure`

### Logs
- MyBatis SQL logging: DEBUG level for `com.thirteenash.mapper`
- Spring Web logging: DEBUG level for `org.springframework.web`