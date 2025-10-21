package com.todo.first.service;

import com.todo.first.dto.CategoryDto;
import com.todo.first.entity.Category;
import com.todo.first.entity.User;
import com.todo.first.repository.CategoryRepository;
import com.todo.first.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDto createCategory(CategoryDto categoryDto, User user) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .color(categoryDto.getColor())
                .user(user)
                .build();

        Category savedCategory = categoryRepository.save(category);

        return CategoryDto.builder()
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .color(savedCategory.getColor())
                .createdAt(savedCategory.getCreatedAt())
                .build();
    }

    public List<CategoryDto> getCategoriesByUser(User user) {
        return categoryRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryByIdAndUser(Long id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDto(category);
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setColor(categoryDto.getColor());

        Category updatedCategory = categoryRepository.save(category);

        return CategoryDto.builder()
                .name(updatedCategory.getName())
                .description(updatedCategory.getDescription())
                .color(updatedCategory.getColor())
                .createdAt(updatedCategory.getCreatedAt())
                .build();
    }

    public void deleteCategory(Long id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    private CategoryDto convertToDto(Category category) {
        return CategoryDto.builder()
                .name(category.getName())
                .description(category.getDescription())
                .color(category.getColor())
                .createdAt(category.getCreatedAt())
                .build();
    }
}