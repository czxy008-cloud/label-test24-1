package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.ChapterQuizResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChapterQuizResultMapper {

    Optional<ChapterQuizResult> findByUserAndChapter(@Param("userId") Long userId,
                                                      @Param("chapterId") Long chapterId);

    Optional<ChapterQuizResult> findByUserAndChapterForUpdate(@Param("userId") Long userId,
                                                               @Param("chapterId") Long chapterId);

    List<ChapterQuizResult> findByUserId(@Param("userId") Long userId);

    List<ChapterQuizResult> findByUserIdAndCourseId(@Param("userId") Long userId,
                                                     @Param("courseId") Long courseId);

    int insert(ChapterQuizResult result);

    int update(ChapterQuizResult result);

    int upsert(ChapterQuizResult result);

    Double getAverageScoreByUser(@Param("userId") Long userId);

    Double getAverageScoreByUserAndCourse(@Param("userId") Long userId,
                                           @Param("courseId") Long courseId);

    int countPassedChapters(@Param("userId") Long userId);

    List<Map<String, Object>> getAverageScoresByUserForAllCourses(@Param("userId") Long userId);
}
