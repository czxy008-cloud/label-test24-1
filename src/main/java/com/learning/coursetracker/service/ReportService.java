package com.learning.coursetracker.service;

import com.learning.coursetracker.dto.LearningReportDTO;
import com.learning.coursetracker.entity.ChapterQuizResult;
import com.learning.coursetracker.entity.Course;
import com.learning.coursetracker.entity.User;
import com.learning.coursetracker.mapper.ChapterQuizResultMapper;
import com.learning.coursetracker.mapper.CourseMapper;
import com.learning.coursetracker.mapper.LearningProgressMapper;
import com.learning.coursetracker.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final LearningProgressMapper progressMapper;
    private final ChapterQuizResultMapper quizResultMapper;

    public ReportService(UserMapper userMapper, CourseMapper courseMapper,
                        LearningProgressMapper progressMapper, ChapterQuizResultMapper quizResultMapper) {
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.progressMapper = progressMapper;
        this.quizResultMapper = quizResultMapper;
    }

    public LearningReportDTO generateLearningReport(Long userId) {
        User user = userMapper.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));

        List<Course> allCourses = courseMapper.findPublishedCourses(null, null);

        int totalCourses = 0;
        int completedCourses = 0;
        int inProgressCourses = 0;
        int totalChapters = 0;
        int completedChapters = 0;

        List<LearningReportDTO.CourseProgress> courseProgressList = new ArrayList<>();

        for (Course course : allCourses) {
            int courseTotalChapters = progressMapper.countTotalChaptersByCourseId(course.getId());
            if (courseTotalChapters == 0) {
                continue;
            }

            totalCourses++;
            totalChapters += courseTotalChapters;

            int courseCompletedChapters = progressMapper.countCompletedChapters(userId, course.getId());
            completedChapters += courseCompletedChapters;

            double completionRate = (double) courseCompletedChapters / courseTotalChapters * 100;

            if (completionRate >= 100) {
                completedCourses++;
            } else if (completionRate > 0) {
                inProgressCourses++;
            }

            Double averageScore = quizResultMapper.getAverageScoreByUserAndCourse(userId, course.getId());
            Integer watchTime = progressMapper.getTotalWatchTimeByUserAndCourse(userId, course.getId());

            courseProgressList.add(LearningReportDTO.CourseProgress.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .totalChapters(courseTotalChapters)
                    .completedChapters(courseCompletedChapters)
                    .completionRate(Math.round(completionRate * 100.0) / 100.0)
                    .averageScore(averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0)
                    .totalWatchTime(watchTime != null ? watchTime : 0)
                    .build());
        }

        double overallCompletionRate = totalChapters > 0 ? (double) completedChapters / totalChapters * 100 : 0;

        Double averageScore = quizResultMapper.getAverageScoreByUser(userId);
        int passedChapters = quizResultMapper.countPassedChapters(userId);
        List<ChapterQuizResult> allResults = quizResultMapper.findByUserId(userId);

        Integer totalWatchTime = progressMapper.getTotalWatchTimeByUser(userId);

        return LearningReportDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .totalCourses(totalCourses)
                .completedCourses(completedCourses)
                .inProgressCourses(inProgressCourses)
                .totalChapters(totalChapters)
                .completedChapters(completedChapters)
                .overallCompletionRate(Math.round(overallCompletionRate * 100.0) / 100.0)
                .averageScore(averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0)
                .totalQuizzes(allResults.size())
                .passedQuizzes(passedChapters)
                .totalWatchTime(totalWatchTime != null ? totalWatchTime : 0)
                .courseProgressList(courseProgressList)
                .build();
    }
}
