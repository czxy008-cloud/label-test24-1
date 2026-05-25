package com.learning.coursetracker.controller;

import com.learning.coursetracker.dto.ApiResponse;
import com.learning.coursetracker.dto.ProgressUpdateRequest;
import com.learning.coursetracker.entity.LearningProgress;
import com.learning.coursetracker.security.CurrentUser;
import com.learning.coursetracker.service.LearningProgressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/progress")
public class LearningProgressController {

    private final LearningProgressService progressService;
    private final CurrentUser currentUser;

    public LearningProgressController(LearningProgressService progressService, CurrentUser currentUser) {
        this.progressService = progressService;
        this.currentUser = currentUser;
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<LearningProgress>> updateProgress(@Valid @RequestBody ProgressUpdateRequest request) {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        LearningProgress progress = progressService.updateProgress(userId, request);
        return ResponseEntity.ok(ApiResponse.success("进度更新成功", progress));
    }

    @GetMapping("/{chapterId}")
    public ResponseEntity<ApiResponse<LearningProgress>> getProgress(@PathVariable Long chapterId) {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        Optional<LearningProgress> progress = progressService.getProgress(userId, chapterId);
        return progress.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
                .orElse(ResponseEntity.ok(ApiResponse.success(null)));
    }

    @GetMapping("/course/{courseId}/completed")
    public ResponseEntity<ApiResponse<Integer>> getCompletedChapters(@PathVariable Long courseId) {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        Integer count = progressService.getCompletedChapterCount(userId, courseId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
