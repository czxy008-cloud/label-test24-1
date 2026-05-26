-- ============================================================
-- 在线课程学习进度跟踪系统 - 数据库初始化脚本
-- 数据库: PostgreSQL
-- ============================================================

-- ============================================================
-- 1. 用户表 (users)
-- 存储系统用户信息，包括管理员和普通学习者
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL       PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    email           VARCHAR(100)    NOT NULL UNIQUE,
    nickname        VARCHAR(50),
    role            VARCHAR(20)     NOT NULL DEFAULT 'STUDENT',
    enabled         BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 为用户表添加注释
COMMENT ON TABLE users IS '用户信息表';
COMMENT ON COLUMN users.id IS '用户唯一标识';
COMMENT ON COLUMN users.username IS '登录用户名';
COMMENT ON COLUMN users.password IS '加密后的密码';
COMMENT ON COLUMN users.email IS '邮箱地址';
COMMENT ON COLUMN users.nickname IS '昵称';
COMMENT ON COLUMN users.role IS '角色：ADMIN-管理员，STUDENT-普通学员';
COMMENT ON COLUMN users.enabled IS '账号是否启用';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- ============================================================
-- 2. 课程分类表 (course_categories)
-- 存储课程的分类信息，用于课程目录的分类筛选
-- ============================================================
CREATE TABLE IF NOT EXISTS course_categories (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    description     TEXT,
    parent_id       BIGINT,
    sort_order      INT             DEFAULT 0,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES course_categories(id) ON DELETE SET NULL
);

COMMENT ON TABLE course_categories IS '课程分类表';
COMMENT ON COLUMN course_categories.id IS '分类唯一标识';
COMMENT ON COLUMN course_categories.name IS '分类名称';
COMMENT ON COLUMN course_categories.description IS '分类描述';
COMMENT ON COLUMN course_categories.parent_id IS '父分类ID，支持多级分类';
COMMENT ON COLUMN course_categories.sort_order IS '排序顺序，数值越小越靠前';
COMMENT ON COLUMN course_categories.created_at IS '创建时间';

CREATE INDEX IF NOT EXISTS idx_categories_parent ON course_categories(parent_id);

