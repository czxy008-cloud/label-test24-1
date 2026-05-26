package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.Quiz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface QuizMapper {

    List<Quiz> findByChapterId(@Param("chapterId") Long chapterId);

    Optional<Quiz> findById(@Param("id") Long id);

    List<Quiz> findByIds(@Param("ids") List<Long> ids);

    int countByChapterId(@Param("chapterId") Long chapterId);

    int insert(Quiz quiz);

    int update(Quiz quiz);

    int deleteById(@Param("id") Long id);
}
