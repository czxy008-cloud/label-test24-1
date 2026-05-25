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
public class CourseCategory {

    private Long id;

    private String name;

    private String description;

    private Long parentId;

    private Integer sortOrder;

    private LocalDateTime createdAt;
}
