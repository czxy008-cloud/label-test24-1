package com.learning.coursetracker.controller;

import com.learning.coursetracker.dto.ApiResponse;
import com.learning.coursetracker.entity.CourseCategory;
import com.learning.coursetracker.service.CourseCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CourseCategoryController {

    private final CourseCategoryService categoryService;

    public CourseCategoryController(CourseCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseCategory>>> getAllCategories() {
        List<CourseCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseCategory>> getCategoryById(@PathVariable Long id) {
        Optional<CourseCategory> category = categoryService.getCategoryById(id);
        return category.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
                .orElse(ResponseEntity.ok(ApiResponse.error(404, "分类不存在")));
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<CourseCategory>>> getSubCategories(@PathVariable Long id) {
        List<CourseCategory> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.ok(ApiResponse.success(subCategories));
    }
}
