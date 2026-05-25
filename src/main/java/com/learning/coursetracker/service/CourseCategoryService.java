package com.learning.coursetracker.service;

import com.learning.coursetracker.entity.CourseCategory;
import com.learning.coursetracker.mapper.CourseCategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseCategoryService {

    private final CourseCategoryMapper courseCategoryMapper;

    public CourseCategoryService(CourseCategoryMapper courseCategoryMapper) {
        this.courseCategoryMapper = courseCategoryMapper;
    }

    public List<CourseCategory> getAllCategories() {
        return courseCategoryMapper.findAll();
    }

    public Optional<CourseCategory> getCategoryById(Long id) {
        return courseCategoryMapper.findById(id);
    }

    public List<CourseCategory> getSubCategories(Long parentId) {
        return courseCategoryMapper.findByParentId(parentId);
    }
}
