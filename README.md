# Complex Todo List API

A comprehensive RESTful API for managing todos with advanced features including user authentication, categories, subtasks, recurring tasks, reminders, and advanced filtering capabilities.

## Features

- User authentication with JWT tokens
- Todo management with categories, priorities, and statuses
- Subtask support
- Recurring tasks (daily, weekly, monthly, yearly)
- Reminder notifications
- Advanced filtering and search capabilities
- Data validation and error handling
- RESTful API design

## Technologies Used

- Java 17
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- MySQL 8.0+
- Maven
- Lombok
- JWT for authentication
- Hibernate Validator

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd first
   ```

2. Configure the database in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/todo_list?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=
   ```

3. Create the database:
   ```sql
   CREATE DATABASE todo_list;
   ```

4. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

### User Profile

- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile

### Categories

- `POST /api/categories` - Create a new category
- `GET /api/categories` - Get all categories for the current user
- `GET /api/categories/{id}` - Get a specific category
- `PUT /api/categories/{id}` - Update a category
- `DELETE /api/categories/{id}` - Delete a category

### Todos

- `POST /api/todos` - Create a new todo
- `GET /api/todos` - Get all todos with filtering options
- `GET /api/todos/{id}` - Get a specific todo
- `PUT /api/todos/{id}` - Update a todo
- `DELETE /api/todos/{id}` - Delete a todo
- `GET /api/todos/{id}/subtasks` - Get subtasks for a todo
- `POST /api/todos/{id}/subtasks` - Create a subtask
- `GET /api/todos/date-range` - Get todos within a date range
- `GET /api/todos/overdue` - Get overdue todos

### Reminders

- `POST /api/reminders` - Create a new reminder
- `GET /api/reminders` - Get all reminders for the current user
- `GET /api/reminders/{id}` - Get a specific reminder
- `PUT /api/reminders/{id}` - Update a reminder
- `DELETE /api/reminders/{id}` - Delete a reminder

## Filtering and Search

The todos endpoint supports advanced filtering:

- `status` - Filter by status (PENDING, IN_PROGRESS, COMPLETED)
- `priority` - Filter by priority (LOW, MEDIUM, HIGH, URGENT)
- `categoryId` - Filter by category ID
- `search` - Search in title or description
- `dueDateFrom` - Filter by due date range start (ISO format)
- `dueDateTo` - Filter by due date range end (ISO format)
- `sortBy` - Sort field (dueDate, priority, createdAt, etc.)
- `sortOrder` - Sort order (asc or desc)
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)

Example:
```
GET /api/todos?status=COMPLETED&priority=HIGH&search=meeting&sortBy=dueDate&sortOrder=asc&page=0&size=20
```

## Data Models

### User

```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2023-01-01T10:00:00",
  "updatedAt": "2023-01-01T10:00:00"
}
```

### Category

```json
{
  "id": 1,
  "name": "Work",
  "description": "Work-related tasks",
  "color": "#FF0000",
  "createdAt": "2023-01-01T10:00:00"
}
```

### Todo

```json
{
  "id": 1,
  "title": "Complete project",
  "description": "Finish the Spring Boot project",
  "status": "IN_PROGRESS",
  "priority": "HIGH",
  "dueDate": "2023-12-31T18:00:00",
  "reminderTime": "2023-12-31T17:00:00",
  "category": {
    "id": 1,
    "name": "Work"
  },
  "isRecurring": true,
  "recurrencePattern": "WEEKLY",
  "recurrenceEndDate": "2024-12-31T18:00:00",
  "createdAt": "2023-01-01T10:00:00",
  "updatedAt": "2023-01-01T10:00:00"
}
```

### Reminder

```json
{
  "id": 1,
  "todo": {
    "id": 1,
    "title": "Complete project"
  },
  "remindAt": "2023-12-31T17:00:00",
  "isSent": false,
  "createdAt": "2023-01-01T10:00:00"
}
```

## Authentication

Most endpoints require authentication via JWT token. After logging in, include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## Recurring Tasks

Recurring tasks automatically generate new instances based on their pattern:
- DAILY: Creates a new instance every day
- WEEKLY: Creates a new instance every week
- MONTHLY: Creates a new instance every month
- YEARLY: Creates a new instance every year

## Error Handling

The API returns appropriate HTTP status codes and error messages:

- 400 Bad Request: Validation errors
- 401 Unauthorized: Missing or invalid authentication
- 404 Not Found: Resource not found
- 500 Internal Server Error: Unexpected errors

Example error response:
```json
{
  "timestamp": "2023-01-01T10:00:00",
  "status": 400,
  "errors": {
    "title": "Title is required",
    "description": "Description must be less than 1000 characters"
  }
}
```

## Testing

Run the tests with Maven:
```bash
mvn test
```

## License

This project is licensed under the MIT License.