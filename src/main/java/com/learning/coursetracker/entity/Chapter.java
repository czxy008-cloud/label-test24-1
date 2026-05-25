package com.learning.coursetracker.entity;

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
public class Chapter {

    private Long id;

    private Long courseId;

    private String title;

    private String description;

    private String videoUrl;

    private Integer duration;

    private Integer sortOrder;

    private Boolean isFree;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Quiz> quizzes;
}
