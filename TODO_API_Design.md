# Complex Todo List API - Design Document

## Overview
This document outlines the architecture and design for a complex Todo List API using Spring Boot with MySQL database. The API will include features such as user authentication, categories, subtasks, recurring tasks, reminders, and advanced filtering capabilities.

## Database Configuration
- Database: MySQL
- Database Name: todo_list
- Username: root
- Password: (none)

## Entity Models

### User
- id (Long): Primary key
- username (String): Unique identifier
- email (String): User email
- password (String): Encrypted password
- createdAt (LocalDateTime): Account creation timestamp
- updatedAt (LocalDateTime): Last update timestamp

### Category
- id (Long): Primary key
- name (String): Category name
- description (String): Optional description
- color (String): Color code for UI representation
- userId (Long): Foreign key to User
- createdAt (LocalDateTime): Creation timestamp

### Todo
- id (Long): Primary key
- title (String): Todo title
- description (String): Detailed description
- status (Enum): PENDING, IN_PROGRESS, COMPLETED
- priority (Enum): LOW, MEDIUM, HIGH, URGENT
- dueDate (LocalDateTime): Due date and time
- reminderTime (LocalDateTime): Optional reminder time
- categoryId (Long): Foreign key to Category
- userId (Long): Foreign key to User
- parentId (Long): Self-referencing foreign key for subtasks
- isRecurring (Boolean): Flag for recurring tasks
- recurrencePattern (String): Pattern for recurring tasks (DAILY, WEEKLY, MONTHLY, YEARLY)
- recurrenceEndDate (LocalDateTime): End date for recurring tasks
- createdAt (LocalDateTime): Creation timestamp
- updatedAt (LocalDateTime): Last update timestamp

### Reminder
- id (Long): Primary key
- todoId (Long): Foreign key to Todo
- remindAt (LocalDateTime): When to send reminder
- isSent (Boolean): Flag indicating if reminder was sent
- createdAt (LocalDateTime): Creation timestamp

## API Endpoints

### Authentication
- POST /api/auth/register - Register new user
- POST /api/auth/login - Login user
- POST /api/auth/logout - Logout user

### Users
- GET /api/users/profile - Get current user profile
- PUT /api/users/profile - Update user profile

### Categories
- GET /api/categories - List all categories for user
- POST /api/categories - Create new category
- GET /api/categories/{id} - Get category by ID
- PUT /api/categories/{id} - Update category
- DELETE /api/categories/{id} - Delete category

### Todos
- GET /api/todos - List all todos for user with filtering options
- POST /api/todos - Create new todo
- GET /api/todos/{id} - Get todo by ID
- PUT /api/todos/{id} - Update todo
- DELETE /api/todos/{id} - Delete todo
- GET /api/todos/{id}/subtasks - Get subtasks for a todo
- POST /api/todos/{id}/subtasks - Add subtask to a todo

### Reminders
- GET /api/reminders - List all reminders for user
- POST /api/reminders - Create new reminder
- PUT /api/reminders/{id} - Update reminder
- DELETE /api/reminders/{id} - Delete reminder

## Advanced Filtering Capabilities
- Filter by status (PENDING, IN_PROGRESS, COMPLETED)
- Filter by priority (LOW, MEDIUM, HIGH, URGENT)
- Filter by due date range
- Filter by category
- Search by title or description
- Sort by due date, priority, creation date

## Security
- JWT-based authentication
- Password encryption using BCrypt
- Role-based access control

## Recurring Tasks Implementation
- Support for daily, weekly, monthly, yearly recurrence
- Automatic generation of recurring task instances
- Ability to modify or cancel future instances

## Data Validation
- Input validation for all endpoints
- Custom validation for business rules
- Proper error handling and meaningful error messages

## Project Structure
```
src/main/java/com/todo/first/
├── FirstApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── DatabaseConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── CategoryController.java
│   ├── TodoController.java
│   └── ReminderController.java
├── entity/
│   ├── User.java
│   ├── Category.java
│   ├── Todo.java
│   └── Reminder.java
├── repository/
│   ├── UserRepository.java
│   ├── CategoryRepository.java
│   ├── TodoRepository.java
│   └── ReminderRepository.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── CategoryService.java
│   ├── TodoService.java
│   └── ReminderService.java
├── dto/
│   ├── UserDto.java
│   ├── CategoryDto.java
│   ├── TodoDto.java
│   └── ReminderDto.java
└── exception/
    ├── ResourceNotFoundException.java
    ├── UnauthorizedException.java
    └── GlobalExceptionHandler.java
```

## Technology Stack
- Java 17
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Security
- MySQL 8.0+
- Maven
- Lombok
- JWT for authentication

## Development Plan
1. Configure database connection
2. Design and implement entity models
3. Create JPA repositories
4. Implement service layer with business logic
5. Develop REST controllers
6. Add authentication and authorization
7. Implement recurring tasks functionality
8. Add advanced filtering and search capabilities
9. Add validation and error handling
10. Test API endpoints

## Future Enhancements
- Email/SMS notifications for reminders
- Integration with calendar applications
- File attachments for todos
- Collaboration features (shared todos)
- Mobile-responsive API design