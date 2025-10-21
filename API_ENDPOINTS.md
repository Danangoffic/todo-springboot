# API Endpoints Specification

## Authentication Endpoints

### Register User
- **URL**: `/api/auth/register`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "username": "string",
    "email": "string",
    "createdAt": "datetime"
  }
  ```

### Login User
- **URL**: `/api/auth/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Response**:
  ```json
  {
    "token": "string",
    "expiresIn": "number"
  }
  ```

## User Profile Endpoints

### Get User Profile
- **URL**: `/api/users/profile`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`
- **Response**:
  ```json
  {
    "id": "number",
    "username": "string",
    "email": "string",
    "createdAt": "datetime",
    "updatedAt": "datetime"
  }
  ```

### Update User Profile
- **URL**: `/api/users/profile`
- **Method**: `PUT`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "username": "string",
    "email": "string"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "username": "string",
    "email": "string",
    "createdAt": "datetime",
    "updatedAt": "datetime"
  }
  ```

## Category Endpoints

### List Categories
- **URL**: `/api/categories`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`
- **Query Parameters**:
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Page size (default: 10)
- **Response**:
  ```json
  {
    "content": [
      {
        "id": "number",
        "name": "string",
        "description": "string",
        "color": "string",
        "createdAt": "datetime"
      }
    ],
    "pageable": {
      "pageNumber": "number",
      "pageSize": "number"
    },
    "totalElements": "number",
    "totalPages": "number"
  }
  ```

### Create Category
- **URL**: `/api/categories`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "color": "string"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "name": "string",
    "description": "string",
    "color": "string",
    "createdAt": "datetime"
  }
  ```

### Get Category
- **URL**: `/api/categories/{id}`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`
- **Response**:
  ```json
  {
    "id": "number",
    "name": "string",
    "description": "string",
    "color": "string",
    "createdAt": "datetime"
  }
  ```

### Update Category
- **URL**: `/api/categories/{id}`
- **Method**: `PUT`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "color": "string"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "name": "string",
    "description": "string",
    "color": "string",
    "createdAt": "datetime"
  }
  ```

### Delete Category
- **URL**: `/api/categories/{id}`
- **Method**: `DELETE`
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `204 No Content`

## Todo Endpoints

### List Todos
- **URL**: `/api/todos`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`
- **Query Parameters**:
  - `status` (optional): Filter by status (PENDING, IN_PROGRESS, COMPLETED)
  - `priority` (optional): Filter by priority (LOW, MEDIUM, HIGH, URGENT)
  - `categoryId` (optional): Filter by category ID
  - `dueDateFrom` (optional): Filter by due date range start
  - `dueDateTo` (optional): Filter by due date range end
  - `search` (optional): Search in title or description
  - `sortBy` (optional): Sort field (dueDate, priority, createdAt)
  - `sortOrder` (optional): Sort order (ASC, DESC)
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Page size (default: 10)
- **Response**:
  ```json
  {
    "content": [
      {
        "id": "number",
        "title": "string",
        "description": "string",
        "status": "string",
        "priority": "string",
        "dueDate": "datetime",
        "reminderTime": "datetime",
        "category": {
          "id": "number",
          "name": "string"
        },
        "isRecurring": "boolean",
        "recurrencePattern": "string",
        "createdAt": "datetime",
        "updatedAt": "datetime"
      }
    ],
    "pageable": {
      "pageNumber": "number",
      "pageSize": "number"
    },
    "totalElements": "number",
    "totalPages": "number"
  }
  ```

### Create Todo
- **URL**: `/api/todos`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "title": "string",
    "description": "string",
    "status": "string",
    "priority": "string",
    "dueDate": "datetime",
    "reminderTime": "datetime",
    "categoryId": "number",
    "isRecurring": "boolean",
    "recurrencePattern": "string",
    "recurrenceEndDate": "datetime"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "title": "string",
    "description": "string",
    "status": "string",
    "priority": "string",
    "dueDate": "datetime",
    "reminderTime": "datetime",
    "category": {
      "id": "number",
      "name": "string"
    },
    "isRecurring": "boolean",
    "recurrencePattern": "string",
    "recurrenceEndDate": "datetime",
    "createdAt": "datetime",
    "updatedAt": "datetime"
  }
  ```

