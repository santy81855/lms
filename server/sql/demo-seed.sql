-- Abundant LMS demo seed data
-- Run after server/sql/production-schema.sql has created the schema.
-- This script targets a local MySQL database named `lms`.
-- It deletes and recreates only the demo users/courses listed below.

USE lms;

-- ---------------------------------------------------------------------------
-- Demo cleanup
-- ---------------------------------------------------------------------------
-- MySQL cannot reopen the same TEMPORARY table multiple times inside one query,
-- so this cleanup intentionally avoids temporary ID tables. It deletes demo data
-- by joining back to the seeded demo course join codes and seeded demo user emails.

SET FOREIGN_KEY_CHECKS = 0;

-- Child quiz answers connected to demo quiz submissions, demo quiz questions,
-- or demo answer options.
DELETE qsa
FROM quiz_submission_answers qsa
LEFT JOIN quiz_submissions qs
    ON qsa.quiz_submission_id = qs.id
LEFT JOIN quizzes q_from_submission
    ON qs.quiz_id = q_from_submission.id
LEFT JOIN course_modules cm_from_submission
    ON q_from_submission.module_id = cm_from_submission.id
LEFT JOIN courses c_from_submission
    ON cm_from_submission.course_id = c_from_submission.id
LEFT JOIN users u_from_submission
    ON qs.student_id = u_from_submission.id
LEFT JOIN quiz_questions qq_from_answer
    ON qsa.question_id = qq_from_answer.id
LEFT JOIN quizzes q_from_answer
    ON qq_from_answer.quiz_id = q_from_answer.id
LEFT JOIN course_modules cm_from_answer
    ON q_from_answer.module_id = cm_from_answer.id
LEFT JOIN courses c_from_answer
    ON cm_from_answer.course_id = c_from_answer.id
LEFT JOIN quiz_answer_options qao_from_selected
    ON qsa.selected_option_id = qao_from_selected.id
LEFT JOIN quiz_questions qq_from_selected
    ON qao_from_selected.question_id = qq_from_selected.id
LEFT JOIN quizzes q_from_selected
    ON qq_from_selected.quiz_id = q_from_selected.id
LEFT JOIN course_modules cm_from_selected
    ON q_from_selected.module_id = cm_from_selected.id
LEFT JOIN courses c_from_selected
    ON cm_from_selected.course_id = c_from_selected.id
WHERE c_from_submission.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25')
   OR c_from_answer.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25')
   OR c_from_selected.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25')
   OR u_from_submission.email IN (
        'teacher.demo@example.com',
        'student.demo@example.com',
        'alex.chen@example.com',
        'priya.patel@example.com',
        'jordan.lee@example.com',
        'samir.khan@example.com',
        'lina.morales@example.com',
        'noah.brooks@example.com',
        'emma.wilson@example.com',
        'diego.ramirez@example.com',
        'harper.nguyen@example.com',
        'mia.johnson@example.com',
        'olivia.martin@example.com',
        'ethan.clark@example.com',
        'ava.thompson@example.com',
        'lucas.young@example.com'
   );

DELETE qs
FROM quiz_submissions qs
LEFT JOIN quizzes q
    ON qs.quiz_id = q.id
LEFT JOIN course_modules cm
    ON q.module_id = cm.id
LEFT JOIN courses c
    ON cm.course_id = c.id
LEFT JOIN users u
    ON qs.student_id = u.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25')
   OR u.email IN (
        'teacher.demo@example.com',
        'student.demo@example.com',
        'alex.chen@example.com',
        'priya.patel@example.com',
        'jordan.lee@example.com',
        'samir.khan@example.com',
        'lina.morales@example.com',
        'noah.brooks@example.com',
        'emma.wilson@example.com',
        'diego.ramirez@example.com',
        'harper.nguyen@example.com',
        'mia.johnson@example.com',
        'olivia.martin@example.com',
        'ethan.clark@example.com',
        'ava.thompson@example.com',
        'lucas.young@example.com'
   );

DELETE qao
FROM quiz_answer_options qao
JOIN quiz_questions qq
    ON qao.question_id = qq.id
JOIN quizzes q
    ON qq.quiz_id = q.id
JOIN course_modules cm
    ON q.module_id = cm.id
JOIN courses c
    ON cm.course_id = c.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25');

DELETE qq
FROM quiz_questions qq
JOIN quizzes q
    ON qq.quiz_id = q.id
JOIN course_modules cm
    ON q.module_id = cm.id
JOIN courses c
    ON cm.course_id = c.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25');

DELETE q
FROM quizzes q
JOIN course_modules cm
    ON q.module_id = cm.id
JOIN courses c
    ON cm.course_id = c.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25');

DELETE aps
FROM assignment_submissions aps
LEFT JOIN assignments a
    ON aps.assignment_id = a.id
LEFT JOIN course_modules cm
    ON a.module_id = cm.id
LEFT JOIN courses c
    ON cm.course_id = c.id
LEFT JOIN users u
    ON aps.student_id = u.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25')
   OR u.email IN (
        'teacher.demo@example.com',
        'student.demo@example.com',
        'alex.chen@example.com',
        'priya.patel@example.com',
        'jordan.lee@example.com',
        'samir.khan@example.com',
        'lina.morales@example.com',
        'noah.brooks@example.com',
        'emma.wilson@example.com',
        'diego.ramirez@example.com',
        'harper.nguyen@example.com',
        'mia.johnson@example.com',
        'olivia.martin@example.com',
        'ethan.clark@example.com',
        'ava.thompson@example.com',
        'lucas.young@example.com'
   );

