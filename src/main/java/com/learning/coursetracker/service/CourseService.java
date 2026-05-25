package com.learning.coursetracker.service;

import com.learning.coursetracker.dto.CourseDTO;
import com.learning.coursetracker.entity.Chapter;
import com.learning.coursetracker.entity.Course;
import com.learning.coursetracker.mapper.ChapterMapper;
import com.learning.coursetracker.mapper.CourseMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseMapper courseMapper;
    private final ChapterMapper chapterMapper;

    public CourseService(CourseMapper courseMapper, ChapterMapper chapterMapper) {
        this.courseMapper = courseMapper;
        this.chapterMapper = chapterMapper;
    }

    public List<CourseDTO> getPublishedCourses(Long categoryId, String keyword) {
        List<Course> courses = courseMapper.findPublishedCourses(categoryId, keyword);
        return courses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<CourseDTO> getAllCourses(Long categoryId, String keyword) {
        List<Course> courses = courseMapper.findAll(categoryId, keyword);
        return courses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Optional<CourseDTO> getCourseById(Long id) {
        return courseMapper.findById(id).map(this::convertToDTO);
    }

    public List<CourseDTO> getCoursesByCategory(Long categoryId) {
        List<Course> courses = courseMapper.findByCategoryId(categoryId);
        return courses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private CourseDTO convertToDTO(Course course) {
        List<Chapter> chapters = chapterMapper.findByCourseId(course.getId());

        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .categoryId(course.getCategoryId())
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .instructor(course.getInstructor())
                .coverImageUrl(course.getCoverImageUrl())
                .totalDuration(course.getTotalDuration())
                .difficultyLevel(course.getDifficultyLevel())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .chapters(chapters.stream().map(chapter ->
                        CourseDTO.ChapterDTO.builder()
                                .id(chapter.getId())
                                .title(chapter.getTitle())
                                .description(chapter.getDescription())
                                .videoUrl(chapter.getVideoUrl())
                                .duration(chapter.getDuration())
                                .sortOrder(chapter.getSortOrder())
                                .isFree(chapter.getIsFree())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
