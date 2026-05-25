package com.learning.coursetracker.service;

import com.learning.coursetracker.dto.QuizResultResponse;
import com.learning.coursetracker.dto.QuizSubmitRequest;
import com.learning.coursetracker.entity.ChapterQuizResult;
import com.learning.coursetracker.entity.Quiz;
import com.learning.coursetracker.entity.QuizScore;
import com.learning.coursetracker.mapper.ChapterQuizResultMapper;
import com.learning.coursetracker.mapper.QuizMapper;
import com.learning.coursetracker.mapper.QuizScoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizMapper quizMapper;
    private final QuizScoreMapper quizScoreMapper;
    private final ChapterQuizResultMapper chapterQuizResultMapper;

    public QuizService(QuizMapper quizMapper, QuizScoreMapper quizScoreMapper, ChapterQuizResultMapper chapterQuizResultMapper) {
        this.quizMapper = quizMapper;
        this.quizScoreMapper = quizScoreMapper;
        this.chapterQuizResultMapper = chapterQuizResultMapper;
    }

    @Transactional
    public QuizResultResponse submitQuiz(Long userId, QuizSubmitRequest request) {
        List<Quiz> quizzes = quizMapper.findByChapterId(request.getChapterId());

        if (quizzes.isEmpty()) {
            throw new RuntimeException("该章节暂无测验题目");
        }

        quizScoreMapper.deleteByUserAndChapter(userId, request.getChapterId());

        List<QuizResultResponse.QuizDetail> details = new ArrayList<>();
        int totalScore = 0;
        int maxScore = 0;
        int correctCount = 0;

        for (Quiz quiz : quizzes) {
            maxScore += quiz.getPoints();

            String userAnswer = request.getAnswers().stream()
                    .filter(a -> a.getQuizId().equals(quiz.getId()))
                    .map(QuizSubmitRequest.QuizAnswer::getUserAnswer)
                    .findFirst()
                    .orElse(null);

            boolean isCorrect = userAnswer != null && userAnswer.equals(quiz.getCorrectAnswer());

            if (isCorrect) {
                correctCount++;
                totalScore += quiz.getPoints();
            }

            QuizScore quizScore = QuizScore.builder()
                    .userId(userId)
                    .chapterId(request.getChapterId())
                    .quizId(quiz.getId())
                    .userAnswer(userAnswer)
                    .isCorrect(isCorrect)
                    .score(isCorrect ? quiz.getPoints() : 0)
                    .submittedAt(LocalDateTime.now())
                    .build();

            quizScoreMapper.insert(quizScore);

            details.add(QuizResultResponse.QuizDetail.builder()
                    .quizId(quiz.getId())
                    .questionText(quiz.getQuestionText())
                    .userAnswer(userAnswer)
                    .correctAnswer(quiz.getCorrectAnswer())
                    .isCorrect(isCorrect)
                    .explanation(quiz.getExplanation())
                    .score(isCorrect ? quiz.getPoints() : 0)
                    .build());
        }

        double passingScore = maxScore * 0.6;
        boolean isPassed = totalScore >= passingScore;

        Optional<ChapterQuizResult> existingResult = chapterQuizResultMapper.findByUserAndChapter(userId, request.getChapterId());

        if (existingResult.isPresent()) {
            ChapterQuizResult result = existingResult.get();
            result.setTotalScore(totalScore);
            result.setMaxScore(maxScore);
            result.setCorrectCount(correctCount);
            result.setTotalCount(quizzes.size());
            result.setAttemptCount(result.getAttemptCount() + 1);
            result.setIsPassed(isPassed);
            result.setSubmittedAt(LocalDateTime.now());
            chapterQuizResultMapper.update(result);
        } else {
            ChapterQuizResult result = ChapterQuizResult.builder()
                    .userId(userId)
                    .chapterId(request.getChapterId())
                    .totalScore(totalScore)
                    .maxScore(maxScore)
                    .correctCount(correctCount)
                    .totalCount(quizzes.size())
                    .attemptCount(1)
                    .isPassed(isPassed)
                    .submittedAt(LocalDateTime.now())
                    .build();
            chapterQuizResultMapper.insert(result);
        }

        double accuracy = quizzes.size() > 0 ? (double) correctCount / quizzes.size() * 100 : 0;

        return QuizResultResponse.builder()
                .totalScore(totalScore)
                .maxScore(maxScore)
                .correctCount(correctCount)
                .totalCount(quizzes.size())
                .accuracy(accuracy)
                .isPassed(isPassed)
                .details(details)
                .build();
    }

    public Optional<ChapterQuizResult> getChapterResult(Long userId, Long chapterId) {
        return chapterQuizResultMapper.findByUserAndChapter(userId, chapterId);
    }

    public List<ChapterQuizResult> getUserResults(Long userId) {
        return chapterQuizResultMapper.findByUserId(userId);
    }
}
