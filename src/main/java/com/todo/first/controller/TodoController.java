package com.todo.first.controller;

import com.todo.first.dto.TodoDto;
import com.todo.first.dto.CreateTodoDto;
import com.todo.first.dto.UpdateTodoDto;
import com.todo.first.entity.Todo;
import com.todo.first.entity.User;
import com.todo.first.service.TodoService;
import com.todo.first.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {
    private final TodoService todoService;
    private final UserService userService;

    public TodoController(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@Valid @RequestBody CreateTodoDto createTodoDto) {
        User user = getCurrentUser();
        TodoDto createdTodo = todoService.createTodo(createTodoDto, user);
        return ResponseEntity.ok(createdTodo);
    }

    @GetMapping
    public ResponseEntity<Page<TodoDto>> getTodos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(required = false) String dueDateFrom,
            @RequestParam(required = false) String dueDateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = getCurrentUser();
        
        Todo.Status statusEnum = status != null ? Todo.Status.valueOf(status.toUpperCase()) : null;
        Todo.Priority priorityEnum = priority != null ? Todo.Priority.valueOf(priority.toUpperCase()) : null;
        
        // Parse date filters
        LocalDateTime dueDateFromParsed = null;
        LocalDateTime dueDateToParsed = null;
        
        if (dueDateFrom != null && !dueDateFrom.isEmpty()) {
            dueDateFromParsed = LocalDateTime.parse(dueDateFrom);
        }
        
        if (dueDateTo != null && !dueDateTo.isEmpty()) {
            dueDateToParsed = LocalDateTime.parse(dueDateTo);
        }
        
        // Create pageable with sorting
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, sortBy);
        }
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TodoDto> todos = todoService.getTodosWithAdvancedFilters(
                user, statusEnum, priorityEnum, categoryId, dueDateFromParsed, dueDateToParsed, search, pageable);
        
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoDto> getTodo(@PathVariable Long id) {
        User user = getCurrentUser();
        TodoDto todo = todoService.getTodoByIdAndUser(id, user);
        return ResponseEntity.ok(todo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long id,
                                             @Valid @RequestBody UpdateTodoDto updateTodoDto) {
        User user = getCurrentUser();
        TodoDto updatedTodo = todoService.updateTodo(id, updateTodoDto, user);
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        User user = getCurrentUser();
        todoService.deleteTodo(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/subtasks")
    public ResponseEntity<List<TodoDto>> getSubtasks(@PathVariable Long id) {
        User user = getCurrentUser();
        List<TodoDto> subtasks = todoService.getSubtasks(id, user);
        return ResponseEntity.ok(subtasks);
    }

    @PostMapping("/{id}/subtasks")
    public ResponseEntity<TodoDto> createSubtask(@PathVariable Long id,
                                                @Valid @RequestBody CreateTodoDto createTodoDto) {
        User user = getCurrentUser();
        TodoDto createdSubtask = todoService.createSubtask(id, createTodoDto, user);
        return ResponseEntity.ok(createdSubtask);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TodoDto>> getTodosByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        User user = getCurrentUser();
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<TodoDto> todos = todoService.getTodosByDateRange(user, start, end);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TodoDto>> getOverdueTodos() {
        User user = getCurrentUser();
        List<TodoDto> overdueTodos = todoService.getOverdueTodos(user);
        return ResponseEntity.ok(overdueTodos);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username).orElse(null);
    }
}