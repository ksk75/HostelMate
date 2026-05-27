-- ============================================================
-- HostelMate — Hostel Expense Management System
-- Database Schema (MySQL 8.0+)
-- ============================================================
-- Run this script to create the complete database schema.
-- Usage: mysql -u root -p < schema.sql
-- ============================================================

-- Create and use the database
CREATE DATABASE IF NOT EXISTS hostelmate
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE hostelmate;

-- ============================================================
-- 1. ROOMS TABLE
-- Stores hostel room information
-- ============================================================
CREATE TABLE IF NOT EXISTS rooms (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10)  NOT NULL UNIQUE,
    floor       INT          NOT NULL DEFAULT 1,
    capacity    INT          NOT NULL DEFAULT 4,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_floor    CHECK (floor >= 0 AND floor <= 20),
    CONSTRAINT chk_capacity CHECK (capacity >= 1 AND capacity <= 10)
) ENGINE=InnoDB;

-- ============================================================
-- 2. USERS TABLE
-- Stores all hostel residents and admins
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    phone         VARCHAR(15)   DEFAULT NULL,
    role          ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    room_id       INT           DEFAULT NULL,
    profile_pic   VARCHAR(255)  DEFAULT 'default-avatar.png',
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_user_room FOREIGN KEY (room_id) REFERENCES rooms(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    -- Indexes
    INDEX idx_user_email (email),
    INDEX idx_user_role  (role),
    INDEX idx_user_room  (room_id)
) ENGINE=InnoDB;

-- ============================================================
-- 3. CATEGORIES TABLE
-- Predefined expense categories
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)   NOT NULL UNIQUE,
    icon        VARCHAR(50)   DEFAULT 'bi-tag',
    description VARCHAR(200)  DEFAULT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Seed default categories
INSERT INTO categories (name, icon, description) VALUES
    ('Rent',          'bi-house-door',    'Monthly hostel rent'),
    ('Food/Mess',     'bi-cup-straw',     'Mess bills and food expenses'),
    ('Electricity',   'bi-lightning',     'Electricity and power bills'),
    ('Water',         'bi-droplet',       'Water supply charges'),
    ('WiFi',          'bi-wifi',          'Internet and WiFi charges'),
    ('Maintenance',   'bi-tools',         'Room and hostel maintenance'),
    ('Miscellaneous', 'bi-three-dots',    'Other miscellaneous expenses');

-- ============================================================
-- 4. EXPENSES TABLE
-- Individual expense records
-- ============================================================
CREATE TABLE IF NOT EXISTS expenses (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(150)  NOT NULL,
    description TEXT          DEFAULT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    expense_date DATE         NOT NULL,
    paid_by     INT           NOT NULL,
    category_id INT           NOT NULL,
    split_type  ENUM('EQUAL', 'CUSTOM') NOT NULL DEFAULT 'EQUAL',
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_expense_paidby   FOREIGN KEY (paid_by)     REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Constraints
    CONSTRAINT chk_amount CHECK (amount > 0),

    -- Indexes
    INDEX idx_expense_paidby   (paid_by),
    INDEX idx_expense_category (category_id),
    INDEX idx_expense_date     (expense_date),
    INDEX idx_expense_deleted  (is_deleted)
) ENGINE=InnoDB;

-- ============================================================
-- 5. EXPENSE_SHARES TABLE
-- Tracks who shares each expense and their individual amounts
-- ============================================================
CREATE TABLE IF NOT EXISTS expense_shares (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    expense_id    INT           NOT NULL,
    user_id       INT           NOT NULL,
    share_amount  DECIMAL(10,2) NOT NULL,
    is_paid       BOOLEAN       NOT NULL DEFAULT FALSE,
    paid_date     DATE          DEFAULT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_share_expense FOREIGN KEY (expense_id) REFERENCES expenses(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_share_user    FOREIGN KEY (user_id)    REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    -- Prevent duplicate shares for same user on same expense
    UNIQUE KEY uk_expense_user (expense_id, user_id),

    -- Constraints
    CONSTRAINT chk_share_amount CHECK (share_amount >= 0),

    -- Indexes
    INDEX idx_share_user    (user_id),
    INDEX idx_share_paid    (is_paid)
) ENGINE=InnoDB;

-- ============================================================
-- 6. PAYMENTS TABLE
-- Records actual payments between users
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    payer_id         INT           NOT NULL,
    payee_id         INT           NOT NULL,
    amount           DECIMAL(10,2) NOT NULL,
    expense_share_id INT           DEFAULT NULL,
    payment_date     DATE          NOT NULL,
    notes            VARCHAR(255)  DEFAULT NULL,
    payment_method   ENUM('CASH', 'UPI', 'BANK_TRANSFER', 'OTHER') DEFAULT 'CASH',
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_payment_payer FOREIGN KEY (payer_id)         REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_payment_payee FOREIGN KEY (payee_id)         REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_payment_share FOREIGN KEY (expense_share_id) REFERENCES expense_shares(id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    -- Constraints
    CONSTRAINT chk_payment_amount   CHECK (amount > 0),

    -- Indexes
    INDEX idx_payment_payer (payer_id),
    INDEX idx_payment_payee (payee_id),
    INDEX idx_payment_date  (payment_date)
) ENGINE=InnoDB;

-- ============================================================
-- 7. SETTLEMENTS TABLE
-- Tracks balance settlements between users
-- ============================================================
CREATE TABLE IF NOT EXISTS settlements (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    from_user_id  INT           NOT NULL,
    to_user_id    INT           NOT NULL,
    amount        DECIMAL(10,2) NOT NULL,
    settled_date  DATE          DEFAULT NULL,
    status        ENUM('PENDING', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    notes         VARCHAR(255)  DEFAULT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_settlement_from FOREIGN KEY (from_user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_settlement_to   FOREIGN KEY (to_user_id)   REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    -- Constraints
    CONSTRAINT chk_settlement_amount CHECK (amount > 0),

    -- Indexes
    INDEX idx_settlement_from   (from_user_id),
    INDEX idx_settlement_to     (to_user_id),
    INDEX idx_settlement_status (status)
) ENGINE=InnoDB;

-- ============================================================
-- 8. NOTIFICATIONS TABLE
-- In-app notification system
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT           NOT NULL,
    message    VARCHAR(500)  NOT NULL,
    type       ENUM('EXPENSE_ADDED', 'PAYMENT_RECEIVED', 'PAYMENT_REMINDER',
                    'SETTLEMENT_REQUEST', 'SYSTEM', 'RENT_REMINDER') NOT NULL DEFAULT 'SYSTEM',
    link       VARCHAR(255)  DEFAULT NULL,
    is_read    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    -- Indexes
    INDEX idx_notif_user   (user_id),
    INDEX idx_notif_read   (is_read),
    INDEX idx_notif_created (created_at)
) ENGINE=InnoDB;

-- ============================================================
-- END OF SCHEMA
-- ============================================================