### Get Todo
- **URL**: `/api/todos/{id}`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`
- **Response**:
  ```json
  {
    "id": "number",
    "title": "string",
    "description": "string",
    "status": "string",
    "priority": "string",
    "dueDate": "datetime",
    "reminderTime": "datetime",
    "category": {
      "id": "number",
      "name": "string"
    },
    "isRecurring": "boolean",
    "recurrencePattern": "string",
    "recurrenceEndDate": "datetime",
    "createdAt": "datetime",
    "updatedAt": "datetime"
  }
  ```

### Update Todo
- **URL**: `/api/todos/{id}`
- **Method**: `PUT`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "title": "string",
    "description": "string",
    "status": "string",
    "priority": "string",
    "dueDate": "datetime",
    "reminderTime": "datetime",
    "categoryId": "number",
    "isRecurring": "boolean",
    "recurrencePattern": "string",
    "recurrenceEndDate": "datetime"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "title": "string",
    "description": "string",
    "status": "string",
    "priority": "string",
    "dueDate": "datetime",
    "reminderTime": "datetime",
    "category": {
      "id": "number",
      "name": "string"
    },
    "isRecurring": "boolean",
    "recurrencePattern": "string",
    "recurrenceEndDate": "datetime",
    "createdAt": "datetime",
    "updatedAt": "datetime"
  }
  ```

### Delete Todo
- **URL**: `/api/todos/{id}`
- **Method**: `DELETE`
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `204 No Content`

### Get Subtasks
- **URL**: `/api/todos/{id}/subtasks`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`
- **Response**:
  ```json
  [
    {
      "id": "number",
      "title": "string",
      "description": "string",
      "status": "string",
      "priority": "string",
      "dueDate": "datetime",
      "parentId": "number",
      "createdAt": "datetime",
      "updatedAt": "datetime"
    }
  ]
  ```

### Add Subtask
- **URL**: `/api/todos/{id}/subtasks`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "title": "string",
    "description": "string",
    "status": "string",
    "priority": "string",
    "dueDate": "datetime"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "title": "string",
    "description": "string",
    "status": "string",
    "priority": "string",
    "dueDate": "datetime",
    "parentId": "number",
    "createdAt": "datetime",
    "updatedAt": "datetime"
  }
  ```

## Reminder Endpoints

### List Reminders
- **URL**: `/api/reminders`
- **Method**: `GET`
- **Headers**: `Authorization: Bearer <token>`
- **Query Parameters**:
  - `isSent` (optional): Filter by sent status
  - `page` (optional): Page number (default: 0)
  - `size` (optional): Page size (default: 10)
- **Response**:
  ```json
  {
    "content": [
      {
        "id": "number",
        "todo": {
          "id": "number",
          "title": "string"
        },
        "remindAt": "datetime",
        "isSent": "boolean",
        "createdAt": "datetime"
      }
    ],
    "pageable": {
      "pageNumber": "number",
      "pageSize": "number"
    },
    "totalElements": "number",
    "totalPages": "number"
  }
  ```

### Create Reminder
- **URL**: `/api/reminders`
- **Method**: `POST`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "todoId": "number",
    "remindAt": "datetime"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "todo": {
      "id": "number",
      "title": "string"
    },
    "remindAt": "datetime",
    "isSent": "boolean",
    "createdAt": "datetime"
  }
  ```

### Update Reminder
- **URL**: `/api/reminders/{id}`
- **Method**: `PUT`
- **Headers**: `Authorization: Bearer <token>`
- **Request Body**:
  ```json
  {
    "remindAt": "datetime",
    "isSent": "boolean"
  }
  ```
- **Response**:
  ```json
  {
    "id": "number",
    "todo": {
      "id": "number",
      "title": "string"
    },
    "remindAt": "datetime",
    "isSent": "boolean",
    "createdAt": "datetime"
  }
  ```

### Delete Reminder
- **URL**: `/api/reminders/{id}`
- **Method**: `DELETE`
- **Headers**: `Authorization: Bearer <token>`
- **Response**: `204 No Content`