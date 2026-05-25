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
public class CourseDTO {

    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String instructor;
    private String coverImageUrl;
    private Integer totalDuration;
    private String difficultyLevel;
    private String status;
    private LocalDateTime createdAt;

    private List<ChapterDTO> chapters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapterDTO {
        private Long id;
        private String title;
        private String description;
        private String videoUrl;
        private Integer duration;
        private Integer sortOrder;
        private Boolean isFree;
    }
}
