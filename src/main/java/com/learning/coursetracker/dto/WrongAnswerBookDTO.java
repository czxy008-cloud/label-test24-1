package com.learning.coursetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongAnswerBookDTO {

    private Long userId;
    private Integer totalWrongCount;
    private Integer totalQuestionCount;
    private Double overallErrorRate;
    private List<CourseWrongStats> courseList;
    private List<KnowledgePointErrorStat> topWeakKnowledgePoints;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseWrongStats {
        private Long courseId;
        private String courseTitle;
        private Integer totalWrongCount;
        private Integer totalQuestionCount;
        private Double errorRate;
        private List<ChapterWrongStats> chapterList;
        private List<KnowledgePointErrorStat> knowledgePointStats;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterWrongStats {
        private Long chapterId;
        private String chapterTitle;
        private Integer totalWrongCount;
        private Integer totalQuestionCount;
        private Double errorRate;
        private List<WrongQuizDetail> wrongQuizList;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrongQuizDetail {
        private Long quizId;
        private String questionText;
        private String userAnswer;
        private String correctAnswer;
        private String correctOptionText;
        private String explanation;
        private List<String> knowledgeTags;
        private LocalDateTime submittedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KnowledgePointErrorStat {
        private String knowledgeTag;
        private Integer wrongCount;
        private Integer totalCount;
        private Double errorRate;
    }
}