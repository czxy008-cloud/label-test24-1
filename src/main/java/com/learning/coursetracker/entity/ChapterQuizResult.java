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
public class ChapterQuizResult {

    private Long id;

    private Long userId;

    private Long chapterId;

    private Integer totalScore;

    private Integer maxScore;

    private Integer correctCount;

    private Integer totalCount;

    private Integer attemptCount;

    private Boolean isPassed;

    private LocalDateTime submittedAt;
}
