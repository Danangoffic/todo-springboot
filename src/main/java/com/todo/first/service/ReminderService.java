package com.todo.first.service;

import com.todo.first.dto.ReminderDto;
import com.todo.first.dto.CreateReminderDto;
import com.todo.first.entity.Reminder;
import com.todo.first.entity.Todo;
import com.todo.first.entity.User;
import com.todo.first.repository.ReminderRepository;
import com.todo.first.repository.TodoRepository;
import com.todo.first.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final TodoRepository todoRepository;

    public ReminderService(ReminderRepository reminderRepository, TodoRepository todoRepository) {
        this.reminderRepository = reminderRepository;
        this.todoRepository = todoRepository;
    }

    public ReminderDto createReminder(CreateReminderDto createReminderDto, User user) {
        Todo todo = todoRepository.findByIdAndUser(createReminderDto.getTodoId(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Todo not found with id: " + createReminderDto.getTodoId()));

        Reminder reminder = new Reminder();
        reminder.setTodo(todo);
        reminder.setRemindAt(createReminderDto.getRemindAt());
        reminder.setIsSent(false);

        Reminder savedReminder = reminderRepository.save(reminder);

        return convertToDto(savedReminder);
    }

    public List<ReminderDto> getRemindersByUser(User user) {
        return reminderRepository.findByTodoUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Page<ReminderDto> getRemindersByUser(User user, Pageable pageable) {
        return reminderRepository.findByTodoUser(user, pageable)
                .map(this::convertToDto);
    }

    public ReminderDto getReminderByIdAndUser(Long id, User user) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
        
        // Check if the reminder belongs to the user
        if (!reminder.getTodo().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Reminder not found with id: " + id);
        }
        
        return convertToDto(reminder);
    }

    public ReminderDto updateReminder(Long id, ReminderDto reminderDto, User user) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
        
        // Check if the reminder belongs to the user
        if (!reminder.getTodo().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Reminder not found with id: " + id);
        }

        reminder.setRemindAt(reminderDto.getRemindAt());
        reminder.setIsSent(reminderDto.getIsSent());

        Reminder updatedReminder = reminderRepository.save(reminder);

        return convertToDto(updatedReminder);
    }

    public void deleteReminder(Long id, User user) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with id: " + id));
        
        // Check if the reminder belongs to the user
        if (!reminder.getTodo().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Reminder not found with id: " + id);
        }
        
        reminderRepository.delete(reminder);
    }

    private ReminderDto convertToDto(Reminder reminder) {
        return ReminderDto.builder()
                .id(reminder.getId())
                .todo(ReminderDto.TodoDto.builder()
                        .id(reminder.getTodo().getId())
                        .title(reminder.getTodo().getTitle())
                        .build())
                .remindAt(reminder.getRemindAt())
                .isSent(reminder.getIsSent())
                .createdAt(reminder.getCreatedAt())
                .build();
    }
}