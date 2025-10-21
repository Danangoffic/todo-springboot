package com.todo.first.service;

import com.todo.first.entity.Todo;
import com.todo.first.repository.TodoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecurringTaskService {
    private final TodoRepository todoRepository;

    public RecurringTaskService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * Process recurring tasks and generate new instances based on their recurrence pattern
     */
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void processRecurringTasks() {
        // Find all recurring tasks that need to generate new instances
        List<Todo> recurringTodos = todoRepository.findByIsRecurringTrueAndRecurrenceEndDateAfterOrRecurrenceEndDateIsNull(LocalDateTime.now());
        
        for (Todo recurringTodo : recurringTodos) {
            // Check if it's time to create a new instance
            if (shouldCreateNewInstance(recurringTodo)) {
                createNewInstance(recurringTodo);
            }
        }
    }

    /**
     * Determine if a new instance should be created based on the recurrence pattern
     */
    private boolean shouldCreateNewInstance(Todo recurringTodo) {
        if (recurringTodo.getRecurrencePattern() == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastInstanceDate = getLastInstanceDate(recurringTodo);
        
        switch (recurringTodo.getRecurrencePattern().toUpperCase()) {
            case "DAILY":
                return now.isAfter(lastInstanceDate.plusDays(1));
            case "WEEKLY":
                return now.isAfter(lastInstanceDate.plusWeeks(1));
            case "MONTHLY":
                return now.isAfter(lastInstanceDate.plusMonths(1));
            case "YEARLY":
                return now.isAfter(lastInstanceDate.plusYears(1));
            default:
                return false;
        }
    }

    /**
     * Get the date of the last instance of this recurring task
     */
    private LocalDateTime getLastInstanceDate(Todo recurringTodo) {
        // For simplicity, we'll use the original todo's due date as the last instance date
        // In a more complex implementation, you might want to track actual instances
        return recurringTodo.getDueDate() != null ? recurringTodo.getDueDate() : recurringTodo.getCreatedAt();
    }

    /**
     * Create a new instance of a recurring task
     */
    private void createNewInstance(Todo recurringTodo) {
        Todo newInstance = new Todo();
        newInstance.setTitle(recurringTodo.getTitle());
        newInstance.setDescription(recurringTodo.getDescription());
        newInstance.setStatus(Todo.Status.PENDING);
        newInstance.setPriority(recurringTodo.getPriority());
        newInstance.setUser(recurringTodo.getUser());
        newInstance.setCategory(recurringTodo.getCategory());
        newInstance.setIsRecurring(recurringTodo.getIsRecurring());
        newInstance.setRecurrencePattern(recurringTodo.getRecurrencePattern());
        newInstance.setRecurrenceEndDate(recurringTodo.getRecurrenceEndDate());
        newInstance.setParent(recurringTodo);
        
        // Calculate new due date based on recurrence pattern
        LocalDateTime newDueDate = calculateNextDueDate(recurringTodo);
        newInstance.setDueDate(newDueDate);
        
        // Save the new instance
        todoRepository.save(newInstance);
    }

    /**
     * Calculate the next due date based on the recurrence pattern
     */
    private LocalDateTime calculateNextDueDate(Todo recurringTodo) {
        LocalDateTime currentDate = recurringTodo.getDueDate() != null ? 
            recurringTodo.getDueDate() : recurringTodo.getCreatedAt();
            
        switch (recurringTodo.getRecurrencePattern().toUpperCase()) {
            case "DAILY":
                return currentDate.plusDays(1);
            case "WEEKLY":
                return currentDate.plusWeeks(1);
            case "MONTHLY":
                return currentDate.plusMonths(1);
            case "YEARLY":
                return currentDate.plusYears(1);
            default:
                return currentDate;
        }
    }
}