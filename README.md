# Task Management Application

A Spring Boot-based task management system with JWT authentication and user authorization features.

## Features

- **User Authentication**: JWT-based secure authentication system
- **Task Management**: Create, read, update, and delete tasks
- **User Authorization**: Role-based access control
- **RESTful API**: Clean API endpoints for all operations
- **Exception Handling**: Global exception handling with proper error responses
- **Security Configuration**: Custom security configuration with JWT filters

## Technology Stack

- **Backend**: Spring Boot
- **Security**: Spring Security with JWT
- **Database**: (Configured in application.properties)
- **Build Tool**: Maven (mvnw)
- **Authentication**: JWT (JSON Web Tokens)

## Project Structure

```
src/main/java/com/example/
├── Application.java                 # Main application class
├── config/
│   ├── AppConfig.java              # Application configuration
│   ├── JwtAuthenticationFilter.java # JWT authentication filter
│   ├── JwtUtil.java                # JWT utility methods
│   ├── SecurityConfig.java         # Security configuration
│   └── GlobalExceptionHandler.java # Global exception handling
├── model/
│   ├── Task.java                   # Task entity
│   ├── TaskResponse.java           # Task response DTO
│   ├── User.java                   # User entity
│   └── TokenBlacklist.java         # Blacklisted tokens
└── repository/
    ├── TaskRepository.java         # Task data access layer
    └── UserRepository.java         # User data access layer
└── service/
    ├── TaskService.java            # Task business logic
    └── UserService.java            # User business logic
```

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd demo
   ```

2. **Configure Git** (if not already done)
   ```bash
   git config user.name "Your Name"
   git config user.email "your.email@example.com"
   ```

3. **Configure database** in `application.properties`

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/logout` - User logout

### Task Endpoints
- `GET /api/tasks` - Get all tasks (Authenticated users)
- `POST /api/tasks` - Create a new task (Authenticated users)
- `GET /api/tasks/{id}` - Get task by ID (Authenticated users)
- `PUT /api/tasks/{id}` - Update task (Task owner/Admin)
- `DELETE /api/tasks/{id}` - Delete task (Task owner/Admin)

## Testing API Endpoints with Postman

### 1. User Registration
**Endpoint:** `POST http://localhost:8080/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
    "username": "testuser",
    "password": "password123",
    "email": "testuser@example.com"
}
```

### 2. User Login
**Endpoint:** `POST http://localhost:8080/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
    "username": "testuser",
    "password": "password123"
}
```

**Response:** You'll receive a JWT token in the response. Save this token for authenticated requests.

### 3. Create a Task (Authenticated)
**Endpoint:** `POST http://localhost:8080/api/tasks`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <your-jwt-token>
```

**Body (raw JSON):**
```json
{
    "title": "Complete project documentation",
    "description": "Write detailed documentation for the task management API",
    "dueDate": "2023-12-31"
}
```

### 4. Get All Tasks
**Endpoint:** `GET http://localhost:8080/api/tasks`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

### 5. Update a Task
**Endpoint:** `PUT http://localhost:8080/api/tasks/{taskId}`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <your-jwt-token>
```

**Body (raw JSON):**
```json
{
    "title": "Updated task title",
    "description": "Updated description",
    "completed": true
}
```

### 6. Logout
**Endpoint:** `POST http://localhost:8080/api/auth/logout`

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

## Postman Collection Setup

1. **Create a new Collection** named "Task Management API"
2. **Add environment variables**:
   - `baseUrl`: `http://localhost:8080`
   - `jwtToken`: (will be set automatically after login)

3. **Add a pre-request script** for authenticated endpoints:
```javascript
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get('jwtToken')
});
```

4. **Add tests** to automatically capture the JWT token after login:
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set('jwtToken', jsonData.token);
}
```

## Security Features

- JWT-based authentication
- Password encryption
- Token blacklisting for logout functionality
- Role-based authorization
- Secure API endpoints

## Development

This project uses Maven wrapper (mvnw) for dependency management and building. The application is configured with proper security settings and exception handling for production-ready deployment.

## License

This project is licensed under the MIT License.
