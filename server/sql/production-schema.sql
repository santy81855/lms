-- LMS Database Initialization Script
-- Safe to run multiple times: creates the database and tables only if they do not already exist.

CREATE
DATABASE IF NOT EXISTS lms
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE
lms;

-- users table
CREATE TABLE IF NOT EXISTS users
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    first_name
    VARCHAR
(
    100
) NOT NULL,
    last_name VARCHAR
(
    100
) NOT NULL,

    email VARCHAR
(
    255
) NOT NULL UNIQUE,
    password_hash VARCHAR
(
    255
) NOT NULL,

    account_status ENUM
(
    'ACTIVE',
    'DISABLED',
    'PENDING'
) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- roles table allows user to have multiple roles
CREATE TABLE IF NOT EXISTS roles
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    name
    ENUM
(
    'STUDENT',
    'TEACHER',
    'ADMIN'
) NOT NULL UNIQUE,
    description VARCHAR
(
    255
),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );


-- Seed default platform roles.
-- INSERT IGNORE prevents duplicate role errors if this script is re-run.
INSERT
IGNORE INTO roles (name, description)
VALUES
    ('STUDENT', 'Can enroll in courses and complete lessons, quizzes, and assignments'),
    ('TEACHER', 'Can create courses, generate curriculum, publish content, and view student progress'),
    ('ADMIN', 'Can manage users, roles, and platform-level data');

-- table to connect users with roles since they are many to many
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id
    BIGINT
    NOT
    NULL,
    role_id
    BIGINT
    NOT
    NULL,

    assigned_at
    TIMESTAMP
    NOT
    NULL
    DEFAULT
    CURRENT_TIMESTAMP,

    PRIMARY
    KEY
(
    user_id,
    role_id
),
    CONSTRAINT fk_user_roles_user
    FOREIGN KEY
(
    user_id
)
    REFERENCES users
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
    FOREIGN KEY
(
    role_id
)
    REFERENCES roles
(
    id
)
    ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS courses
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    teacher_id
    BIGINT
    NOT
    NULL,

    title
    VARCHAR
(
    255
) NOT NULL,
    subject VARCHAR
(
    100
),
    grade_level ENUM
(
    'ELEMENTARY',
    'MIDDLE_SCHOOL',
    'HIGH_SCHOOL',
    'UNIVERSITY',
    'OTHER'
) NOT NULL DEFAULT 'OTHER',

    description TEXT,

    status ENUM
(
    'DRAFT',
    'ACTIVE',
    'ARCHIVED'
) NOT NULL DEFAULT 'DRAFT',
    -- think of making specific codes per person to better track who's joining
    join_code VARCHAR
(
    20
) NOT NULL UNIQUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_teacher
    FOREIGN KEY
(
    teacher_id
)
    REFERENCES users
(
    id
)
                                                            ON DELETE RESTRICT
    );

-- connect students to courses (students and courses are many to many)
-- take a better look at delete cascade vs other options
CREATE TABLE IF NOT EXISTS course_enrollments
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    course_id
    BIGINT
    NOT
    NULL,
    student_id
    BIGINT
    NOT
    NULL,

    enrollment_status
    ENUM
(
    'ACTIVE',
    'DROPPED',
    'COMPLETED'
) NOT NULL DEFAULT 'ACTIVE',

    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    CONSTRAINT fk_course_enrollments_course
    FOREIGN KEY
(
    course_id
)
    REFERENCES courses
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_course_enrollments_student
    FOREIGN KEY
(
    student_id
)
    REFERENCES users
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT uq_course_student
    UNIQUE
(
    course_id,
    student_id
)
    );

-- represents a group of contetn
CREATE TABLE IF NOT EXISTS course_modules
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    course_id
    BIGINT
    NOT
    NULL,

    title
    VARCHAR
(
    255
) NOT NULL,
    description TEXT,

    module_order INT NOT NULL,

    status ENUM
(
    'DRAFT',
    'PUBLISHED',
    'ARCHIVED'
) NOT NULL DEFAULT 'DRAFT',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    CONSTRAINT fk_course_modules_course
    FOREIGN KEY
(
    course_id
)
    REFERENCES courses
(
    id
)
                                                            ON DELETE CASCADE,
    CONSTRAINT uq_course_module_order
    UNIQUE
