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
public class Course {

    private Long id;

    private String title;

    private String description;

    private Long categoryId;

    private String instructor;

    private String coverImageUrl;

    private Integer totalDuration;

    private String difficultyLevel;

    private String status;

    private Boolean published;

    private Long createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CourseCategory category;
}
