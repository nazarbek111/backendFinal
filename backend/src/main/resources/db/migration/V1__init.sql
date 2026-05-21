-- =============================================
-- V1__init.sql  — Initial schema for literacy_db
-- =============================================

CREATE TABLE parents (
                         id         BIGSERIAL PRIMARY KEY,
                         email      VARCHAR(255) NOT NULL UNIQUE,
                         password   VARCHAR(255) NOT NULL,
                         name       VARCHAR(255),
                         role       VARCHAR(20)  NOT NULL DEFAULT 'PARENT',
                         created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE children (
                          id               BIGSERIAL PRIMARY KEY,
                          parent_id        BIGINT       NOT NULL REFERENCES parents(id) ON DELETE CASCADE,
                          name             VARCHAR(255) NOT NULL,
                          age              INTEGER      NOT NULL CHECK (age BETWEEN 3 AND 8),
                          avatar           VARCHAR(255),
                          xp_points        INTEGER      NOT NULL DEFAULT 0,
                          level            INTEGER      NOT NULL DEFAULT 1,
                          streak           INTEGER      NOT NULL DEFAULT 0,
                          last_active_date DATE,
                          role             VARCHAR(20)  NOT NULL DEFAULT 'CHILD',
                          created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE units (
                       id          BIGSERIAL PRIMARY KEY,
                       title       VARCHAR(255) NOT NULL,
                       description TEXT,
                       order_index INTEGER
);

CREATE TABLE lessons (
                         id          BIGSERIAL PRIMARY KEY,
                         unit_id     BIGINT      NOT NULL REFERENCES units(id) ON DELETE CASCADE,
                         title       VARCHAR(255) NOT NULL,
                         type        VARCHAR(30)  NOT NULL,
                         order_index INTEGER,
                         published   BOOLEAN      NOT NULL DEFAULT FALSE,
                         xp_reward   INTEGER      NOT NULL DEFAULT 10
);

CREATE TABLE exercises (
                           id             BIGSERIAL PRIMARY KEY,
                           lesson_id      BIGINT       NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
                           question       TEXT         NOT NULL,
                           correct_answer VARCHAR(500) NOT NULL,
                           options        TEXT,
                           order_index    INTEGER
);

CREATE TABLE lesson_progress (
                                 id           BIGSERIAL PRIMARY KEY,
                                 child_id     BIGINT    NOT NULL REFERENCES children(id) ON DELETE CASCADE,
                                 lesson_id    BIGINT    NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
                                 completed    BOOLEAN   NOT NULL DEFAULT FALSE,
                                 xp_earned    INTEGER   NOT NULL DEFAULT 0,
                                 completed_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                 UNIQUE (child_id, lesson_id)
);

CREATE TABLE exercise_results (
                                  id           BIGSERIAL PRIMARY KEY,
                                  child_id     BIGINT    NOT NULL REFERENCES children(id) ON DELETE CASCADE,
                                  exercise_id  BIGINT    NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,
                                  correct      BOOLEAN   NOT NULL,
                                  time_taken   INTEGER,
                                  submitted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE badges (
                        id          BIGSERIAL PRIMARY KEY,
                        name        VARCHAR(100) NOT NULL UNIQUE,
                        description VARCHAR(500),
                        icon        VARCHAR(255)
);

CREATE TABLE child_badges (
                              id        BIGSERIAL PRIMARY KEY,
                              child_id  BIGINT    NOT NULL REFERENCES children(id) ON DELETE CASCADE,
                              badge_id  BIGINT    NOT NULL REFERENCES badges(id) ON DELETE CASCADE,
                              earned_at TIMESTAMP NOT NULL DEFAULT NOW(),
                              UNIQUE (child_id, badge_id)
);

CREATE TABLE notifications (
                               id         BIGSERIAL PRIMARY KEY,
                               parent_id  BIGINT       NOT NULL,
                               message    VARCHAR(500) NOT NULL,
                               type       VARCHAR(30)  NOT NULL,
                               is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_tokens (
                                id          BIGSERIAL PRIMARY KEY,
                                parent_id   BIGINT       NOT NULL REFERENCES parents(id) ON DELETE CASCADE,
                                token       VARCHAR(500) NOT NULL UNIQUE,
                                expires_at  TIMESTAMP    NOT NULL,
                                created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE activity_logs (
                               id          BIGSERIAL PRIMARY KEY,
                               admin_email VARCHAR(255) NOT NULL,
                               action      VARCHAR(500) NOT NULL,
                               created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_children_parent_id   ON children(parent_id);
CREATE INDEX idx_lessons_unit_id      ON lessons(unit_id);
CREATE INDEX idx_exercises_lesson_id  ON exercises(lesson_id);
CREATE INDEX idx_lesson_progress_child ON lesson_progress(child_id);
CREATE INDEX idx_exercise_results_child ON exercise_results(child_id);
CREATE INDEX idx_child_badges_child    ON child_badges(child_id);
CREATE INDEX idx_notifications_parent  ON notifications(parent_id);
CREATE INDEX idx_activity_logs_admin   ON activity_logs(admin_email);

-- Seed badges
INSERT INTO badges (name, description, icon) VALUES
                                                 ('First Lesson',  'Completed first lesson!', '🎯'),
                                                 ('7-Day Streak',  '7 days in a row!',         '🔥'),
                                                 ('100 XP',        'Earned 100 XP!',            '⭐'),
                                                 ('Level Up',      'Reached level 2!',          '🚀'),
                                                 ('Unit Complete', 'Completed a full unit!',    '🏆');
