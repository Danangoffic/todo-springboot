package com.todo.first.repository;

import com.todo.first.entity.Todo;
import com.todo.first.entity.User;
import com.todo.first.entity.Category;
import com.todo.first.entity.Todo.Status;
import com.todo.first.entity.Todo.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUser(User user);
    List<Todo> findByUserAndParentIsNull(User user);
    List<Todo> findByParent(Todo parent);
    Optional<Todo> findByIdAndUser(Long id, User user);
    List<Todo> findByUserAndStatus(User user, Status status);
    List<Todo> findByUserAndPriority(User user, Priority priority);
    List<Todo> findByUserAndCategory(User user, Category category);
    List<Todo> findByUserAndDueDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    List<Todo> findByIsRecurringTrueAndRecurrenceEndDateAfterOrRecurrenceEndDateIsNull(LocalDateTime dateTime);
    
    @Query("SELECT t FROM Todo t WHERE t.user = :user AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Todo> findByUserAndSearchTerm(@Param("user") User user, 
                                      @Param("searchTerm") String searchTerm, 
                                      Pageable pageable);
    
    @Query("SELECT t FROM Todo t WHERE t.user = :user " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
           "AND (:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) " +
           "AND (:dueDateTo IS NULL OR t.dueDate <= :dueDateTo) " +
           "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Todo> findTodosWithFilters(@Param("user") User user,
                                   @Param("status") Status status,
                                   @Param("priority") Priority priority,
                                   @Param("categoryId") Long categoryId,
                                   @Param("dueDateFrom") LocalDateTime dueDateFrom,
                                   @Param("dueDateTo") LocalDateTime dueDateTo,
                                   @Param("search") String search,
                                   Pageable pageable);
                                   
    @Query("SELECT t FROM Todo t WHERE t.user = :user " +
           "AND t.dueDate >= :startDate AND t.dueDate <= :endDate")
    List<Todo> findByUserAndDueDateRange(@Param("user") User user,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
                                        
    @Query("SELECT t FROM Todo t WHERE t.user = :user " +
           "AND t.status = :status AND t.dueDate < :currentDate")
    List<Todo> findOverdueTodosByUserAndStatus(@Param("user") User user,
                                              @Param("status") Status status,
                                              @Param("currentDate") LocalDateTime currentDate);
}