DELETE a
FROM assignments a
JOIN course_modules cm
    ON a.module_id = cm.id
JOIN courses c
    ON cm.course_id = c.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25');

DELETE l
FROM lessons l
JOIN course_modules cm
    ON l.module_id = cm.id
JOIN courses c
    ON cm.course_id = c.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25');

DELETE cm
FROM course_modules cm
JOIN courses c
    ON cm.course_id = c.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25');

DELETE ce
FROM course_enrollments ce
LEFT JOIN courses c
    ON ce.course_id = c.id
LEFT JOIN users u
    ON ce.student_id = u.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25')
   OR u.email IN (
        'teacher.demo@example.com',
        'student.demo@example.com',
        'alex.chen@example.com',
        'priya.patel@example.com',
        'jordan.lee@example.com',
        'samir.khan@example.com',
        'lina.morales@example.com',
        'noah.brooks@example.com',
        'emma.wilson@example.com',
        'diego.ramirez@example.com',
        'harper.nguyen@example.com',
        'mia.johnson@example.com',
        'olivia.martin@example.com',
        'ethan.clark@example.com',
        'ava.thompson@example.com',
        'lucas.young@example.com'
   );

DELETE al
FROM activity_logs al
LEFT JOIN courses c
    ON al.course_id = c.id
LEFT JOIN users u
    ON al.actor_user_id = u.id
WHERE c.join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25')
   OR u.email IN (
        'teacher.demo@example.com',
        'student.demo@example.com',
        'alex.chen@example.com',
        'priya.patel@example.com',
        'jordan.lee@example.com',
        'samir.khan@example.com',
        'lina.morales@example.com',
        'noah.brooks@example.com',
        'emma.wilson@example.com',
        'diego.ramirez@example.com',
        'harper.nguyen@example.com',
        'mia.johnson@example.com',
        'olivia.martin@example.com',
        'ethan.clark@example.com',
        'ava.thompson@example.com',
        'lucas.young@example.com'
   );

DELETE ur
FROM user_roles ur
JOIN users u
    ON ur.user_id = u.id
WHERE u.email IN (
    'teacher.demo@example.com',
    'student.demo@example.com',
    'alex.chen@example.com',
    'priya.patel@example.com',
    'jordan.lee@example.com',
    'samir.khan@example.com',
    'lina.morales@example.com',
    'noah.brooks@example.com',
    'emma.wilson@example.com',
    'diego.ramirez@example.com',
    'harper.nguyen@example.com',
    'mia.johnson@example.com',
    'olivia.martin@example.com',
    'ethan.clark@example.com',
    'ava.thompson@example.com',
    'lucas.young@example.com'
);

DELETE FROM courses
WHERE join_code IN ('ECO2026', 'CSDEMO26', 'HIST2026', 'ALG2026', 'WRITE26', 'BIO2026', 'ARCH25');

DELETE FROM users
WHERE email IN (
                'teacher.demo@example.com',
                'student.demo@example.com',
                'alex.chen@example.com',
                'priya.patel@example.com',
                'jordan.lee@example.com',
                'samir.khan@example.com',
                'lina.morales@example.com',
                'noah.brooks@example.com',
                'emma.wilson@example.com',
                'diego.ramirez@example.com',
                'harper.nguyen@example.com',
                'mia.johnson@example.com',
                'olivia.martin@example.com',
                'ethan.clark@example.com',
                'ava.thompson@example.com',
                'lucas.young@example.com'
    );

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------------
-- Roles and users
-- ---------------------------------------------------------------------------

INSERT IGNORE INTO roles (name, description)
VALUES
    ('STUDENT', 'Can enroll in courses and complete lessons, quizzes, and assignments'),
    ('TEACHER', 'Can create courses, generate curriculum, publish content, and view student progress'),
    ('ADMIN', 'Can manage users, roles, and platform-level data');

-- Password for every seeded user: Password123!
-- The {noop} prefix works with Spring's DelegatingPasswordEncoder in local demo data.
INSERT INTO users (first_name, last_name, email, password_hash, account_status)
VALUES

    ('Sofia','Reed','teacher.demo@example.com','{noop}Password123!','ACTIVE'),
    ('Maya','Rivera','student.demo@example.com','{noop}Password123!','ACTIVE'),
    ('Alex','Chen','alex.chen@example.com','{noop}Password123!','ACTIVE'),
    ('Priya','Patel','priya.patel@example.com','{noop}Password123!','ACTIVE'),
    ('Jordan','Lee','jordan.lee@example.com','{noop}Password123!','ACTIVE'),
    ('Samir','Khan','samir.khan@example.com','{noop}Password123!','ACTIVE'),
    ('Lina','Morales','lina.morales@example.com','{noop}Password123!','ACTIVE'),
    ('Noah','Brooks','noah.brooks@example.com','{noop}Password123!','ACTIVE'),
    ('Emma','Wilson','emma.wilson@example.com','{noop}Password123!','ACTIVE'),
    ('Diego','Ramirez','diego.ramirez@example.com','{noop}Password123!','ACTIVE'),
    ('Harper','Nguyen','harper.nguyen@example.com','{noop}Password123!','ACTIVE'),
    ('Mia','Johnson','mia.johnson@example.com','{noop}Password123!','ACTIVE'),
    ('Olivia','Martin','olivia.martin@example.com','{noop}Password123!','ACTIVE'),
    ('Ethan','Clark','ethan.clark@example.com','{noop}Password123!','ACTIVE'),
    ('Ava','Thompson','ava.thompson@example.com','{noop}Password123!','ACTIVE'),
    ('Lucas','Young','lucas.young@example.com','{noop}Password123!','ACTIVE');


