package com.learning.coursetracker.controller;

import com.learning.coursetracker.dto.ApiResponse;
import com.learning.coursetracker.dto.CourseDTO;
import com.learning.coursetracker.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCourses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        List<CourseDTO> courses = courseService.getPublishedCourses(categoryId, keyword);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCourses(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        List<CourseDTO> courses = courseService.getAllCourses(categoryId, keyword);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> getCourseById(@PathVariable Long id) {
        Optional<CourseDTO> course = courseService.getCourseById(id);
        return course.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
                .orElse(ResponseEntity.ok(ApiResponse.error(404, "课程不存在")));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getCoursesByCategory(@PathVariable Long categoryId) {
        List<CourseDTO> courses = courseService.getCoursesByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
}
