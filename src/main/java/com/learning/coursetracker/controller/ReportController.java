package com.learning.coursetracker.controller;

import com.learning.coursetracker.dto.ApiResponse;
import com.learning.coursetracker.dto.LearningReportDTO;
import com.learning.coursetracker.dto.WrongAnswerBookDTO;
import com.learning.coursetracker.security.CurrentUser;
import com.learning.coursetracker.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final CurrentUser currentUser;

    public ReportController(ReportService reportService, CurrentUser currentUser) {
        this.reportService = reportService;
        this.currentUser = currentUser;
    }

    @GetMapping("/my-learning")
    public ResponseEntity<ApiResponse<LearningReportDTO>> getMyLearningReport() {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        LearningReportDTO report = reportService.generateLearningReport(userId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/my-wrong-answers")
    public ResponseEntity<ApiResponse<WrongAnswerBookDTO>> getMyWrongAnswerBook() {
        Long userId = currentUser.getUserId();
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        WrongAnswerBookDTO book = reportService.getWrongAnswerBook(userId);
        return ResponseEntity.ok(ApiResponse.success(book));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<LearningReportDTO>> getUserReport(@PathVariable Long userId) {
        Long currentUserId = currentUser.getUserId();
        if (currentUserId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        if (!currentUserId.equals(userId) && !currentUser.isAdmin()) {
            return ResponseEntity.ok(ApiResponse.error(403, "无权限访问该用户数据"));
        }

        LearningReportDTO report = reportService.generateLearningReport(userId);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/user/{userId}/wrong-answers")
    public ResponseEntity<ApiResponse<WrongAnswerBookDTO>> getUserWrongAnswerBook(@PathVariable Long userId) {
        Long currentUserId = currentUser.getUserId();
        if (currentUserId == null) {
            return ResponseEntity.ok(ApiResponse.error(401, "未登录"));
        }

        if (!currentUserId.equals(userId) && !currentUser.isAdmin()) {
            return ResponseEntity.ok(ApiResponse.error(403, "无权限访问该用户数据"));
        }

        WrongAnswerBookDTO book = reportService.getWrongAnswerBook(userId);
        return ResponseEntity.ok(ApiResponse.success(book));
    }
}
