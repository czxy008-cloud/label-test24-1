package com.learning.coursetracker.mapper;

import com.learning.coursetracker.entity.LearningProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LearningProgressMapper {

    Optional<LearningProgress> findByUserAndChapter(@Param("userId") Long userId,
                                                     @Param("chapterId") Long chapterId);

    List<LearningProgress> findByUserId(@Param("userId") Long userId);

    List<LearningProgress> findByUserIdAndCourseId(@Param("userId") Long userId,
                                                    @Param("courseId") Long courseId);

    int countCompletedChapters(@Param("userId") Long userId,
                                @Param("courseId") Long courseId);

    int countTotalChaptersByCourseId(@Param("courseId") Long courseId);

    int insert(LearningProgress progress);

    int update(LearningProgress progress);

    int getTotalWatchTimeByUser(@Param("userId") Long userId);

    int getTotalWatchTimeByUserAndCourse(@Param("userId") Long userId,
                                          @Param("courseId") Long courseId);

    List<Map<String, Object>> getCourseProgressStatsForUser(@Param("userId") Long userId);
}
