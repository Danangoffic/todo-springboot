package com.todo.first.service;

import com.todo.first.dto.TodoDto;
import com.todo.first.dto.CreateTodoDto;
import com.todo.first.dto.UpdateTodoDto;
import com.todo.first.entity.Todo;
import com.todo.first.entity.User;
import com.todo.first.entity.Category;
import com.todo.first.repository.TodoRepository;
import com.todo.first.repository.CategoryRepository;
import com.todo.first.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    public TodoDto createTodo(CreateTodoDto createTodoDto, User user) {
        Todo todo = new Todo();
        todo.setTitle(createTodoDto.getTitle());
        todo.setDescription(createTodoDto.getDescription());
        todo.setStatus(createTodoDto.getStatus() != null ? createTodoDto.getStatus() : Todo.Status.PENDING);
        todo.setPriority(createTodoDto.getPriority() != null ? createTodoDto.getPriority() : Todo.Priority.MEDIUM);
        todo.setDueDate(createTodoDto.getDueDate());
        todo.setReminderTime(createTodoDto.getReminderTime());
        todo.setUser(user);
        todo.setIsRecurring(createTodoDto.getIsRecurring() != null ? createTodoDto.getIsRecurring() : false);
        todo.setRecurrencePattern(createTodoDto.getRecurrencePattern());
        todo.setRecurrenceEndDate(createTodoDto.getRecurrenceEndDate());

        // Set category if provided
        if (createTodoDto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUser(createTodoDto.getCategoryId(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + createTodoDto.getCategoryId()));
            todo.setCategory(category);
        }

        Todo savedTodo = todoRepository.save(todo);

        return convertToDto(savedTodo);
    }

    public List<TodoDto> getTodosByUser(User user) {
        return todoRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<TodoDto> getTodosWithFilters(User user, Todo.Status status, Todo.Priority priority, 
                                           Long categoryId, String search, Pageable pageable) {
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findByIdAndUser(categoryId, user)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        }
        
        return todoRepository.findTodosWithFilters(user, status, priority, categoryId, null, null, search, pageable)
                .map(this::convertToDto);
    }

    public Page<TodoDto> getTodosWithAdvancedFilters(User user, Todo.Status status, Todo.Priority priority, 
                                                   Long categoryId, LocalDateTime dueDateFrom, LocalDateTime dueDateTo, 
                                                   String search, Pageable pageable) {
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findByIdAndUser(categoryId, user)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        }
        
        return todoRepository.findTodosWithFilters(user, status, priority, categoryId, dueDateFrom, dueDateTo, search, pageable)
                .map(this::convertToDto);
    }

    public TodoDto getTodoByIdAndUser(Long id, User user) {
        Todo todo = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        return convertToDto(todo);
    }

    public TodoDto updateTodo(Long id, UpdateTodoDto updateTodoDto, User user) {
        Todo todo = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));

        todo.setTitle(updateTodoDto.getTitle() != null ? updateTodoDto.getTitle() : todo.getTitle());
        todo.setDescription(updateTodoDto.getDescription() != null ? updateTodoDto.getDescription() : todo.getDescription());
        todo.setStatus(updateTodoDto.getStatus() != null ? updateTodoDto.getStatus() : todo.getStatus());
        todo.setPriority(updateTodoDto.getPriority() != null ? updateTodoDto.getPriority() : todo.getPriority());
        todo.setDueDate(updateTodoDto.getDueDate() != null ? updateTodoDto.getDueDate() : todo.getDueDate());
        todo.setReminderTime(updateTodoDto.getReminderTime() != null ? updateTodoDto.getReminderTime() : todo.getReminderTime());
        todo.setIsRecurring(updateTodoDto.getIsRecurring() != null ? updateTodoDto.getIsRecurring() : todo.getIsRecurring());
        todo.setRecurrencePattern(updateTodoDto.getRecurrencePattern() != null ? updateTodoDto.getRecurrencePattern() : todo.getRecurrencePattern());
        todo.setRecurrenceEndDate(updateTodoDto.getRecurrenceEndDate() != null ? updateTodoDto.getRecurrenceEndDate() : todo.getRecurrenceEndDate());

        // Update category if provided
        if (updateTodoDto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUser(updateTodoDto.getCategoryId(), user)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + updateTodoDto.getCategoryId()));
            todo.setCategory(category);
        }

        Todo updatedTodo = todoRepository.save(todo);

        return convertToDto(updatedTodo);
    }

    public void deleteTodo(Long id, User user) {
        Todo todo = todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + id));
        todoRepository.delete(todo);
    }

    public List<TodoDto> getSubtasks(Long parentId, User user) {
        Todo parentTodo = todoRepository.findByIdAndUser(parentId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent todo not found with id: " + parentId));
        
        return todoRepository.findByParent(parentTodo).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TodoDto createSubtask(Long parentId, CreateTodoDto createTodoDto, User user) {
        Todo parentTodo = todoRepository.findByIdAndUser(parentId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent todo not found with id: " + parentId));

        Todo subtask = new Todo();
        subtask.setTitle(createTodoDto.getTitle());
        subtask.setDescription(createTodoDto.getDescription());
        subtask.setStatus(createTodoDto.getStatus() != null ? createTodoDto.getStatus() : Todo.Status.PENDING);
        subtask.setPriority(createTodoDto.getPriority() != null ? createTodoDto.getPriority() : Todo.Priority.MEDIUM);
        subtask.setDueDate(createTodoDto.getDueDate());
        subtask.setReminderTime(createTodoDto.getReminderTime());
        subtask.setUser(user);
        subtask.setParent(parentTodo);

        Todo savedSubtask = todoRepository.save(subtask);

        return convertToDto(savedSubtask);
    }

    public List<TodoDto> getTodosByDateRange(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return todoRepository.findByUserAndDueDateRange(user, startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<TodoDto> getOverdueTodos(User user) {
        return todoRepository.findOverdueTodosByUserAndStatus(user, Todo.Status.PENDING, LocalDateTime.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TodoDto convertToDto(Todo todo) {
        TodoDto.TodoDtoBuilder builder = TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .status(todo.getStatus())
                .priority(todo.getPriority())
                .dueDate(todo.getDueDate())
                .reminderTime(todo.getReminderTime())
                .isRecurring(todo.getIsRecurring())
                .recurrencePattern(todo.getRecurrencePattern())
                .recurrenceEndDate(todo.getRecurrenceEndDate())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt());

        // Set category if it exists
        if (todo.getCategory() != null) {
            builder.category(TodoDto.CategoryDto.builder()
                    .id(todo.getCategory().getId())
                    .name(todo.getCategory().getName())
                    .build());
        }

        return builder.build();
    }
}