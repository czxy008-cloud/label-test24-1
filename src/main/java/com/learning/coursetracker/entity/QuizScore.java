package com.learning.coursetracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizScore {

    private Long id;

    private Long userId;

    private Long chapterId;

    private Long quizId;

    private String userAnswer;

    private Boolean isCorrect;

    private Integer score;

    private LocalDateTime submittedAt;
}
