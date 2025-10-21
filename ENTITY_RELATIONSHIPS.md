# Entity Relationship Diagram

```mermaid
erDiagram
    USER ||--o{ TODO : creates
    USER ||--o{ CATEGORY : owns
    USER ||--o{ REMINDER : has
    CATEGORY ||--o{ TODO : contains
    TODO ||--o{ TODO : subtasks
    TODO ||--o{ REMINDER : triggers
    
    USER {
        Long id PK
        String username
        String email
        String password
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    CATEGORY {
        Long id PK
        String name
        String description
        String color
        Long userId FK
        LocalDateTime createdAt
    }
    
    TODO {
        Long id PK
        String title
        String description
        String status
        String priority
        LocalDateTime dueDate
        LocalDateTime reminderTime
        Long categoryId FK
        Long userId FK
        Long parentId FK
        Boolean isRecurring
        String recurrencePattern
        LocalDateTime recurrenceEndDate
        LocalDateTime createdAt
        LocalDateTime updatedAt
    }
    
    REMINDER {
        Long id PK
        Long todoId FK
        LocalDateTime remindAt
        Boolean isSent
        LocalDateTime createdAt
    }