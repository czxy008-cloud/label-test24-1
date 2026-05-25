package com.learning.coursetracker.controller;

import com.learning.coursetracker.dto.ApiResponse;
import com.learning.coursetracker.entity.Chapter;
import com.learning.coursetracker.entity.Quiz;
import com.learning.coursetracker.service.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Chapter>>> getChaptersByCourse(@PathVariable Long courseId) {
        List<Chapter> chapters = chapterService.getChaptersByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(chapters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Chapter>> getChapterById(@PathVariable Long id) {
        Optional<Chapter> chapter = chapterService.getChapterById(id);
        return chapter.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
                .orElse(ResponseEntity.ok(ApiResponse.error(404, "章节不存在")));
    }

    @GetMapping("/{id}/quizzes")
    public ResponseEntity<ApiResponse<List<Quiz>>> getChapterQuizzes(@PathVariable Long id) {
        List<Quiz> quizzes = chapterService.getChapterQuizzes(id);
        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }
}
