package com.learning.coursetracker.service;

import com.learning.coursetracker.dto.ProgressUpdateRequest;
import com.learning.coursetracker.entity.Chapter;
import com.learning.coursetracker.entity.LearningProgress;
import com.learning.coursetracker.mapper.ChapterMapper;
import com.learning.coursetracker.mapper.LearningProgressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LearningProgressService {

    private final LearningProgressMapper progressMapper;
    private final ChapterMapper chapterMapper;

    public LearningProgressService(LearningProgressMapper progressMapper, ChapterMapper chapterMapper) {
        this.progressMapper = progressMapper;
        this.chapterMapper = chapterMapper;
    }

    @Transactional
    public LearningProgress updateProgress(Long userId, ProgressUpdateRequest request) {
        Optional<LearningProgress> existingOpt = progressMapper.findByUserAndChapter(userId, request.getChapterId());

        if (existingOpt.isPresent()) {
            LearningProgress progress = existingOpt.get();

            progress.setLastPosition(request.getPosition());
            progress.setLastWatchedAt(LocalDateTime.now());

            if (request.getWatchTime() != null) {
                progress.setTotalWatchTime(progress.getTotalWatchTime() + request.getWatchTime());
            }

            Chapter chapter = chapterMapper.findById(request.getChapterId()).orElse(null);
            if (chapter != null && chapter.getDuration() > 0) {
                double progressRatio = (double) request.getPosition() / chapter.getDuration();
                if (progressRatio >= 0.9 && !progress.getIsCompleted()) {
                    progress.setIsCompleted(true);
                    progress.setCompletedAt(LocalDateTime.now());
                }
            }

            progressMapper.update(progress);
            return progress;
        } else {
            LearningProgress progress = LearningProgress.builder()
                    .userId(userId)
                    .chapterId(request.getChapterId())
                    .lastPosition(request.getPosition())
                    .isCompleted(false)
                    .firstWatchedAt(LocalDateTime.now())
                    .lastWatchedAt(LocalDateTime.now())
                    .totalWatchTime(request.getWatchTime() != null ? request.getWatchTime() : 0)
                    .build();

            progressMapper.insert(progress);
            return progress;
        }
    }

    public Optional<LearningProgress> getProgress(Long userId, Long chapterId) {
        return progressMapper.findByUserAndChapter(userId, chapterId);
    }

    public Integer getCompletedChapterCount(Long userId, Long courseId) {
        return progressMapper.countCompletedChapters(userId, courseId);
    }

    public Integer getTotalChapterCount(Long courseId) {
        return progressMapper.countTotalChaptersByCourseId(courseId);
    }

    public Integer getTotalWatchTime(Long userId) {
        return progressMapper.getTotalWatchTimeByUser(userId);
    }

    public Integer getTotalWatchTimeForCourse(Long userId, Long courseId) {
        return progressMapper.getTotalWatchTimeByUserAndCourse(userId, courseId);
    }
}
