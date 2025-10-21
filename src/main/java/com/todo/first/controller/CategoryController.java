package com.todo.first.controller;

import com.todo.first.dto.CategoryDto;
import com.todo.first.entity.User;
import com.todo.first.service.CategoryService;
import com.todo.first.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        User user = getCurrentUser();
        CategoryDto createdCategory = categoryService.createCategory(categoryDto, user);
        return ResponseEntity.ok(createdCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        User user = getCurrentUser();
        List<CategoryDto> categories = categoryService.getCategoriesByUser(user);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        User user = getCurrentUser();
        CategoryDto category = categoryService.getCategoryByIdAndUser(id, user);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, 
                                                     @Valid @RequestBody CategoryDto categoryDto) {
        User user = getCurrentUser();
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto, user);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        User user = getCurrentUser();
        categoryService.deleteCategory(id, user);
        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username).orElse(null);
    }
}