-- ============================================================
-- 3. 课程信息表 (courses)
-- 存储课程的基本信息
-- ============================================================
CREATE TABLE IF NOT EXISTS courses (
    id                  BIGSERIAL       PRIMARY KEY,
    title               VARCHAR(200)    NOT NULL,
    description         TEXT,
    category_id         BIGINT,
    instructor          VARCHAR(100),
    cover_image_url     VARCHAR(500),
    total_duration      INT             DEFAULT 0,
    difficulty_level    VARCHAR(20)     DEFAULT 'BEGINNER',
    status              VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    published           BOOLEAN         NOT NULL DEFAULT FALSE,
    created_by          BIGINT,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES course_categories(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

COMMENT ON TABLE courses IS '课程信息表';
COMMENT ON COLUMN courses.id IS '课程唯一标识';
COMMENT ON COLUMN courses.title IS '课程标题';
COMMENT ON COLUMN courses.description IS '课程描述';
COMMENT ON COLUMN courses.category_id IS '所属分类ID';
COMMENT ON COLUMN courses.instructor IS '讲师姓名';
COMMENT ON COLUMN courses.cover_image_url IS '课程封面图片URL';
COMMENT ON COLUMN courses.total_duration IS '课程总时长（秒）';
COMMENT ON COLUMN courses.difficulty_level IS '难度等级：BEGINNER-入门，INTERMEDIATE-中级，ADVANCED-高级';
COMMENT ON COLUMN courses.status IS '课程状态：DRAFT-草稿，PUBLISHED-已发布，ARCHIVED-已归档';
COMMENT ON COLUMN courses.published IS '是否已发布';
COMMENT ON COLUMN courses.created_by IS '创建者用户ID';
COMMENT ON COLUMN courses.created_at IS '创建时间';
COMMENT ON COLUMN courses.updated_at IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_courses_category ON courses(category_id);
CREATE INDEX IF NOT EXISTS idx_courses_status ON courses(status);
CREATE INDEX IF NOT EXISTS idx_courses_published ON courses(published);

-- ============================================================
-- 4. 章节内容表 (chapters)
-- 存储课程的章节/课时信息，包括视频资源
-- ============================================================
CREATE TABLE IF NOT EXISTS chapters (
    id              BIGSERIAL       PRIMARY KEY,
    course_id       BIGINT          NOT NULL,
    title           VARCHAR(200)    NOT NULL,
    description     TEXT,
    video_url       VARCHAR(500),
    duration        INT             DEFAULT 0,
    sort_order      INT             DEFAULT 0,
    is_free         BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

COMMENT ON TABLE chapters IS '章节内容表';
COMMENT ON COLUMN chapters.id IS '章节唯一标识';
COMMENT ON COLUMN chapters.course_id IS '所属课程ID';
COMMENT ON COLUMN chapters.title IS '章节标题';
COMMENT ON COLUMN chapters.description IS '章节描述';
COMMENT ON COLUMN chapters.video_url IS '视频资源URL';
COMMENT ON COLUMN chapters.duration IS '视频时长（秒）';
COMMENT ON COLUMN chapters.sort_order IS '章节排序顺序';
COMMENT ON COLUMN chapters.is_free IS '是否为免费试看章节';
COMMENT ON COLUMN chapters.created_at IS '创建时间';
COMMENT ON COLUMN chapters.updated_at IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_chapters_course ON chapters(course_id);

-- ============================================================
-- 5. 测验题目表 (quizzes)
-- 存储章节关联的测验题目
-- ============================================================
CREATE TABLE IF NOT EXISTS quizzes (
    id              BIGSERIAL       PRIMARY KEY,
    chapter_id      BIGINT          NOT NULL,
    question_text   TEXT            NOT NULL,
    option_a        VARCHAR(500),
    option_b        VARCHAR(500),
    option_c        VARCHAR(500),
    option_d        VARCHAR(500),
    correct_answer  CHAR(1)         NOT NULL,
    explanation     TEXT,
    knowledge_tags  VARCHAR(500),
    points          INT             DEFAULT 10,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

COMMENT ON TABLE quizzes IS '测验题目表';
COMMENT ON COLUMN quizzes.id IS '题目唯一标识';
COMMENT ON COLUMN quizzes.chapter_id IS '所属章节ID';
COMMENT ON COLUMN quizzes.question_text IS '题目内容';
COMMENT ON COLUMN quizzes.option_a IS '选项A';
COMMENT ON COLUMN quizzes.option_b IS '选项B';
COMMENT ON COLUMN quizzes.option_c IS '选项C';
COMMENT ON COLUMN quizzes.option_d IS '选项D';
COMMENT ON COLUMN quizzes.correct_answer IS '正确答案：A/B/C/D';
COMMENT ON COLUMN quizzes.explanation IS '答案解析';
COMMENT ON COLUMN quizzes.knowledge_tags IS '关联知识点标签，多个标签以逗号分隔';
COMMENT ON COLUMN quizzes.points IS '题目分值';
COMMENT ON COLUMN quizzes.created_at IS '创建时间';

CREATE INDEX IF NOT EXISTS idx_quizzes_chapter ON quizzes(chapter_id);

-- ============================================================
-- 6. 用户学习记录表 (learning_progress)
-- 记录用户观看视频的进度，包括最后观看时间点
-- ============================================================
CREATE TABLE IF NOT EXISTS learning_progress (
    id                  BIGSERIAL       PRIMARY KEY,
    user_id             BIGINT          NOT NULL,
    chapter_id          BIGINT          NOT NULL,
    last_position       INT             DEFAULT 0,
    is_completed        BOOLEAN         NOT NULL DEFAULT FALSE,
    completed_at        TIMESTAMP,
    first_watched_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_watched_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_watch_time    INT             DEFAULT 0,
    UNIQUE(user_id, chapter_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

COMMENT ON TABLE learning_progress IS '学习进度记录表';
COMMENT ON COLUMN learning_progress.id IS '记录唯一标识';
COMMENT ON COLUMN learning_progress.user_id IS '用户ID';
COMMENT ON COLUMN learning_progress.chapter_id IS '章节ID';
COMMENT ON COLUMN learning_progress.last_position IS '最后观看的时间点（秒），用于断点续播';
COMMENT ON COLUMN learning_progress.is_completed IS '是否已完成该章节学习';
COMMENT ON COLUMN learning_progress.completed_at IS '完成学习的时间';
COMMENT ON COLUMN learning_progress.first_watched_at IS '首次观看时间';
COMMENT ON COLUMN learning_progress.last_watched_at IS '最后观看时间';
COMMENT ON COLUMN learning_progress.total_watch_time IS '累计观看时长（秒）';

CREATE INDEX IF NOT EXISTS idx_progress_user ON learning_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_progress_chapter ON learning_progress(chapter_id);

-- ============================================================
-- 7. 测验成绩表 (quiz_scores)
-- 记录用户提交测验的成绩信息
-- ============================================================
CREATE TABLE IF NOT EXISTS quiz_scores (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    chapter_id      BIGINT          NOT NULL,
    quiz_id         BIGINT          NOT NULL,
    user_answer     CHAR(1),
    is_correct      BOOLEAN         NOT NULL DEFAULT FALSE,
    score           INT             DEFAULT 0,
    submitted_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    UNIQUE(user_id, chapter_id, quiz_id)
);

COMMENT ON TABLE quiz_scores IS '测验成绩表';
COMMENT ON COLUMN quiz_scores.id IS '成绩记录唯一标识';
COMMENT ON COLUMN quiz_scores.user_id IS '用户ID';
COMMENT ON COLUMN quiz_scores.chapter_id IS '章节ID';
COMMENT ON COLUMN quiz_scores.quiz_id IS '题目ID';
COMMENT ON COLUMN quiz_scores.user_answer IS '用户答案';
COMMENT ON COLUMN quiz_scores.is_correct IS '是否答对';
COMMENT ON COLUMN quiz_scores.score IS '得分';
COMMENT ON COLUMN quiz_scores.submitted_at IS '提交时间';

CREATE INDEX IF NOT EXISTS idx_scores_user ON quiz_scores(user_id);
CREATE INDEX IF NOT EXISTS idx_scores_chapter ON quiz_scores(chapter_id);
CREATE INDEX IF NOT EXISTS idx_scores_submitted ON quiz_scores(submitted_at);
CREATE INDEX IF NOT EXISTS idx_scores_user_chapter ON quiz_scores(user_id, chapter_id);
CREATE INDEX IF NOT EXISTS idx_scores_user_correct ON quiz_scores(user_id, is_correct);
CREATE INDEX IF NOT EXISTS idx_quiz_scores_quiz ON quiz_scores(quiz_id);

CREATE INDEX IF NOT EXISTS idx_results_user_chapter ON chapter_quiz_results(user_id, chapter_id);
CREATE INDEX IF NOT EXISTS idx_results_user_passed ON chapter_quiz_results(user_id, is_passed);

-- ============================================================
-- 8. 章节测验成绩汇总表 (chapter_quiz_results)
-- 汇总用户在各章节测验的总体成绩
-- ============================================================
CREATE TABLE IF NOT EXISTS chapter_quiz_results (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    chapter_id      BIGINT          NOT NULL,
    total_score     INT             DEFAULT 0,
    max_score       INT             DEFAULT 0,
    correct_count   INT             DEFAULT 0,
    total_count     INT             DEFAULT 0,
    attempt_count   INT             DEFAULT 0,
    is_passed       BOOLEAN         NOT NULL DEFAULT FALSE,
    submitted_at    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, chapter_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapters(id) ON DELETE CASCADE
);

COMMENT ON TABLE chapter_quiz_results IS '章节测验成绩汇总表';
COMMENT ON COLUMN chapter_quiz_results.id IS '成绩汇总唯一标识';
COMMENT ON COLUMN chapter_quiz_results.user_id IS '用户ID';
COMMENT ON COLUMN chapter_quiz_results.chapter_id IS '章节ID';
COMMENT ON COLUMN chapter_quiz_results.total_score IS '实际得分';
COMMENT ON COLUMN chapter_quiz_results.max_score IS '满分';
COMMENT ON COLUMN chapter_quiz_results.correct_count IS '答对题数';
COMMENT ON COLUMN chapter_quiz_results.total_count IS '总题数';
COMMENT ON COLUMN chapter_quiz_results.attempt_count IS '尝试次数';
COMMENT ON COLUMN chapter_quiz_results.is_passed IS '是否通过';
COMMENT ON COLUMN chapter_quiz_results.submitted_at IS '提交时间';

-- ============================================================
-- 初始化示例数据
-- ============================================================

-- 插入课程分类
INSERT INTO course_categories (name, description, sort_order) VALUES
    ('编程开发', '计算机编程与软件开发相关课程', 1),
    ('数据科学', '数据分析、机器学习相关课程', 2),
    ('人工智能', '人工智能、深度学习相关课程', 3),
    ('前端开发', 'Web前端开发技术课程', 4),
    ('数据库技术', '数据库设计与管理课程', 5);

-- 插入示例用户 (密码: password123，BCrypt加密)
INSERT INTO users (username, password, email, nickname, role) VALUES
    ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', '系统管理员', 'ADMIN'),
    ('student01', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'student01@example.com', '张三', 'STUDENT'),
    ('student02', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'student02@example.com', '李四', 'STUDENT');

-- 插入示例课程
INSERT INTO courses (title, description, category_id, instructor, difficulty_level, status, published, created_by) VALUES
    ('Spring Boot入门到精通', '全面学习Spring Boot框架，从基础到实战项目', 1, '王老师', 'INTERMEDIATE', 'PUBLISHED', TRUE, 1),
    ('Python数据分析实战', '使用Python进行数据分析和可视化', 2, '李老师', 'BEGINNER', 'PUBLISHED', TRUE, 1),
    ('深度学习基础', '深度学习基础知识与神经网络入门', 3, '赵老师', 'ADVANCED', 'PUBLISHED', TRUE, 1);

-- 插入示例章节
INSERT INTO chapters (course_id, title, description, video_url, duration, sort_order, is_free) VALUES
    (1, 'Spring Boot概述', '了解Spring Boot的优势和特点', 'https://example.com/video/ch1.mp4', 1800, 1, TRUE),
    (1, '快速入门', '创建第一个Spring Boot项目', 'https://example.com/video/ch2.mp4', 2400, 2, TRUE),
    (1, '数据访问层', 'Spring Data JPA与MyBatis集成', 'https://example.com/video/ch3.mp4', 3000, 3, FALSE),
    (2, 'Python基础回顾', '回顾Python基础知识', 'https://example.com/video/py1.mp4', 2000, 1, TRUE),
    (2, 'Pandas入门', '学习Pandas数据处理', 'https://example.com/video/py2.mp4', 2500, 2, FALSE);

-- 插入示例测验题目
INSERT INTO quizzes (chapter_id, question_text, option_a, option_b, option_c, option_d, correct_answer, explanation, knowledge_tags, points) VALUES
    (1, 'Spring Boot的主要优势是什么？', '配置繁琐', '约定优于配置', '需要大量XML配置', '不支持自动配置', 'B', 'Spring Boot遵循约定优于配置的原则，简化了Spring应用的开发', 'Spring Boot,约定优于配置,自动配置', 10),
    (1, 'Spring Boot项目的入口注解是？', '@Component', '@Service', '@SpringBootApplication', '@Configuration', 'C', '@SpringBootApplication是Spring Boot项目的核心入口注解', 'Spring Boot,注解,核心注解', 10),
    (2, 'Pandas中用于处理表格数据的主要数据结构是？', 'Array', 'Series', 'DataFrame', 'List', 'C', 'DataFrame是Pandas中处理表格数据的主要数据结构', 'Pandas,DataFrame,数据结构', 10);