(
    course_id,
    module_order
)
    );

-- instructional content within a module
CREATE TABLE IF NOT EXISTS lessons
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    module_id
    BIGINT
    NOT
    NULL,

    title
    VARCHAR
(
    255
) NOT NULL,
    content LONGTEXT,

    lesson_order INT NOT NULL,

    estimated_minutes INT,

    status ENUM
(
    'DRAFT',
    'PUBLISHED',
    'ARCHIVED'
) NOT NULL DEFAULT 'DRAFT',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    CONSTRAINT fk_lessons_module
    FOREIGN KEY
(
    module_id
)
    REFERENCES course_modules
(
    id
)
                                                            ON DELETE CASCADE,
    CONSTRAINT uq_module_lesson_order
    UNIQUE
(
    module_id,
    lesson_order
)
    );

-- assignments refer to a part of a module where the user would submit text/link/file
CREATE TABLE IF NOT EXISTS assignments
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    module_id
    BIGINT
    NOT
    NULL,

    title
    VARCHAR
(
    255
) NOT NULL,
    instructions LONGTEXT,

    assignment_order INT NOT NULL,

    due_at TIMESTAMP NULL,

    max_points DECIMAL
(
    6,
    2
) NOT NULL DEFAULT 100.00,

    submission_type ENUM
(
    'TEXT',
    'FILE',
    'LINK'
) NOT NULL DEFAULT 'TEXT',

    status ENUM
(
    'DRAFT',
    'PUBLISHED',
    'ARCHIVED'
) NOT NULL DEFAULT 'DRAFT',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    CONSTRAINT fk_assignments_module
    FOREIGN KEY
(
    module_id
)
    REFERENCES course_modules
(
    id
)
                                                            ON DELETE CASCADE,
    CONSTRAINT uq_module_assignment_order
    UNIQUE
(
    module_id,
    assignment_order
)
    );

-- quizzes are self explanatory
CREATE TABLE IF NOT EXISTS quizzes
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    module_id
    BIGINT
    NOT
    NULL,

    title
    VARCHAR
(
    255
) NOT NULL,
    description TEXT,

    quiz_order INT NOT NULL,

    max_points DECIMAL
(
    6,
    2
) NOT NULL DEFAULT 100.00,

    time_limit_minutes INT NULL,

    attempts_allowed INT NOT NULL DEFAULT 1,

    status ENUM
(
    'DRAFT',
    'PUBLISHED',
    'ARCHIVED'
) NOT NULL DEFAULT 'DRAFT',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    CONSTRAINT fk_quizzes_module
    FOREIGN KEY
(
    module_id
)
    REFERENCES course_modules
(
    id
)
                                                            ON DELETE CASCADE,
    CONSTRAINT uq_module_quiz_order
    UNIQUE
(
    module_id,
    quiz_order
)
    );

-- the actual quiz questions
CREATE TABLE IF NOT EXISTS quiz_questions
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    quiz_id
    BIGINT
    NOT
    NULL,

    question_text
    LONGTEXT
    NOT
    NULL,

    question_type
    ENUM
(
    'MULTIPLE_CHOICE',
    'TRUE_FALSE',
    'SHORT_ANSWER'
) NOT NULL DEFAULT 'MULTIPLE_CHOICE',

    question_order INT NOT NULL,

    points DECIMAL
(
    6,
    2
) NOT NULL DEFAULT 1.00,

    explanation LONGTEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_questions_quiz
    FOREIGN KEY
(
    quiz_id
)
    REFERENCES quizzes
(
    id
)
                                                            ON DELETE CASCADE,
    CONSTRAINT uq_quiz_question_order
    UNIQUE
(
    quiz_id,
    question_order
)
    );

-- the actual multiple choice or true or false options to the questions
CREATE TABLE IF NOT EXISTS quiz_answer_options
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    question_id
    BIGINT
    NOT
    NULL,

    option_text
    LONGTEXT
    NOT
    NULL,

    option_order
    INT
    NOT
    NULL,

    is_correct
    BOOLEAN
    NOT
    NULL
    DEFAULT
    FALSE,

    created_at
    TIMESTAMP
    NOT
    NULL
    DEFAULT
    CURRENT_TIMESTAMP,
    updated_at
    TIMESTAMP
    NOT
    NULL
    DEFAULT
    CURRENT_TIMESTAMP
    ON
    UPDATE
    CURRENT_TIMESTAMP,

    CONSTRAINT
    fk_quiz_answer_options_question
    FOREIGN
    KEY
