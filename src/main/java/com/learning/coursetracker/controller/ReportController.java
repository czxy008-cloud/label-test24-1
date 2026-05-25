package com.learning.coursetracker.controller;

import com.learning.coursetracker.dto.ApiResponse;
import com.learning.coursetracker.dto.LearningReportDTO;
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

        try {
            LearningReportDTO report = reportService.generateLearningReport(userId);
            return ResponseEntity.ok(ApiResponse.success(report));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<LearningReportDTO>> getUserReport(@PathVariable Long userId) {
        try {
            LearningReportDTO report = reportService.generateLearningReport(userId);
            return ResponseEntity.ok(ApiResponse.success(report));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(ApiResponse.error(400, e.getMessage()));
        }
    }
}
