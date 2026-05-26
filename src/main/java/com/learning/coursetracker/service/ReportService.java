package com.learning.coursetracker.service;

import com.learning.coursetracker.dto.LearningReportDTO;
import com.learning.coursetracker.dto.WrongAnswerBookDTO;
import com.learning.coursetracker.entity.Chapter;
import com.learning.coursetracker.entity.ChapterQuizResult;
import com.learning.coursetracker.entity.Course;
import com.learning.coursetracker.entity.Quiz;
import com.learning.coursetracker.entity.QuizScore;
import com.learning.coursetracker.entity.User;
import com.learning.coursetracker.exception.BusinessException;
import com.learning.coursetracker.mapper.ChapterMapper;
import com.learning.coursetracker.mapper.ChapterQuizResultMapper;
import com.learning.coursetracker.mapper.CourseMapper;
import com.learning.coursetracker.mapper.LearningProgressMapper;
import com.learning.coursetracker.mapper.QuizMapper;
import com.learning.coursetracker.mapper.QuizScoreMapper;
import com.learning.coursetracker.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final LearningProgressMapper progressMapper;
    private final ChapterQuizResultMapper quizResultMapper;
    private final QuizScoreMapper quizScoreMapper;
    private final QuizMapper quizMapper;
    private final ChapterMapper chapterMapper;

    public ReportService(UserMapper userMapper, CourseMapper courseMapper,
                        LearningProgressMapper progressMapper, ChapterQuizResultMapper quizResultMapper,
                        QuizScoreMapper quizScoreMapper, QuizMapper quizMapper, ChapterMapper chapterMapper) {
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.progressMapper = progressMapper;
        this.quizResultMapper = quizResultMapper;
        this.quizScoreMapper = quizScoreMapper;
        this.quizMapper = quizMapper;
        this.chapterMapper = chapterMapper;
    }

    public LearningReportDTO generateLearningReport(Long userId) {
        User user = userMapper.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));

        List<Course> allCourses = courseMapper.findPublishedCourses(null, null);
        Map<Long, Course> courseMap = allCourses.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        List<Map<String, Object>> progressStats = progressMapper.getCourseProgressStatsForUser(userId);
        List<Map<String, Object>> scoreStats = quizResultMapper.getAverageScoresByUserForAllCourses(userId);

        Map<Long, Map<String, Object>> progressMap = progressStats.stream()
                .collect(Collectors.toMap(
                        m -> ((Number) m.get("course_id")).longValue(),
                        m -> m
                ));
        Map<Long, Map<String, Object>> scoreMap = scoreStats.stream()
                .collect(Collectors.toMap(
                        m -> ((Number) m.get("course_id")).longValue(),
                        m -> m
                ));

        int totalCourses = 0;
        int completedCourses = 0;
        int inProgressCourses = 0;
        int totalChapters = 0;
        int completedChapters = 0;

        List<LearningReportDTO.CourseProgress> courseProgressList = new ArrayList<>();

        for (Course course : allCourses) {
            Map<String, Object> ps = progressMap.get(course.getId());
            if (ps == null) continue;

            int courseTotalChapters = ((Number) ps.getOrDefault("total_chapters", 0)).intValue();
            if (courseTotalChapters == 0) {
                continue;
            }

            int courseCompletedChapters = ((Number) ps.getOrDefault("completed_chapters", 0)).intValue();
            int courseWatchTime = ((Number) ps.getOrDefault("total_watch_time", 0)).intValue();

            Map<String, Object> ss = scoreMap.get(course.getId());
            double courseAvgScore = ss != null ? ((Number) ss.getOrDefault("average_score", 0)).doubleValue() : 0.0;

            totalCourses++;
            totalChapters += courseTotalChapters;
            completedChapters += courseCompletedChapters;

            double completionRate = (double) courseCompletedChapters / courseTotalChapters * 100;

            if (completionRate >= 100) {
                completedCourses++;
            } else if (completionRate > 0) {
                inProgressCourses++;
            }

            courseProgressList.add(LearningReportDTO.CourseProgress.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .totalChapters(courseTotalChapters)
                    .completedChapters(courseCompletedChapters)
                    .completionRate(Math.round(completionRate * 100.0) / 100.0)
                    .averageScore(Math.round(courseAvgScore * 100.0) / 100.0)
                    .totalWatchTime(courseWatchTime)
                    .build());
        }

        double overallCompletionRate = totalChapters > 0 ? (double) completedChapters / totalChapters * 100 : 0;

        Double averageScore = quizResultMapper.getAverageScoreByUser(userId);
        int passedChapters = quizResultMapper.countPassedChapters(userId);
        List<ChapterQuizResult> allResults = quizResultMapper.findByUserId(userId);

        Integer totalWatchTime = progressMapper.getTotalWatchTimeByUser(userId);

        return LearningReportDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .totalCourses(totalCourses)
                .completedCourses(completedCourses)
                .inProgressCourses(inProgressCourses)
                .totalChapters(totalChapters)
                .completedChapters(completedChapters)
                .overallCompletionRate(Math.round(overallCompletionRate * 100.0) / 100.0)
                .averageScore(averageScore != null ? Math.round(averageScore * 100.0) / 100.0 : 0.0)
                .totalQuizzes(allResults.size())
                .passedQuizzes(passedChapters)
                .totalWatchTime(totalWatchTime != null ? totalWatchTime : 0)
                .courseProgressList(courseProgressList)
                .build();
    }

    public WrongAnswerBookDTO getWrongAnswerBook(Long userId) {
        userMapper.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));

        List<QuizScore> allScores = quizScoreMapper.findByUserId(userId);
        List<QuizScore> wrongScores = quizScoreMapper.findWrongByUserId(userId);

        int totalQuestionCount = allScores.size();
        int totalWrongCount = wrongScores.size();
        double overallErrorRate = totalQuestionCount > 0
                ? Math.round((double) totalWrongCount / totalQuestionCount * 10000.0) / 100.0
                : 0.0;

        Set<Long> allQuizIds = allScores.stream()
                .map(QuizScore::getQuizId)
                .collect(Collectors.toSet());

        Map<Long, Quiz> quizCache = new HashMap<>();
        if (!allQuizIds.isEmpty()) {
            List<Quiz> quizzes = quizMapper.findByIds(new ArrayList<>(allQuizIds));
            quizCache = quizzes.stream()
                    .collect(Collectors.toMap(Quiz::getId, q -> q));
        }

        Map<Long, List<QuizScore>> wrongScoresByChapter = wrongScores.stream()
                .collect(Collectors.groupingBy(QuizScore::getChapterId));
        Map<Long, List<QuizScore>> allScoresByChapter = allScores.stream()
                .collect(Collectors.groupingBy(QuizScore::getChapterId));

        Map<String, int[]> globalKnowledgeStats = new HashMap<>();

        List<WrongAnswerBookDTO.CourseWrongStats> courseList = new ArrayList<>();

        List<Course> allCourses = courseMapper.findPublishedCourses(null, null);
        for (Course course : allCourses) {
            List<Chapter> chapters = chapterMapper.findByCourseId(course.getId());
            if (chapters.isEmpty()) continue;

            int courseWrongCount = 0;
            int courseTotalCount = 0;
            List<WrongAnswerBookDTO.ChapterWrongStats> chapterList = new ArrayList<>();
            Map<String, int[]> courseKnowledgeStats = new HashMap<>();

            for (Chapter chapter : chapters) {
                List<QuizScore> chapterWrong = wrongScoresByChapter.getOrDefault(chapter.getId(), new ArrayList<>());
                List<QuizScore> chapterAll = allScoresByChapter.getOrDefault(chapter.getId(), new ArrayList<>());

                if (chapterWrong.isEmpty() && chapterAll.isEmpty()) continue;

                courseWrongCount += chapterWrong.size();
                courseTotalCount += chapterAll.size();

                List<WrongAnswerBookDTO.WrongQuizDetail> wrongQuizList = new ArrayList<>();
                for (QuizScore ws : chapterWrong) {
                    Quiz quiz = quizCache.get(ws.getQuizId());
                    if (quiz == null) continue;

                    wrongQuizList.add(WrongAnswerBookDTO.WrongQuizDetail.builder()
                            .quizId(quiz.getId())
                            .questionText(quiz.getQuestionText())
                            .userAnswer(ws.getUserAnswer())
                            .correctAnswer(quiz.getCorrectAnswer())
                            .correctOptionText(getOptionText(quiz, quiz.getCorrectAnswer()))
                            .explanation(quiz.getExplanation())
                            .knowledgeTags(parseKnowledgeTags(quiz.getKnowledgeTags()))
                            .submittedAt(ws.getSubmittedAt())
                            .build());

                    for (String tag : parseKnowledgeTags(quiz.getKnowledgeTags())) {
                        courseKnowledgeStats.computeIfAbsent(tag, k -> new int[]{0, 0});
                        courseKnowledgeStats.get(tag)[0]++;
                    }
                }

                for (QuizScore as : chapterAll) {
                    Quiz quiz = quizCache.get(as.getQuizId());
                    if (quiz == null) continue;
                    for (String tag : parseKnowledgeTags(quiz.getKnowledgeTags())) {
                        courseKnowledgeStats.computeIfAbsent(tag, k -> new int[]{0, 0});
                        courseKnowledgeStats.get(tag)[1]++;
                    }
                }

                double chapterErrorRate = chapterAll.size() > 0
                        ? Math.round((double) chapterWrong.size() / chapterAll.size() * 10000.0) / 100.0
                        : 0.0;

                chapterList.add(WrongAnswerBookDTO.ChapterWrongStats.builder()
                        .chapterId(chapter.getId())
                        .chapterTitle(chapter.getTitle())
                        .totalWrongCount(chapterWrong.size())
                        .totalQuestionCount(chapterAll.size())
                        .errorRate(chapterErrorRate)
                        .wrongQuizList(wrongQuizList)
                        .build());
            }

            if (courseTotalCount == 0) continue;

            double courseErrorRate = courseTotalCount > 0
                    ? Math.round((double) courseWrongCount / courseTotalCount * 10000.0) / 100.0
                    : 0.0;

            List<WrongAnswerBookDTO.KnowledgePointErrorStat> knowledgePointStats = courseKnowledgeStats.entrySet().stream()
                    .map(e -> {
                        String tag = e.getKey();
                        int wrong = e.getValue()[0];
                        int total = e.getValue()[1];
                        double rate = total > 0 ? Math.round((double) wrong / total * 10000.0) / 100.0 : 0.0;
                        return WrongAnswerBookDTO.KnowledgePointErrorStat.builder()
                                .knowledgeTag(tag)
                                .wrongCount(wrong)
                                .totalCount(total)
                                .errorRate(rate)
                                .build();
                    })
                    .sorted(Comparator.comparing(WrongAnswerBookDTO.KnowledgePointErrorStat::getErrorRate).reversed())
                    .collect(Collectors.toList());

            for (Map.Entry<String, int[]> entry : courseKnowledgeStats.entrySet()) {
                globalKnowledgeStats.computeIfAbsent(entry.getKey(), k -> new int[]{0, 0});
                globalKnowledgeStats.get(entry.getKey())[0] += entry.getValue()[0];
                globalKnowledgeStats.get(entry.getKey())[1] += entry.getValue()[1];
            }

            courseList.add(WrongAnswerBookDTO.CourseWrongStats.builder()
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .totalWrongCount(courseWrongCount)
                    .totalQuestionCount(courseTotalCount)
                    .errorRate(courseErrorRate)
                    .chapterList(chapterList)
                    .knowledgePointStats(knowledgePointStats)
                    .build());
        }

        List<WrongAnswerBookDTO.KnowledgePointErrorStat> topWeakKnowledgePoints = globalKnowledgeStats.entrySet().stream()
                .map(e -> {
                    String tag = e.getKey();
                    int wrong = e.getValue()[0];
                    int total = e.getValue()[1];
                    double rate = total > 0 ? Math.round((double) wrong / total * 10000.0) / 100.0 : 0.0;
                    return WrongAnswerBookDTO.KnowledgePointErrorStat.builder()
                            .knowledgeTag(tag)
                            .wrongCount(wrong)
                            .totalCount(total)
                            .errorRate(rate)
                            .build();
                })
                .sorted(Comparator.comparing(WrongAnswerBookDTO.KnowledgePointErrorStat::getErrorRate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return WrongAnswerBookDTO.builder()
                .userId(userId)
                .totalWrongCount(totalWrongCount)
                .totalQuestionCount(totalQuestionCount)
                .overallErrorRate(overallErrorRate)
                .courseList(courseList)
                .topWeakKnowledgePoints(topWeakKnowledgePoints)
                .build();
    }

    private String getOptionText(Quiz quiz, String answerLetter) {
        if (answerLetter == null) return null;
        switch (answerLetter.toUpperCase()) {
            case "A": return quiz.getOptionA();
            case "B": return quiz.getOptionB();
            case "C": return quiz.getOptionC();
            case "D": return quiz.getOptionD();
            default: return null;
        }
    }

    private List<String> parseKnowledgeTags(String knowledgeTags) {
        if (knowledgeTags == null || knowledgeTags.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(knowledgeTags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