SET @teacher_id = (SELECT id FROM users WHERE email = 'teacher.demo@example.com');

INSERT INTO user_roles (user_id, role_id)
SELECT @teacher_id, id FROM roles WHERE name = 'TEACHER';

INSERT INTO user_roles (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE name = 'STUDENT')
FROM users
WHERE email IN (

                'student.demo@example.com',
                'alex.chen@example.com',
                'priya.patel@example.com',
                'jordan.lee@example.com',
                'samir.khan@example.com',
                'lina.morales@example.com',
                'noah.brooks@example.com',
                'emma.wilson@example.com',
                'diego.ramirez@example.com',
                'harper.nguyen@example.com',
                'mia.johnson@example.com',
                'olivia.martin@example.com',
                'ethan.clark@example.com',
                'ava.thompson@example.com',
                'lucas.young@example.com'

    );

-- ---------------------------------------------------------------------------
-- Courses
-- ---------------------------------------------------------------------------

INSERT INTO courses (teacher_id, title, subject, grade_level, description, status, join_code)
VALUES

    (@teacher_id,'Environmental Science: Ecosystems & Sustainability','Science','HIGH_SCHOOL','A polished demo course about ecosystems, biodiversity, natural resources, pollution, climate change, and sustainability.','ACTIVE','ECO2026'),
    (@teacher_id,'Intro to Computer Science with C','Computer Science','UNIVERSITY','A systems-minded introduction to programming, algorithms, memory, functions, arrays, strings, and file I/O using C.','ACTIVE','CSDEMO26'),
    (@teacher_id,'World History Foundations','History','HIGH_SCHOOL','A survey course covering early civilizations, trade networks, revolutions, and modern global connections.','ACTIVE','HIST2026'),
    (@teacher_id,'Algebra I Problem Solving','Math','MIDDLE_SCHOOL','A skills-focused algebra course with equations, inequalities, graphing, functions, and applied problem solving.','ACTIVE','ALG2026'),
    (@teacher_id,'Creative Writing Workshop','English','HIGH_SCHOOL','A draft course for narrative writing, poetry, revision, and peer feedback routines.','DRAFT','WRITE26'),
    (@teacher_id,'Biology Lab Skills','Science','HIGH_SCHOOL','A practical lab skills course focused on scientific method, microscopy, data collection, and lab safety.','ACTIVE','BIO2026'),
    (@teacher_id,'Archived Demo Course: Earth Systems 2025','Science','HIGH_SCHOOL','An older archived course included so the teacher dashboard looks realistic.','ARCHIVED','ARCH25');

SET @eco_course_id = (SELECT id FROM courses WHERE join_code = 'ECO2026');
SET @cs_course_id = (SELECT id FROM courses WHERE join_code = 'CSDEMO26');
SET @hist_course_id = (SELECT id FROM courses WHERE join_code = 'HIST2026');
SET @alg_course_id = (SELECT id FROM courses WHERE join_code = 'ALG2026');
SET @write_course_id = (SELECT id FROM courses WHERE join_code = 'WRITE26');
SET @bio_course_id = (SELECT id FROM courses WHERE join_code = 'BIO2026');
SET @arch_course_id = (SELECT id FROM courses WHERE join_code = 'ARCH25');


-- ---------------------------------------------------------------------------
-- Main demo course: Environmental Science
-- ---------------------------------------------------------------------------

INSERT INTO course_modules (course_id, title, description, module_order, status, published_at)
VALUES
    (@eco_course_id,'Module 1: Ecosystems and Energy Flow','Food webs, trophic levels, energy transfer, and ecosystem stability.',1,'PUBLISHED',NOW() - INTERVAL 20 DAY),
    (@eco_course_id,'Module 2: Biodiversity and Adaptation','Biodiversity, niches, adaptation, invasive species, and ecosystem resilience.',2,'PUBLISHED',NOW() - INTERVAL 18 DAY),
    (@eco_course_id,'Module 3: Natural Resources and Sustainability','Renewable and nonrenewable resources, conservation, and sustainability tradeoffs.',3,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@eco_course_id,'Module 4: Pollution and Climate Change','Pollution sources, climate evidence, mitigation, and adaptation strategies.',4,'PUBLISHED',NOW() - INTERVAL 14 DAY),
    (@eco_course_id,'Module 5: Community Action Project','A draft capstone module where students design a local sustainability action plan.',5,'DRAFT',NULL);

SET @eco_m1 = (SELECT id FROM course_modules WHERE course_id = @eco_course_id AND module_order = 1);
SET @eco_m2 = (SELECT id FROM course_modules WHERE course_id = @eco_course_id AND module_order = 2);
SET @eco_m3 = (SELECT id FROM course_modules WHERE course_id = @eco_course_id AND module_order = 3);
SET @eco_m4 = (SELECT id FROM course_modules WHERE course_id = @eco_course_id AND module_order = 4);
SET @eco_m5 = (SELECT id FROM course_modules WHERE course_id = @eco_course_id AND module_order = 5);

