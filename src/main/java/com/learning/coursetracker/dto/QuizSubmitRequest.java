package com.learning.coursetracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmitRequest {

    @NotNull(message = "章节ID不能为空")
    private Long chapterId;

    @NotNull(message = "答案列表不能为空")
    @NotEmpty(message = "答案列表不能为空")
    @Valid
    private List<QuizAnswer> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAnswer {
        @NotNull(message = "题目ID不能为空")
        private Long quizId;
        private String userAnswer;
    }
}
