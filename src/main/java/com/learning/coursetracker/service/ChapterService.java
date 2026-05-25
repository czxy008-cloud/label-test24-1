package com.learning.coursetracker.service;

import com.learning.coursetracker.entity.Chapter;
import com.learning.coursetracker.entity.Quiz;
import com.learning.coursetracker.mapper.ChapterMapper;
import com.learning.coursetracker.mapper.QuizMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    private final ChapterMapper chapterMapper;
    private final QuizMapper quizMapper;

    public ChapterService(ChapterMapper chapterMapper, QuizMapper quizMapper) {
        this.chapterMapper = chapterMapper;
        this.quizMapper = quizMapper;
    }

    public List<Chapter> getChaptersByCourse(Long courseId) {
        return chapterMapper.findByCourseId(courseId);
    }

    public Optional<Chapter> getChapterById(Long id) {
        Optional<Chapter> chapterOpt = chapterMapper.findById(id);
        chapterOpt.ifPresent(chapter -> {
            List<Quiz> quizzes = quizMapper.findByChapterId(id);
            chapter.setQuizzes(quizzes);
        });
        return chapterOpt;
    }

    public List<Quiz> getChapterQuizzes(Long chapterId) {
        return quizMapper.findByChapterId(chapterId);
    }
}