INSERT INTO lessons (module_id, title, content, lesson_order, estimated_minutes, status, published_at)
VALUES
    (@eco_m1,'What Makes an Ecosystem?','Students identify producers, consumers, decomposers, abiotic factors, and interactions in a pond ecosystem.',1,20,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@eco_m1,'Food Chains, Food Webs, and Energy Pyramids','Students compare food chains and food webs, then explain why energy decreases at higher trophic levels.',2,25,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@eco_m1,'Keystone Species and Ecosystem Balance','Students analyze sea otters and kelp forests as a case study of trophic cascades.',3,30,'PUBLISHED',NOW() - INTERVAL 15 DAY),
    (@eco_m2,'Biodiversity at Three Levels','Students learn genetic diversity, species diversity, and ecosystem diversity, then connect biodiversity to resilience.',1,25,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@eco_m2,'Adaptations and Ecological Niches','Students connect structural, behavioral, and physiological adaptations to ecological niches.',2,30,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@eco_m2,'Invasive Species Case Study','Students study how invasive species spread and why prevention is often more effective than removal.',3,30,'PUBLISHED',NOW() - INTERVAL 15 DAY),
    (@eco_m3,'Renewable vs. Nonrenewable Resources','Students classify resources and evaluate examples such as solar, wind, timber, freshwater, and fossil fuels.',1,25,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@eco_m3,'Sustainable Design Decisions','Students use a decision matrix to compare environmental, economic, and social tradeoffs.',2,30,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@eco_m3,'Water Use and Conservation','Students investigate agricultural, industrial, and household water use and evaluate conservation strategies.',3,25,'PUBLISHED',NOW() - INTERVAL 15 DAY),
    (@eco_m4,'Air and Water Pollution','Students distinguish point-source and nonpoint-source pollution and trace pollutants through systems.',1,25,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@eco_m4,'Evidence for Climate Change','Students interpret evidence from temperature records, ice cores, sea level data, and phenology.',2,35,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@eco_m4,'Mitigation and Adaptation Strategies','Students compare strategies that reduce emissions with strategies that reduce harm.',3,30,'PUBLISHED',NOW() - INTERVAL 15 DAY),
    (@eco_m5,'Choosing a Local Environmental Issue','Draft lesson: students select a local issue and define a problem statement with stakeholders.',1,25,'DRAFT',NULL),
    (@eco_m5,'Designing an Action Plan','Draft lesson: students propose a realistic action plan with goals, constraints, evidence, and communication strategy.',2,30,'DRAFT',NULL);

INSERT INTO quizzes (module_id, title, description, quiz_order, max_points, time_limit_minutes, attempts_allowed, status, published_at)
VALUES
    (@eco_m1,'Ecosystems and Energy Flow Quiz','Checks ecosystems, food webs, energy transfer, and keystone species.',4,3.00,20,3,'PUBLISHED',NOW() - INTERVAL 6 DAY),
    (@eco_m2,'Biodiversity and Adaptation Quiz','Checks biodiversity, adaptation, niches, and invasive species.',4,3.00,20,2,'PUBLISHED',NOW() - INTERVAL 6 DAY),
    (@eco_m3,'Sustainability Decisions Quiz','Checks resources, conservation, and sustainability tradeoffs.',4,3.00,20,2,'PUBLISHED',NOW() - INTERVAL 6 DAY),
    (@eco_m4,'Pollution and Climate Change Quiz','Checks pollution types, climate evidence, mitigation, and adaptation.',4,3.00,20,2,'PUBLISHED',NOW() - INTERVAL 6 DAY),
    (@eco_m5,'Community Action Planning Quiz','Draft quiz for the capstone project.',3,2.00,15,1,'DRAFT',NULL);


SET @eco_q1 = (SELECT id FROM quizzes WHERE module_id = @eco_m1 AND quiz_order = 4);
SET @eco_q2 = (SELECT id FROM quizzes WHERE module_id = @eco_m2 AND quiz_order = 4);
SET @eco_q3 = (SELECT id FROM quizzes WHERE module_id = @eco_m3 AND quiz_order = 4);
SET @eco_q4 = (SELECT id FROM quizzes WHERE module_id = @eco_m4 AND quiz_order = 4);

