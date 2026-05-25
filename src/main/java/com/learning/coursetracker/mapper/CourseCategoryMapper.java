package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.CourseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CourseCategoryMapper {

    List<CourseCategory> findAll();

    Optional<CourseCategory> findById(@Param("id") Long id);

    List<CourseCategory> findByParentId(@Param("parentId") Long parentId);
}
