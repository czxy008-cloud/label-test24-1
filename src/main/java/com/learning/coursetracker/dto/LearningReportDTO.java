package com.learning.coursetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningReportDTO {

    private Long userId;
    private String username;
    private String nickname;

    private Integer totalCourses;
    private Integer completedCourses;
    private Integer inProgressCourses;

    private Integer totalChapters;
    private Integer completedChapters;

    private Double overallCompletionRate;

    private Double averageScore;
    private Integer totalQuizzes;
    private Integer passedQuizzes;

    private Integer totalWatchTime;

    private java.util.List<CourseProgress> courseProgressList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseProgress {
        private Long courseId;
        private String courseTitle;
        private Integer totalChapters;
        private Integer completedChapters;
        private Double completionRate;
        private Double averageScore;
        private Integer totalWatchTime;
    }
}