INSERT INTO quiz_questions (quiz_id, question_text, question_type, question_order, points, explanation)
VALUES
    (@eco_q1,'Which organism group makes its own food and forms the base of many food webs?','MULTIPLE_CHOICE',1,1.00,'Producers such as plants and algae use energy from the sun to make food.'),
    (@eco_q1,'True or false: Energy generally decreases as it moves up an energy pyramid.','TRUE_FALSE',2,1.00,'Only a portion of energy transfers to the next trophic level.'),
    (@eco_q1,'What can happen when a keystone species is removed from an ecosystem?','MULTIPLE_CHOICE',3,1.00,'Removing a keystone species can trigger major ecosystem changes.'),
    (@eco_q2,'Which term means the variety of species in an ecosystem?','MULTIPLE_CHOICE',1,1.00,'Species diversity describes the variety of species present.'),
    (@eco_q2,'True or false: Invasive species can disrupt native ecosystems.','TRUE_FALSE',2,1.00,'Invasive species can outcompete native species.'),
    (@eco_q2,'What is an ecological niche?','MULTIPLE_CHOICE',3,1.00,'A niche is the role a species plays in its ecosystem.'),
    (@eco_q3,'Which resource is renewable when managed carefully?','MULTIPLE_CHOICE',1,1.00,'Forests can be renewable if harvesting and regrowth are balanced.'),
    (@eco_q3,'True or false: Sustainability decisions can involve tradeoffs.','TRUE_FALSE',2,1.00,'Sustainability often balances environmental, economic, and social needs.'),
    (@eco_q3,'Which practice is commonly used to conserve water in agriculture?','MULTIPLE_CHOICE',3,1.00,'Drip irrigation reduces water loss compared with many traditional methods.'),
    (@eco_q4,'Which example is nonpoint-source pollution?','MULTIPLE_CHOICE',1,1.00,'Runoff from many lawns is a nonpoint source.'),
    (@eco_q4,'True or false: Ice core data can provide evidence about past climates.','TRUE_FALSE',2,1.00,'Ice cores preserve information about past atmospheric conditions.'),
    (@eco_q4,'Which action is an example of climate mitigation?','MULTIPLE_CHOICE',3,1.00,'Reducing greenhouse gas emissions is mitigation.');

-- Options for the main live-demo quiz.
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q1 AND question_order = 1);
INSERT INTO quiz_answer_options (question_id, option_text, option_order, is_correct) VALUES
                                                                                         (@q,'Producer',1,TRUE),(@q,'Primary consumer',2,FALSE),(@q,'Secondary consumer',3,FALSE),(@q,'Decomposer only',4,FALSE);
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q1 AND question_order = 2);
INSERT INTO quiz_answer_options (question_id, option_text, option_order, is_correct) VALUES
                                                                                         (@q,'True',1,TRUE),(@q,'False',2,FALSE);
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q1 AND question_order = 3);
INSERT INTO quiz_answer_options (question_id, option_text, option_order, is_correct) VALUES
                                                                                         (@q,'The ecosystem may experience a trophic cascade',1,TRUE),(@q,'All species become producers',2,FALSE),(@q,'Energy transfer stops completely',3,FALSE),(@q,'Abiotic factors disappear',4,FALSE);

-- Generic option sets for the rest of the Environmental Science quizzes.
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q2 AND question_order = 1);
INSERT INTO quiz_answer_options VALUES
                                    (NULL,@q,'Species diversity',1,TRUE,NOW(),NOW()),(NULL,@q,'Uniformity',2,FALSE,NOW(),NOW()),(NULL,@q,'Desertification',3,FALSE,NOW(),NOW()),(NULL,@q,'Condensation',4,FALSE,NOW(),NOW());
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q2 AND question_order = 2);
INSERT INTO quiz_answer_options VALUES (NULL,@q,'True',1,TRUE,NOW(),NOW()),(NULL,@q,'False',2,FALSE,NOW(),NOW());
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q2 AND question_order = 3);
INSERT INTO quiz_answer_options VALUES
                                    (NULL,@q,'The role a species plays in its ecosystem',1,TRUE,NOW(),NOW()),(NULL,@q,'A weather pattern',2,FALSE,NOW(),NOW()),(NULL,@q,'A type of rock',3,FALSE,NOW(),NOW()),(NULL,@q,'A laboratory tool',4,FALSE,NOW(),NOW());

SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q3 AND question_order = 1);
INSERT INTO quiz_answer_options VALUES
                                    (NULL,@q,'A forest managed for regrowth',1,TRUE,NOW(),NOW()),(NULL,@q,'Coal burned faster than it forms',2,FALSE,NOW(),NOW()),(NULL,@q,'A single-use plastic bottle',3,FALSE,NOW(),NOW()),(NULL,@q,'Crude oil',4,FALSE,NOW(),NOW());
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q3 AND question_order = 2);
INSERT INTO quiz_answer_options VALUES (NULL,@q,'True',1,TRUE,NOW(),NOW()),(NULL,@q,'False',2,FALSE,NOW(),NOW());
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q3 AND question_order = 3);
INSERT INTO quiz_answer_options VALUES
                                    (NULL,@q,'Drip irrigation',1,TRUE,NOW(),NOW()),(NULL,@q,'Leaving hoses running',2,FALSE,NOW(),NOW()),(NULL,@q,'Removing all soil cover',3,FALSE,NOW(),NOW()),(NULL,@q,'Pumping unlimited groundwater',4,FALSE,NOW(),NOW());

SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q4 AND question_order = 1);
INSERT INTO quiz_answer_options VALUES
                                    (NULL,@q,'Fertilizer runoff from many lawns',1,TRUE,NOW(),NOW()),(NULL,@q,'A single pipe discharging waste',2,FALSE,NOW(),NOW()),(NULL,@q,'A controlled lab sample',3,FALSE,NOW(),NOW()),(NULL,@q,'A sealed water bottle',4,FALSE,NOW(),NOW());
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q4 AND question_order = 2);
INSERT INTO quiz_answer_options VALUES (NULL,@q,'True',1,TRUE,NOW(),NOW()),(NULL,@q,'False',2,FALSE,NOW(),NOW());
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @eco_q4 AND question_order = 3);
INSERT INTO quiz_answer_options VALUES
                                    (NULL,@q,'Reducing greenhouse gas emissions',1,TRUE,NOW(),NOW()),(NULL,@q,'Raising a seawall only',2,FALSE,NOW(),NOW()),(NULL,@q,'Ignoring flood risk',3,FALSE,NOW(),NOW()),(NULL,@q,'Using more fossil fuels',4,FALSE,NOW(),NOW());

