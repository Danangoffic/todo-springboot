package com.todo.first.controller;

import com.todo.first.dto.ReminderDto;
import com.todo.first.dto.CreateReminderDto;
import com.todo.first.entity.User;
import com.todo.first.service.ReminderService;
import com.todo.first.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "*")
public class ReminderController {
    private final ReminderService reminderService;
    private final UserService userService;

    public ReminderController(ReminderService reminderService, UserService userService) {
        this.reminderService = reminderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ReminderDto> createReminder(@Valid @RequestBody CreateReminderDto createReminderDto) {
        User user = getCurrentUser();
        ReminderDto createdReminder = reminderService.createReminder(createReminderDto, user);
        return ResponseEntity.ok(createdReminder);
    }

    @GetMapping
    public ResponseEntity<Page<ReminderDto>> getReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<ReminderDto> reminders = reminderService.getRemindersByUser(user, pageable);
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderDto> getReminder(@PathVariable Long id) {
        User user = getCurrentUser();
        ReminderDto reminder = reminderService.getReminderByIdAndUser(id, user);
        return ResponseEntity.ok(reminder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderDto> updateReminder(@PathVariable Long id,
                                                     @Valid @RequestBody ReminderDto reminderDto) {
        User user = getCurrentUser();
        ReminderDto updatedReminder = reminderService.updateReminder(id, reminderDto, user);
        return ResponseEntity.ok(updatedReminder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        User user = getCurrentUser();
        reminderService.deleteReminder(id, user);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username).orElse(null);
    }
}