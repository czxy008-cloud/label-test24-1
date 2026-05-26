package com.learning.coursetracker.controller;

import com.learning.coursetracker.dto.ApiResponse;
import com.learning.coursetracker.dto.QuizResultResponse;
import com.learning.coursetracker.dto.QuizSubmitRequest;
import com.learning.coursetracker.entity.ChapterQuizResult;
import com.learning.coursetracker.security.CurrentUser;
import com.learning.coursetracker.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final CurrentUser currentUser;

    public QuizController(QuizService quizService, CurrentUser currentUser) {
        this.quizService = quizService;
        this.currentUser = currentUser;
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<QuizResultResponse>> submitQuiz(@Valid @RequestBody QuizSubmitRequest request) {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        QuizResultResponse result = quizService.submitQuiz(userId, request);
        return ResponseEntity.ok(ApiResponse.success("提交成功", result));
    }

    @GetMapping("/chapter/{chapterId}/result")
    public ResponseEntity<ApiResponse<ChapterQuizResult>> getChapterResult(@PathVariable Long chapterId) {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        Optional<ChapterQuizResult> result = quizService.getChapterResult(userId, chapterId);
        return result.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
                .orElse(ResponseEntity.ok(ApiResponse.success(null)));
    }

    @GetMapping("/my-results")
    public ResponseEntity<ApiResponse<List<ChapterQuizResult>>> getUserResults() {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        List<ChapterQuizResult> results = quizService.getUserResults(userId);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
