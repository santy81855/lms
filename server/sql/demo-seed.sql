-- LMS / Course Manager demo seed data
-- Run after server/sql/production-schema.sql has created the database and tables.
-- Demo passwords use Spring's DelegatingPasswordEncoder {noop} prefix for local demo convenience.
-- Login password for all seeded users: Password123!

USE lms;

START TRANSACTION;

-- Ensure platform roles exist even if the schema seed was skipped.
INSERT IGNORE INTO roles (name, description)
VALUES
    ('STUDENT', 'Can enroll in courses and complete lessons, quizzes, and assignments'),
    ('TEACHER', 'Can create courses, generate curriculum, publish content, and view student progress'),
    ('ADMIN', 'Can manage users, roles, and platform-level data');

-- Clean only this demo dataset so the script is repeatable.
DELETE FROM courses
WHERE join_code IN ('ECO2026');

DELETE FROM users
WHERE email IN (
                'teacher.demo@example.com',
                'student.demo@example.com',
                'student.completed@example.com',
                'student.dropped@example.com'
    );

-- Demo users
INSERT INTO users (first_name, last_name, email, password_hash, account_status)
VALUES
    ('Tara', 'Green', 'teacher.demo@example.com', '{noop}Password123!', 'ACTIVE'),
    ('Maya', 'Rivera', 'student.demo@example.com', '{noop}Password123!', 'ACTIVE'),
    ('Jordan', 'Lee', 'student.completed@example.com', '{noop}Password123!', 'ACTIVE'),
    ('Sam', 'Patel', 'student.dropped@example.com', '{noop}Password123!', 'ACTIVE');

SET @teacher_id = (SELECT id FROM users WHERE email = 'teacher.demo@example.com');
SET @student_id = (SELECT id FROM users WHERE email = 'student.demo@example.com');
SET @completed_student_id = (SELECT id FROM users WHERE email = 'student.completed@example.com');
SET @dropped_student_id = (SELECT id FROM users WHERE email = 'student.dropped@example.com');
SET @teacher_role_id = (SELECT id FROM roles WHERE name = 'TEACHER');
SET @student_role_id = (SELECT id FROM roles WHERE name = 'STUDENT');

INSERT INTO user_roles (user_id, role_id)
VALUES
    (@teacher_id, @teacher_role_id),
    (@student_id, @student_role_id),
    (@completed_student_id, @student_role_id),
    (@dropped_student_id, @student_role_id);

-- Published course for stable demo path.
INSERT INTO courses (teacher_id, title, subject, grade_level, description, status, join_code)
VALUES (
           @teacher_id,
           'Intro to Environmental Science',
           'Science',
           'HIGH_SCHOOL',
           'A high school environmental science course focused on ecosystems, biodiversity, climate, and sustainability.',
           'ACTIVE',
           'ECO2026'
       );

SET @course_id = LAST_INSERT_ID();

-- Roster states: active student for live quiz demo, plus completed/dropped students to make roster meaningful.
INSERT INTO course_enrollments (course_id, student_id, enrollment_status, enrolled_at, completed_at)
VALUES
    (@course_id, @student_id, 'ACTIVE', NOW() - INTERVAL 5 DAY, NULL),
    (@course_id, @completed_student_id, 'COMPLETED', NOW() - INTERVAL 14 DAY, NOW() - INTERVAL 2 DAY),
    (@course_id, @dropped_student_id, 'DROPPED', NOW() - INTERVAL 12 DAY, NULL);

-- Modules
INSERT INTO course_modules (course_id, title, description, module_order, status, published_at)
VALUES
    (@course_id, 'Module 1: Ecosystems & Biodiversity', 'How organisms interact with each other and their environment.', 1, 'PUBLISHED', NOW() - INTERVAL 4 DAY),
    (@course_id, 'Module 2: Climate, Energy & Human Impact', 'How energy choices and human activity shape environmental outcomes.', 2, 'PUBLISHED', NOW() - INTERVAL 3 DAY),
    (@course_id, 'Module 3: Sustainability Project', 'A draft capstone module for teacher review before publishing.', 3, 'DRAFT', NULL);