-- ---------------------------------------------------------------------------
-- Extra courses for fuller navigation
-- ---------------------------------------------------------------------------

INSERT INTO course_modules (course_id, title, description, module_order, status, published_at)
VALUES
    (@cs_course_id,'Module 1: Compiling and Running C Programs','Source code, compilers, executables, main, printf, and command-line workflow.',1,'PUBLISHED',NOW() - INTERVAL 20 DAY),
    (@cs_course_id,'Module 2: Variables, Types, and Control Flow','Variables, types, operators, if statements, loops, and trace-based reasoning.',2,'PUBLISHED',NOW() - INTERVAL 18 DAY),
    (@cs_course_id,'Module 3: Functions, Arrays, and Strings','Program decomposition, reusable functions, arrays, strings, and indexing.',3,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@cs_course_id,'Module 4: Pointers, Structs, and Files','Memory addresses, pointers, structs, and file input/output.',4,'DRAFT',NULL);

SET @cs_m1 = (SELECT id FROM course_modules WHERE course_id = @cs_course_id AND module_order = 1);
SET @cs_m2 = (SELECT id FROM course_modules WHERE course_id = @cs_course_id AND module_order = 2);
SET @cs_m3 = (SELECT id FROM course_modules WHERE course_id = @cs_course_id AND module_order = 3);
SET @cs_m4 = (SELECT id FROM course_modules WHERE course_id = @cs_course_id AND module_order = 4);

INSERT INTO lessons (module_id, title, content, lesson_order, estimated_minutes, status, published_at)
VALUES
    (@cs_m1,'What Happens When C Code Runs?','Students follow a C program from source file to executable and learn preprocessing, compiling, linking, and running.',1,35,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@cs_m1,'The Shape of a Basic C Program','Students read and modify a small C program using #include, main, printf, comments, and return values.',2,40,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@cs_m2,'Variables, Types, and Expressions','Students declare variables, use int, double, and char, and predict arithmetic results.',1,45,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@cs_m2,'Control Flow with Decisions and Loops','Students use if statements, while loops, and for loops to build small command-line programs.',2,45,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@cs_m3,'Functions as Reusable Tools','Students write functions with parameters and return values, then refactor repeated logic.',1,45,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@cs_m3,'Arrays, Strings, and Indexing','Students use arrays and character strings, trace indexes, and explain null terminators.',2,50,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@cs_m4,'Pointers and Memory Addresses','Draft lesson: students connect variables to memory addresses and use pointers in functions.',1,50,'DRAFT',NULL),
    (@cs_m4,'Structs and File Input/Output','Draft lesson: students model records with structs and write a file-based summary program.',2,50,'DRAFT',NULL);

INSERT INTO quizzes (module_id, title, description, quiz_order, max_points, time_limit_minutes, attempts_allowed, status, published_at)
VALUES
    (@cs_m1,'C Program Basics Quiz','Checks compiling, main, printf, and basic syntax.',3,2.00,15,2,'PUBLISHED',NOW() - INTERVAL 7 DAY),
    (@cs_m2,'Variables and Control Flow Quiz','Checks types, expressions, branches, and loops.',3,2.00,15,2,'PUBLISHED',NOW() - INTERVAL 7 DAY),
    (@cs_m3,'Functions and Arrays Quiz','Checks functions, arrays, indexing, and strings.',3,2.00,15,2,'PUBLISHED',NOW() - INTERVAL 7 DAY);


SET @cs_q1 = (SELECT id FROM quizzes WHERE module_id = @cs_m1 AND quiz_order = 3);
INSERT INTO quiz_questions (quiz_id, question_text, question_type, question_order, points, explanation)
VALUES
    (@cs_q1,'Which function is the usual entry point of a C program?','MULTIPLE_CHOICE',1,1.00,'Execution usually begins in main.'),
    (@cs_q1,'True or false: A compiler can help identify syntax errors.','TRUE_FALSE',2,1.00,'Compiler diagnostics often point to syntax issues.');
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @cs_q1 AND question_order = 1);
INSERT INTO quiz_answer_options (question_id, option_text, option_order, is_correct)
VALUES (@q,'main',1,TRUE),(@q,'printf',2,FALSE),(@q,'scanf',3,FALSE),(@q,'return',4,FALSE);
SET @q = (SELECT id FROM quiz_questions WHERE quiz_id = @cs_q1 AND question_order = 2);
INSERT INTO quiz_answer_options (question_id, option_text, option_order, is_correct)
VALUES (@q,'True',1,TRUE),(@q,'False',2,FALSE);

INSERT INTO course_modules (course_id, title, description, module_order, status, published_at)
VALUES
    (@hist_course_id,'Module 1: Early Civilizations','River valleys, agriculture, government, writing, and social organization.',1,'PUBLISHED',NOW() - INTERVAL 20 DAY),
    (@hist_course_id,'Module 2: Trade and Cultural Exchange','Trade routes, diffusion, belief systems, and cross-cultural contact.',2,'PUBLISHED',NOW() - INTERVAL 18 DAY),
    (@hist_course_id,'Module 3: Revolutions and Modern States','Political revolutions, industrialization, nationalism, and reform movements.',3,'DRAFT',NULL);