(
    question_id
)
    REFERENCES quiz_questions
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT uq_question_option_order
    UNIQUE
(
    question_id,
    option_order
)
    );

-- represents a student's submission
CREATE TABLE IF NOT EXISTS quiz_submissions
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    quiz_id
    BIGINT
    NOT
    NULL,
    student_id
    BIGINT
    NOT
    NULL,

    attempt_number
    INT
    NOT
    NULL
    DEFAULT
    1,

    status
    ENUM
(
    'IN_PROGRESS',
    'SUBMITTED',
    'GRADED'
) NOT NULL DEFAULT 'SUBMITTED',

    score DECIMAL
(
    6,
    2
) NULL,
    max_score DECIMAL
(
    6,
    2
) NOT NULL,

    started_at TIMESTAMP NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    graded_at TIMESTAMP NULL,
    CONSTRAINT fk_quiz_submissions_quiz
    FOREIGN KEY
(
    quiz_id
)
    REFERENCES quizzes
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_quiz_submissions_student
    FOREIGN KEY
(
    student_id
)
    REFERENCES users
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT uq_quiz_student_attempt
    UNIQUE
(
    quiz_id,
    student_id,
    attempt_number
)
    );

-- stores the student's answers to be able to show what they got right or wrong
CREATE TABLE IF NOT EXISTS quiz_submission_answers
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    quiz_submission_id
    BIGINT
    NOT
    NULL,
    question_id
    BIGINT
    NOT
    NULL,

    selected_option_id
    BIGINT
    NULL,
    short_answer_text
    LONGTEXT
    NULL,

    is_correct
    BOOLEAN
    NULL,
    points_earned
    DECIMAL
(
    6,
    2
) NOT NULL DEFAULT 0.00,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_quiz_submission_answers_submission
    FOREIGN KEY
(
    quiz_submission_id
)
    REFERENCES quiz_submissions
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_quiz_submission_answers_question
    FOREIGN KEY
(
    question_id
)
    REFERENCES quiz_questions
(
    id
)
    ON DELETE RESTRICT,
    CONSTRAINT fk_quiz_submission_answers_selected_option
    FOREIGN KEY
(
    selected_option_id
)
    REFERENCES quiz_answer_options
(
    id
)
    ON DELETE RESTRICT,
    CONSTRAINT uq_submission_question
    UNIQUE
(
    quiz_submission_id,
    question_id
)
    );

-- user submissions to assignments
CREATE TABLE IF NOT EXISTS assignment_submissions
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    assignment_id
    BIGINT
    NOT
    NULL,
    student_id
    BIGINT
    NOT
    NULL,

    response_text
    LONGTEXT
    NULL,
    file_url
    VARCHAR
(
    500
) NULL,
    link_url VARCHAR
(
    500
) NULL,

    status ENUM
(
    'SUBMITTED',
    'LATE',
    'GRADED',
    'RETURNED'
) NOT NULL DEFAULT 'SUBMITTED',

    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    graded_at TIMESTAMP NULL,

    score DECIMAL
(
    6,
    2
) NULL,
    feedback LONGTEXT NULL,
    CONSTRAINT fk_assignment_submissions_assignment
    FOREIGN KEY
(
    assignment_id
)
    REFERENCES assignments
(
    id
)
                                                            ON DELETE CASCADE,
    CONSTRAINT fk_assignment_submissions_student
    FOREIGN KEY
(
    student_id
)
    REFERENCES users
(
    id
)
                                                            ON DELETE CASCADE,
    CONSTRAINT uq_assignment_student
    UNIQUE
(
    assignment_id,
    student_id
)
    );

-- syllabus content upload
CREATE TABLE IF NOT EXISTS syllabus_uploads
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    course_id
    BIGINT
    NOT
    NULL,
    teacher_id
    BIGINT
    NOT
    NULL,

    title
    VARCHAR
(
    255
) NULL,

    input_type ENUM
