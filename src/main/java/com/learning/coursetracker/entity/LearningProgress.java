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
public class LearningProgress {

    private Long id;

    private Long userId;

    private Long chapterId;

    private Integer lastPosition;

    private Boolean isCompleted;

    private LocalDateTime completedAt;

    private LocalDateTime firstWatchedAt;

    private LocalDateTime lastWatchedAt;

    private Integer totalWatchTime;
}