SET @module1_id = (SELECT id FROM course_modules WHERE course_id = @course_id AND module_order = 1);
SET @module2_id = (SELECT id FROM course_modules WHERE course_id = @course_id AND module_order = 2);
SET @module3_id = (SELECT id FROM course_modules WHERE course_id = @course_id AND module_order = 3);

-- Lessons
INSERT INTO lessons (module_id, title, content, lesson_order, estimated_minutes, status, published_at)
VALUES
    (
        @module1_id,
        'Lesson 1: What Makes an Ecosystem?',
        'An ecosystem includes living organisms, nonliving resources, and the relationships between them.\n\nKey ideas:\n- Producers capture energy, usually from sunlight.\n- Consumers transfer energy through food webs.\n- Decomposers recycle nutrients back into the system.\n\nMini-check: Pick one organism in your neighborhood and describe what it depends on to survive.',
        1,
        18,
        'PUBLISHED',
        NOW() - INTERVAL 4 DAY
    ),
    (
        @module1_id,
        'Lesson 2: Biodiversity as System Resilience',
        'Biodiversity measures variety across genes, species, and ecosystems. More diverse systems are often more resilient because different species can fill different ecological roles.\n\nExample: If one pollinator population drops, a diverse ecosystem may still have other pollinators that keep plants reproducing.',
        2,
        15,
        'PUBLISHED',
        NOW() - INTERVAL 4 DAY
    ),
    (
        @module2_id,
        'Lesson 1: Carbon, Energy, and Tradeoffs',
        'Energy decisions affect air quality, ecosystems, and climate. Renewable and nonrenewable energy sources each have tradeoffs, but the long-term environmental cost of fossil fuels is especially important when studying climate change.\n\nDiscussion prompt: What factors should a city consider before choosing a new energy source?',
        1,
        20,
        'PUBLISHED',
        NOW() - INTERVAL 3 DAY
    ),
    (
        @module3_id,
        'Draft Lesson: Local Sustainability Audit',
        'Students will evaluate water, energy, food, or waste patterns in their school/community and propose one realistic improvement.',
        1,
        25,
        'DRAFT',
        NULL
    );

-- Quizzes
INSERT INTO quizzes (module_id, title, description, quiz_order, max_points, time_limit_minutes, attempts_allowed, status, published_at)
VALUES
    (@module1_id, 'Ecosystems & Biodiversity Check', 'A short auto-graded quiz on ecosystems, biodiversity, and resilience.', 3, 3.00, 10, 1, 'PUBLISHED', NOW() - INTERVAL 4 DAY),
    (@module2_id, 'Climate & Energy Warmup', 'A quick check for the second module.', 2, 2.00, 8, 1, 'PUBLISHED', NOW() - INTERVAL 3 DAY);

SET @quiz1_id = (SELECT id FROM quizzes WHERE module_id = @module1_id AND quiz_order = 3);
SET @quiz2_id = (SELECT id FROM quizzes WHERE module_id = @module2_id AND quiz_order = 2);

-- Quiz 1 questions and options
INSERT INTO quiz_questions (quiz_id, question_text, question_type, question_order, points, explanation)
VALUES
    (@quiz1_id, 'Which example best shows a relationship inside an ecosystem?', 'MULTIPLE_CHOICE', 1, 1.00, 'Ecosystems are built from interactions among organisms and their environment.'),
    (@quiz1_id, 'True or false: A keystone species can have a large effect on an ecosystem even if it is not the most abundant species.', 'TRUE_FALSE', 2, 1.00, 'Keystone species can strongly shape ecosystem structure.'),
    (@quiz1_id, 'Which action would most directly support biodiversity?', 'MULTIPLE_CHOICE', 3, 1.00, 'Protecting connected habitats helps more species survive and move between areas.');

