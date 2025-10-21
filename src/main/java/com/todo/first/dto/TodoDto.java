package com.todo.first.dto;

import com.todo.first.entity.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {
    private Long id;
    private String title;
    private String description;
    private Todo.Status status;
    private Todo.Priority priority;
    private LocalDateTime dueDate;
    private LocalDateTime reminderTime;
    private CategoryDto category;
    private Boolean isRecurring;
    private String recurrencePattern;
    private LocalDateTime recurrenceEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDto {
        private Long id;
        private String name;
    }
}