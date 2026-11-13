-- =====================================================================
--  News & Bookmark Management System
--  MySQL schema and seed data
--
--  Run this once before starting the application:
--      mysql -u root -p < database/schema.sql
--  or open the file in MySQL Workbench and press the lightning bolt.
-- =====================================================================

DROP DATABASE IF EXISTS news_bookmark_db;
CREATE DATABASE news_bookmark_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE news_bookmark_db;

-- ---------------------------------------------------------------------
-- 1. users
--    Login credentials and role of every person using the system.
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id     INT           NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)   NOT NULL,
    password    VARCHAR(64)   NOT NULL COMMENT 'SHA-256 hash, never plain text',
    full_name   VARCHAR(100)  NOT NULL,
    role        VARCHAR(20)   NOT NULL DEFAULT 'User' COMMENT 'Admin or User',
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_username (username)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 2. categories
--    Shared by news articles and bookmarks.
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    category_id   INT          NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(50)  NOT NULL,
    PRIMARY KEY (category_id),
    UNIQUE KEY uq_categories_name (category_name)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 3. news
--    Articles shown in the news feed.
-- ---------------------------------------------------------------------
CREATE TABLE news (
    news_id     INT           NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255)  NOT NULL,
    source      VARCHAR(100)  NOT NULL,
    category_id INT           NULL,
    news_date   DATE          NULL,
    url         VARCHAR(255)  NULL,
    summary     TEXT          NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (news_id),
    KEY idx_news_category (category_id),
    KEY idx_news_date (news_date),
    CONSTRAINT fk_news_category
        FOREIGN KEY (category_id) REFERENCES categories (category_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- 4. bookmarks
--    Links saved by a user. news_id is filled when the bookmark was
--    created from the feed, and left NULL when the link was typed in.
-- ---------------------------------------------------------------------
CREATE TABLE bookmarks (
    bookmark_id INT           NOT NULL AUTO_INCREMENT,
    user_id     INT           NOT NULL,
    news_id     INT           NULL,
    title       VARCHAR(255)  NOT NULL,
    url         VARCHAR(255)  NOT NULL,
    category_id INT           NULL,
    date_added  DATE          NOT NULL,
    notes       TEXT          NULL,
    PRIMARY KEY (bookmark_id),
    KEY idx_bookmarks_user (user_id),
    KEY idx_bookmarks_category (category_id),
    UNIQUE KEY uq_bookmarks_user_url (user_id, url),
    CONSTRAINT fk_bookmarks_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_bookmarks_news
        FOREIGN KEY (news_id) REFERENCES news (news_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    CONSTRAINT fk_bookmarks_category
        FOREIGN KEY (category_id) REFERENCES categories (category_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE = InnoDB;

-- =====================================================================
--  Seed data - the same records the prototype shows in sample mode
-- =====================================================================

-- Passwords are SHA-256 hashes:
--   admin123 -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
--   1234     -> 03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4
INSERT INTO users (username, password, full_name, role) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
 'Administrator', 'Admin'),
('vijay', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',
 'Vijay Sai', 'User');

INSERT INTO categories (category_name) VALUES
('Technology'), ('Sports'), ('Business'), ('Science'), ('Health'), ('Entertainment');

INSERT INTO news (title, source, category_id, news_date, url, summary) VALUES
('ISRO successfully tests reusable launch vehicle', 'The Hindu',
 (SELECT category_id FROM categories WHERE category_name = 'Science'),
 '2026-09-04', 'https://example.com/isro-rlv',
 'The test flight validated autonomous landing systems and marks a step towards low-cost access to orbit.'),

('New Java release focuses on startup performance', 'InfoQ',
 (SELECT category_id FROM categories WHERE category_name = 'Technology'),
 '2026-09-03', 'https://example.com/java-release',
 'The latest release brings faster JVM warm-up, smaller container images and improved pattern matching for switch.'),

('Markets close higher as IT stocks rally', 'Economic Times',
 (SELECT category_id FROM categories WHERE category_name = 'Business'),
 '2026-09-03', 'https://example.com/markets-rally',
 'Benchmark indices ended the session in the green, led by gains in information technology and banking counters.'),

('India wins the series decider in the final over', 'ESPNcricinfo',
 (SELECT category_id FROM categories WHERE category_name = 'Sports'),
 '2026-09-02', 'https://example.com/series-decider',
 'A composed finish from the middle order sealed a two-wicket win and the series with one match to spare.'),

('Study links regular walking to better sleep quality', 'BBC Health',
 (SELECT category_id FROM categories WHERE category_name = 'Health'),
 '2026-09-01', 'https://example.com/walking-sleep',
 'Researchers followed 4,000 adults for a year and found moderate daily activity improved reported sleep quality.'),

('Streaming platforms invest in regional cinema', 'Variety',
 (SELECT category_id FROM categories WHERE category_name = 'Entertainment'),
 '2026-08-31', 'https://example.com/regional-cinema',
 'Regional language originals now account for a growing share of new commissions across major streaming services.');

INSERT INTO bookmarks (user_id, news_id, title, url, category_id, date_added, notes) VALUES
((SELECT user_id FROM users WHERE username = 'admin'), NULL,
 'Java Swing Tutorial - Oracle Docs', 'https://docs.oracle.com/javase/tutorial/uiswing/',
 (SELECT category_id FROM categories WHERE category_name = 'Technology'),
 '2026-09-04', 'Reference for layout managers.'),

((SELECT user_id FROM users WHERE username = 'admin'),
 (SELECT news_id FROM news WHERE url = 'https://example.com/java-release'),
 'New Java release focuses on startup performance', 'https://example.com/java-release',
 (SELECT category_id FROM categories WHERE category_name = 'Technology'),
 '2026-09-03', 'Saved from the news feed.'),

((SELECT user_id FROM users WHERE username = 'admin'), NULL,
 'MySQL Workbench schema design guide', 'https://dev.mysql.com/doc/workbench/en/',
 (SELECT category_id FROM categories WHERE category_name = 'Technology'),
 '2026-09-02', 'Needed for the database design section.'),

((SELECT user_id FROM users WHERE username = 'admin'),
 (SELECT news_id FROM news WHERE url = 'https://example.com/series-decider'),
 'India wins the series decider in the final over', 'https://example.com/series-decider',
 (SELECT category_id FROM categories WHERE category_name = 'Sports'),
 '2026-09-02', 'Watch highlights later.');

-- =====================================================================
--  Useful checks
-- =====================================================================
-- SELECT n.title, n.source, c.category_name, n.news_date
--   FROM news n LEFT JOIN categories c ON c.category_id = n.category_id
--  ORDER BY n.news_date DESC;
--
-- SELECT u.username, COUNT(b.bookmark_id) AS saved
--   FROM users u LEFT JOIN bookmarks b ON b.user_id = u.user_id
--  GROUP BY u.username;
