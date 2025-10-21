package com.todo.first.repository;

import com.todo.first.entity.Reminder;
import com.todo.first.entity.Todo;
import com.todo.first.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByTodo(Todo todo);
    List<Reminder> findByTodoUser(User user);
    Page<Reminder> findByTodoUser(User user, Pageable pageable);
    List<Reminder> findByRemindAtBeforeAndIsSentFalse(LocalDateTime dateTime);
}