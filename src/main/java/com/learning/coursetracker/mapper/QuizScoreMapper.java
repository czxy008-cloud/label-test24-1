package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.QuizScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface QuizScoreMapper {

    List<QuizScore> findByUserAndChapter(@Param("userId") Long userId,
                                          @Param("chapterId") Long chapterId);

    Optional<QuizScore> findByUserAndQuiz(@Param("userId") Long userId,
                                           @Param("quizId") Long quizId);

    List<QuizScore> findByUserId(@Param("userId") Long userId);

    int insert(QuizScore quizScore);

    int deleteByUserAndChapter(@Param("userId") Long userId,
                                @Param("chapterId") Long chapterId);

    Double getAverageScoreByUser(@Param("userId") Long userId);

    Double getAverageScoreByUserAndCourse(@Param("userId") Long userId,
                                           @Param("courseId") Long courseId);

    int countPassedChapters(@Param("userId") Long userId);

    int countTotalChapters(@Param("userId") Long userId);
}