SET @hist_m1 = (SELECT id FROM course_modules WHERE course_id = @hist_course_id AND module_order = 1);
SET @hist_m2 = (SELECT id FROM course_modules WHERE course_id = @hist_course_id AND module_order = 2);
SET @hist_m3 = (SELECT id FROM course_modules WHERE course_id = @hist_course_id AND module_order = 3);

INSERT INTO lessons (module_id, title, content, lesson_order, estimated_minutes, status, published_at)
VALUES
    (@hist_m1,'Why River Valleys Supported Early Civilizations','Students examine how water, fertile soil, transportation, and surplus food supported early cities.',1,30,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@hist_m1,'Writing, Law, and Centralized Power','Students compare writing systems and law codes as tools for organizing larger societies.',2,30,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@hist_m2,'The Silk Roads as a Network','Students analyze trade networks as systems for moving goods, technologies, religions, and ideas.',1,30,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@hist_m2,'Cultural Diffusion and Its Consequences','Students evaluate benefits and conflicts created by cultural exchange.',2,30,'PUBLISHED',NOW() - INTERVAL 16 DAY);

INSERT INTO course_modules (course_id, title, description, module_order, status, published_at)
VALUES
    (@alg_course_id,'Module 1: Equations and Inequalities','Solving one-step, two-step, and multi-step equations and inequalities.',1,'PUBLISHED',NOW() - INTERVAL 20 DAY),
    (@alg_course_id,'Module 2: Linear Relationships','Tables, graphs, slope, intercepts, and real-world linear models.',2,'PUBLISHED',NOW() - INTERVAL 18 DAY),
    (@alg_course_id,'Module 3: Systems of Equations','Solving systems by graphing, substitution, and elimination.',3,'DRAFT',NULL);

SET @alg_m1 = (SELECT id FROM course_modules WHERE course_id = @alg_course_id AND module_order = 1);
SET @alg_m2 = (SELECT id FROM course_modules WHERE course_id = @alg_course_id AND module_order = 2);
SET @alg_m3 = (SELECT id FROM course_modules WHERE course_id = @alg_course_id AND module_order = 3);

INSERT INTO lessons (module_id, title, content, lesson_order, estimated_minutes, status, published_at)
VALUES
    (@alg_m1,'Solving Equations by Maintaining Balance','Students solve equations by applying inverse operations to both sides.',1,35,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@alg_m1,'Inequalities and Number Lines','Students solve inequalities and represent solution sets on number lines.',2,35,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@alg_m2,'Slope as Rate of Change','Students interpret slope from graphs, tables, and word problems.',1,35,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@alg_m2,'Writing Linear Equations','Students write equations in slope-intercept form and connect each term to context.',2,35,'PUBLISHED',NOW() - INTERVAL 16 DAY);

INSERT INTO course_modules (course_id, title, description, module_order, status, published_at)
VALUES
    (@bio_course_id,'Module 1: Lab Safety and Scientific Method','Safety expectations, variables, controls, and experimental design.',1,'PUBLISHED',NOW() - INTERVAL 20 DAY),
    (@bio_course_id,'Module 2: Microscopy and Measurement','Microscope parts, slide preparation, magnification, and careful measurement.',2,'PUBLISHED',NOW() - INTERVAL 18 DAY);

SET @bio_m1 = (SELECT id FROM course_modules WHERE course_id = @bio_course_id AND module_order = 1);
SET @bio_m2 = (SELECT id FROM course_modules WHERE course_id = @bio_course_id AND module_order = 2);

INSERT INTO lessons (module_id, title, content, lesson_order, estimated_minutes, status, published_at)
VALUES
    (@bio_m1,'Safety Contracts and Lab Roles','Students learn how roles, safety contracts, and procedures reduce risk.',1,25,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@bio_m1,'Variables, Controls, and Fair Tests','Students identify independent variables, dependent variables, constants, and controls.',2,30,'PUBLISHED',NOW() - INTERVAL 16 DAY),
    (@bio_m2,'Using a Compound Microscope','Students identify microscope parts, practice focusing safely, and calculate magnification.',1,35,'PUBLISHED',NOW() - INTERVAL 17 DAY),
    (@bio_m2,'Making Observations from Slides','Students prepare simple slides and record qualitative observations.',2,35,'PUBLISHED',NOW() - INTERVAL 16 DAY);

INSERT INTO course_modules (course_id, title, description, module_order, status, published_at)
VALUES
    (@write_course_id,'Module 1: Character and Point of View','Draft module about character goals, conflict, and perspective.',1,'DRAFT',NULL),
    (@write_course_id,'Module 2: Revision Workshop','Draft module about peer feedback, revision goals, and polishing prose.',2,'DRAFT',NULL);

SET @write_m1 = (SELECT id FROM course_modules WHERE course_id = @write_course_id AND module_order = 1);
SET @write_m2 = (SELECT id FROM course_modules WHERE course_id = @write_course_id AND module_order = 2);