SET @q1_id = (SELECT id FROM quiz_questions WHERE quiz_id = @quiz1_id AND question_order = 1);
SET @q2_id = (SELECT id FROM quiz_questions WHERE quiz_id = @quiz1_id AND question_order = 2);
SET @q3_id = (SELECT id FROM quiz_questions WHERE quiz_id = @quiz1_id AND question_order = 3);

INSERT INTO quiz_answer_options (question_id, option_text, option_order, is_correct)
VALUES
    (@q1_id, 'Bees pollinating wildflowers', 1, TRUE),
    (@q1_id, 'A rock sitting in sunlight with no living organisms nearby', 2, FALSE),
    (@q1_id, 'A calculator solving an equation', 3, FALSE),
    (@q2_id, 'True', 1, TRUE),
    (@q2_id, 'False', 2, FALSE),
    (@q3_id, 'Protecting connected habitats', 1, TRUE),
    (@q3_id, 'Removing native plants for decorative pavement', 2, FALSE),
    (@q3_id, 'Introducing random non-native species', 3, FALSE);

-- Quiz 2 questions and options
INSERT INTO quiz_questions (quiz_id, question_text, question_type, question_order, points, explanation)
VALUES
    (@quiz2_id, 'True or false: Burning fossil fuels can increase carbon dioxide in the atmosphere.', 'TRUE_FALSE', 1, 1.00, 'Combustion releases carbon that can contribute to atmospheric CO2 levels.'),
    (@quiz2_id, 'Which source is renewable on a human time scale?', 'MULTIPLE_CHOICE', 2, 1.00, 'Solar energy is replenished continuously by sunlight.');

SET @q4_id = (SELECT id FROM quiz_questions WHERE quiz_id = @quiz2_id AND question_order = 1);
SET @q5_id = (SELECT id FROM quiz_questions WHERE quiz_id = @quiz2_id AND question_order = 2);

INSERT INTO quiz_answer_options (question_id, option_text, option_order, is_correct)
VALUES
    (@q4_id, 'True', 1, TRUE),
    (@q4_id, 'False', 2, FALSE),
    (@q5_id, 'Solar', 1, TRUE),
    (@q5_id, 'Coal', 2, FALSE),
    (@q5_id, 'Oil', 3, FALSE);

-- Pre-existing completed student's quiz result so the roster/course has realistic history.
INSERT INTO quiz_submissions (quiz_id, student_id, attempt_number, status, score, max_score, started_at, submitted_at, graded_at)
VALUES (@quiz1_id, @completed_student_id, 1, 'GRADED', 2.00, 3.00, NOW() - INTERVAL 2 DAY - INTERVAL 15 MINUTE, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY);

SET @completed_submission_id = LAST_INSERT_ID();
SET @q1_correct_option = (SELECT id FROM quiz_answer_options WHERE question_id = @q1_id AND option_order = 1);
SET @q2_correct_option = (SELECT id FROM quiz_answer_options WHERE question_id = @q2_id AND option_order = 1);
SET @q3_wrong_option = (SELECT id FROM quiz_answer_options WHERE question_id = @q3_id AND option_order = 2);

INSERT INTO quiz_submission_answers (quiz_submission_id, question_id, selected_option_id, short_answer_text, is_correct, points_earned)
VALUES
    (@completed_submission_id, @q1_id, @q1_correct_option, NULL, TRUE, 1.00),
    (@completed_submission_id, @q2_id, @q2_correct_option, NULL, TRUE, 1.00),
    (@completed_submission_id, @q3_id, @q3_wrong_option, NULL, FALSE, 0.00);

COMMIT;

-- Demo login reference:
-- Teacher: teacher.demo@example.com / Password123!
-- Student for live quiz: student.demo@example.com / Password123!
-- Join code: ECO2026
