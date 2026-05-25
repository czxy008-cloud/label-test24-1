package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseMapper {

    List<Course> findAll(@Param("categoryId") Long categoryId,
                         @Param("keyword") String keyword);

    List<Course> findPublishedCourses(@Param("categoryId") Long categoryId,
                                      @Param("keyword") String keyword);

    Optional<Course> findById(@Param("id") Long id);

    List<Course> findByCategoryId(@Param("categoryId") Long categoryId);

    int insert(Course course);

    int update(Course course);

    int deleteById(@Param("id") Long id);
}
