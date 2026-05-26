package com.learning.coursetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultResponse {

    private Integer totalScore;
    private Integer maxScore;
    private Integer correctCount;
    private Integer totalCount;
    private Double accuracy;
    private Boolean isPassed;
    private List<QuizDetail> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizDetail {
        private Long quizId;
        private String questionText;
        private String userAnswer;
        private String correctAnswer;
        private String correctOptionText;
        private Boolean isCorrect;
        private String explanation;
        private List<String> knowledgeTags;
        private Integer score;
    }
}