INSERT INTO lessons (module_id, title, content, lesson_order, estimated_minutes, status, published_at)
VALUES
    (@write_m1,'Building a Character from Want and Fear','Draft lesson: students create a character by identifying what they want and fear.',1,30,'DRAFT',NULL),
    (@write_m1,'Point of View Choices','Draft lesson: students compare first person, third person limited, and omniscient narration.',2,30,'DRAFT',NULL);

-- ---------------------------------------------------------------------------
-- Enrollments: main roster + fuller student dashboard
-- ---------------------------------------------------------------------------

SET @maya_id = (SELECT id FROM users WHERE email = 'student.demo@example.com');
SET @alex_id = (SELECT id FROM users WHERE email = 'alex.chen@example.com');
SET @priya_id = (SELECT id FROM users WHERE email = 'priya.patel@example.com');
SET @jordan_id = (SELECT id FROM users WHERE email = 'jordan.lee@example.com');
SET @samir_id = (SELECT id FROM users WHERE email = 'samir.khan@example.com');
SET @lina_id = (SELECT id FROM users WHERE email = 'lina.morales@example.com');
SET @noah_id = (SELECT id FROM users WHERE email = 'noah.brooks@example.com');
SET @emma_id = (SELECT id FROM users WHERE email = 'emma.wilson@example.com');
SET @diego_id = (SELECT id FROM users WHERE email = 'diego.ramirez@example.com');
SET @harper_id = (SELECT id FROM users WHERE email = 'harper.nguyen@example.com');
SET @mia_id = (SELECT id FROM users WHERE email = 'mia.johnson@example.com');
SET @olivia_id = (SELECT id FROM users WHERE email = 'olivia.martin@example.com');
SET @ethan_id = (SELECT id FROM users WHERE email = 'ethan.clark@example.com');
SET @ava_id = (SELECT id FROM users WHERE email = 'ava.thompson@example.com');
SET @lucas_id = (SELECT id FROM users WHERE email = 'lucas.young@example.com');

INSERT INTO course_enrollments (course_id, student_id, enrollment_status, enrolled_at, completed_at)
VALUES
    (@eco_course_id,@maya_id,'ACTIVE',NOW() - INTERVAL 9 DAY,NULL),
    (@eco_course_id,@alex_id,'ACTIVE',NOW() - INTERVAL 12 DAY,NULL),
    (@eco_course_id,@priya_id,'ACTIVE',NOW() - INTERVAL 11 DAY,NULL),
    (@eco_course_id,@jordan_id,'ACTIVE',NOW() - INTERVAL 10 DAY,NULL),
    (@eco_course_id,@samir_id,'ACTIVE',NOW() - INTERVAL 9 DAY,NULL),
    (@eco_course_id,@lina_id,'ACTIVE',NOW() - INTERVAL 8 DAY,NULL),
    (@eco_course_id,@olivia_id,'ACTIVE',NOW() - INTERVAL 7 DAY,NULL),
    (@eco_course_id,@ethan_id,'ACTIVE',NOW() - INTERVAL 6 DAY,NULL),
    (@eco_course_id,@noah_id,'COMPLETED',NOW() - INTERVAL 20 DAY,NOW() - INTERVAL 3 DAY),
    (@eco_course_id,@emma_id,'COMPLETED',NOW() - INTERVAL 18 DAY,NOW() - INTERVAL 2 DAY),
    (@eco_course_id,@diego_id,'COMPLETED',NOW() - INTERVAL 16 DAY,NOW() - INTERVAL 1 DAY),
    (@eco_course_id,@lucas_id,'COMPLETED',NOW() - INTERVAL 22 DAY,NOW() - INTERVAL 5 DAY),
    (@eco_course_id,@harper_id,'DROPPED',NOW() - INTERVAL 14 DAY,NULL),
    (@eco_course_id,@mia_id,'DROPPED',NOW() - INTERVAL 13 DAY,NULL),
    (@eco_course_id,@ava_id,'DROPPED',NOW() - INTERVAL 13 DAY,NULL),

    -- Extra active courses for the main demo student dashboard.
    (@cs_course_id,@maya_id,'ACTIVE',NOW() - INTERVAL 6 DAY,NULL),
    (@hist_course_id,@maya_id,'ACTIVE',NOW() - INTERVAL 5 DAY,NULL),
    (@alg_course_id,@maya_id,'ACTIVE',NOW() - INTERVAL 4 DAY,NULL),

    -- Additional enrollment variety across extra courses.
    (@cs_course_id,@alex_id,'ACTIVE',NOW() - INTERVAL 6 DAY,NULL),
    (@cs_course_id,@priya_id,'ACTIVE',NOW() - INTERVAL 6 DAY,NULL),
    (@hist_course_id,@jordan_id,'ACTIVE',NOW() - INTERVAL 5 DAY,NULL),
    (@alg_course_id,@samir_id,'ACTIVE',NOW() - INTERVAL 4 DAY,NULL),
    (@bio_course_id,@lina_id,'ACTIVE',NOW() - INTERVAL 3 DAY,NULL),
    (@bio_course_id,@olivia_id,'ACTIVE',NOW() - INTERVAL 3 DAY,NULL);

SELECT 'Abundant LMS demo seed completed.' AS message;
SELECT 'Teacher login: teacher.demo@example.com / Password123!' AS teacher_login;
SELECT 'Student login: student.demo@example.com / Password123!' AS student_login;
SELECT 'Main demo join code: ECO2026' AS main_join_code;
