package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChapterMapper {

    List<Chapter> findByCourseId(@Param("courseId") Long courseId);

    Optional<Chapter> findById(@Param("id") Long id);

    int insert(Chapter chapter);

    int update(Chapter chapter);

    int deleteById(@Param("id") Long id);

    int countByCourseId(@Param("courseId") Long courseId);
}