(
    'TEXT',
    'FILE'
) NOT NULL DEFAULT 'TEXT',

    syllabus_text LONGTEXT NULL,
    original_file_name VARCHAR
(
    255
) NULL,
    file_url VARCHAR
(
    500
) NULL,

    processing_status ENUM
(
    'PENDING',
    'PROCESSING',
    'COMPLETED',
    'FAILED'
) NOT NULL DEFAULT 'PENDING',

    error_message TEXT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    CONSTRAINT fk_syllabus_uploads_course
    FOREIGN KEY
(
    course_id
)
    REFERENCES courses
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_syllabus_uploads_teacher
    FOREIGN KEY
(
    teacher_id
)
    REFERENCES users
(
    id
)
    ON DELETE RESTRICT
    );

-- this keeps ai generated content separate until the teacher accepts it, then it can be turned into modules
CREATE TABLE IF NOT EXISTS curriculum_drafts
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    course_id
    BIGINT
    NOT
    NULL,
    syllabus_upload_id
    BIGINT
    NOT
    NULL,
    teacher_id
    BIGINT
    NOT
    NULL,

    title
    VARCHAR
(
    255
) NULL,

    generation_status ENUM
(
    'PENDING',
    'GENERATING',
    'COMPLETED',
    'FAILED'
) NOT NULL DEFAULT 'PENDING',

    ai_model VARCHAR
(
    100
) NULL,

    teacher_notes TEXT NULL,
    error_message TEXT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,

    generated_content_json JSON NULL,
    accepted_at TIMESTAMP NULL,
    CONSTRAINT fk_curriculum_drafts_course
    FOREIGN KEY
(
    course_id
)
    REFERENCES courses
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_curriculum_drafts_syllabus_upload
    FOREIGN KEY
(
    syllabus_upload_id
)
    REFERENCES syllabus_uploads
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_curriculum_drafts_teacher
    FOREIGN KEY
(
    teacher_id
)
    REFERENCES users
(
    id
)
    ON DELETE RESTRICT
    );

-- store logs for easier debugging
CREATE TABLE IF NOT EXISTS activity_logs
(
    id
    BIGINT
    AUTO_INCREMENT
    PRIMARY
    KEY,

    actor_user_id
    BIGINT
    NULL,

    course_id
    BIGINT
    NULL,
    module_id
    BIGINT
    NULL,

    entity_type
    ENUM
(
    'USER',
    'COURSE',
    'MODULE',
    'LESSON',
    'ASSIGNMENT',
    'QUIZ',
    'QUIZ_SUBMISSION',
    'ASSIGNMENT_SUBMISSION',
    'SYLLABUS_UPLOAD',
    'CURRICULUM_DRAFT'
) NOT NULL,

    entity_id BIGINT NOT NULL,

    action_type ENUM
(
    'CREATED',
    'UPDATED',
    'DELETED',
    'PUBLISHED',
    'UNPUBLISHED',
    'ARCHIVED',
    'ENROLLED',
    'SUBMITTED',
    'GRADED',
    'GENERATED',
    'FAILED'
) NOT NULL,

    description TEXT NOT NULL,

    metadata JSON NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_logs_actor
    FOREIGN KEY
(
    actor_user_id
)
    REFERENCES users
(
    id
)
    ON DELETE SET NULL,
    CONSTRAINT fk_activity_logs_course
    FOREIGN KEY
(
    course_id
)
    REFERENCES courses
(
    id
)
    ON DELETE CASCADE,
    CONSTRAINT fk_activity_logs_module
    FOREIGN KEY
(
    module_id
)
    REFERENCES course_modules
(
    id
)
    ON DELETE SET NULL
    );

    ALTER TABLE quiz_submission_answers
ADD COLUMN is_graded BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE quiz_submission_answers
MODIFY COLUMN points_earned DECIMAL(10,2) NULL;

ALTER TABLE quiz_submission_answers
MODIFY COLUMN is_correct BOOLEAN NULL;

ALTER TABLE quiz_submission_answers
ADD COLUMN graded_at DATETIME NULL;

ALTER TABLE quiz_submission_answers
ADD COLUMN graded_by BIGINT NULL;


DESCRIBE quiz_submission_answers;