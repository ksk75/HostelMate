-- ============================================================
-- HostelMate — Sample Data
-- ============================================================
-- Run AFTER schema.sql to populate the database with test data.
-- All passwords are hashed using BCrypt. The plaintext passwords
-- are listed in comments for testing purposes.
-- ============================================================

USE hostelmate;

-- ============================================================
-- ROOMS (5 rooms across 3 floors)
-- ============================================================
INSERT INTO rooms (room_number, floor, capacity) VALUES
    ('101', 1, 4),
    ('102', 1, 3),
    ('201', 2, 4),
    ('202', 2, 2),
    ('301', 3, 4);

-- ============================================================
-- USERS (1 admin + 6 students)
-- ============================================================
-- Password for ALL test users: "password123"
-- BCrypt hash of "password123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- NOTE: In production, each password would have a unique salt.
--       For testing, we use the same hash for simplicity.

INSERT INTO users (full_name, email, password_hash, phone, role, room_id, is_active) VALUES
    -- Admin
    ('Admin User',    'admin@hostelmate.com',
     '$2a$10$H.HczJ5YmQTWSMDklmbcGOcy8mO2SD4SoFQ9FDnxlX1n8tsCAEJ/.',
     '9876543210', 'ADMIN', NULL, TRUE),

    -- Students in Room 101
    ('Rahul Sharma',  'rahul@hostelmate.com',
     '$2a$10$H.HczJ5YmQTWSMDklmbcGOcy8mO2SD4SoFQ9FDnxlX1n8tsCAEJ/.',
     '9876543211', 'STUDENT', 1, TRUE),
    ('Priya Nair',    'priya@hostelmate.com',
     '$2a$10$H.HczJ5YmQTWSMDklmbcGOcy8mO2SD4SoFQ9FDnxlX1n8tsCAEJ/.',
     '9876543212', 'STUDENT', 1, TRUE),
    ('Amit Kumar',    'amit@hostelmate.com',
     '$2a$10$H.HczJ5YmQTWSMDklmbcGOcy8mO2SD4SoFQ9FDnxlX1n8tsCAEJ/.',
     '9876543213', 'STUDENT', 1, TRUE),

    -- Students in Room 201
    ('Sneha Iyer',    'sneha@hostelmate.com',
     '$2a$10$H.HczJ5YmQTWSMDklmbcGOcy8mO2SD4SoFQ9FDnxlX1n8tsCAEJ/.',
     '9876543214', 'STUDENT', 3, TRUE),
    ('Karthik Raj',   'karthik@hostelmate.com',
     '$2a$10$H.HczJ5YmQTWSMDklmbcGOcy8mO2SD4SoFQ9FDnxlX1n8tsCAEJ/.',
     '9876543215', 'STUDENT', 3, TRUE),

    -- Student in Room 202
    ('Deepa Menon',   'deepa@hostelmate.com',
     '$2a$10$H.HczJ5YmQTWSMDklmbcGOcy8mO2SD4SoFQ9FDnxlX1n8tsCAEJ/.',
     '9876543216', 'STUDENT', 4, TRUE);

-- ============================================================
-- EXPENSES (20+ sample expenses)
-- ============================================================

-- Room 101 expenses (paid by Rahul - id:2, Priya - id:3, Amit - id:4)
INSERT INTO expenses (title, description, amount, expense_date, paid_by, category_id, split_type) VALUES
    ('May Rent',             'Monthly hostel rent for May 2026',         12000.00, '2026-05-01', 2, 1, 'EQUAL'),
    ('WiFi Bill May',        'Monthly broadband bill',                    2000.00, '2026-05-03', 3, 5, 'EQUAL'),
    ('Electricity May',      'Electricity bill for May',                  1800.00, '2026-05-05', 4, 3, 'EQUAL'),
    ('Mess Bill Week 1',     'Mess charges for first week',               2400.00, '2026-05-07', 2, 2, 'EQUAL'),
    ('Water Bill',           'Water supply charges',                       600.00, '2026-05-10', 3, 4, 'EQUAL'),
    ('Room Cleaning',        'Monthly room cleaning service',              900.00, '2026-05-12', 4, 6, 'EQUAL'),
    ('Mess Bill Week 2',     'Mess charges for second week',              2400.00, '2026-05-14', 2, 2, 'EQUAL'),
    ('Snacks & Drinks',      'Evening snacks order',                       750.00, '2026-05-15', 3, 7, 'EQUAL'),
    ('Mess Bill Week 3',     'Mess charges for third week',               2400.00, '2026-05-21', 4, 2, 'EQUAL'),
    ('Laundry',              'Laundry service for the room',               480.00, '2026-05-22', 2, 7, 'EQUAL'),

    -- Room 201 expenses (paid by Sneha - id:5, Karthik - id:6)
    ('May Rent Room 201',    'Monthly hostel rent for May',              10000.00, '2026-05-01', 5, 1, 'EQUAL'),
    ('WiFi Bill Room 201',   'Internet bill split',                       1500.00, '2026-05-03', 6, 5, 'EQUAL'),
    ('Electricity Room 201', 'Electricity for May',                       1200.00, '2026-05-05', 5, 3, 'EQUAL'),
    ('Mess Bill Room 201',   'Mess charges week 1-2',                     3600.00, '2026-05-10', 6, 2, 'EQUAL'),
    ('Groceries',            'Shared grocery shopping',                   1100.00, '2026-05-18', 5, 7, 'EQUAL'),

    -- Room 202 expense (paid by Deepa - id:7, solo)
    ('May Rent Room 202',    'Monthly rent for single room',              5000.00, '2026-05-01', 7, 1, 'EQUAL'),
    ('Electricity Room 202', 'Electricity for May',                        800.00, '2026-05-05', 7, 3, 'EQUAL'),

    -- Cross-room shared expenses
    ('Birthday Party',       'Sneha birthday celebration',                3000.00, '2026-05-20', 2, 7, 'EQUAL'),
    ('Study Materials',      'Shared textbooks and notes printing',       1200.00, '2026-05-22', 5, 7, 'CUSTOM'),
    ('Cricket Equipment',    'Bat and ball for weekend matches',           1500.00, '2026-05-25', 6, 7, 'EQUAL');

-- ============================================================
-- EXPENSE SHARES
-- ============================================================

-- Expense 1: May Rent (₹12000 split among Room 101: Rahul, Priya, Amit)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (1, 2, 4000.00, TRUE),   -- Rahul (paid - he's the payer)
    (1, 3, 4000.00, FALSE),  -- Priya owes
    (1, 4, 4000.00, FALSE);  -- Amit owes

-- Expense 2: WiFi Bill (₹2000 split among Room 101)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (2, 2, 666.67, FALSE),   -- Rahul owes
    (2, 3, 666.67, TRUE),    -- Priya (paid - she's the payer)
    (2, 4, 666.66, FALSE);   -- Amit owes

-- Expense 3: Electricity (₹1800 split among Room 101)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (3, 2, 600.00, FALSE),
    (3, 3, 600.00, FALSE),
    (3, 4, 600.00, TRUE);    -- Amit paid

-- Expense 4: Mess Bill Week 1 (₹2400 split among Room 101)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (4, 2, 800.00, TRUE),
    (4, 3, 800.00, FALSE),
    (4, 4, 800.00, FALSE);

-- Expense 5: Water Bill (₹600 split among Room 101)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (5, 2, 200.00, FALSE),
    (5, 3, 200.00, TRUE),
    (5, 4, 200.00, FALSE);

-- Expense 6: Room Cleaning (₹900 split among Room 101)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (6, 2, 300.00, FALSE),
    (6, 3, 300.00, FALSE),
    (6, 4, 300.00, TRUE);

-- Expense 7: Mess Bill Week 2 (₹2400)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (7, 2, 800.00, TRUE),
    (7, 3, 800.00, TRUE),
    (7, 4, 800.00, FALSE);

-- Expense 8: Snacks (₹750)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (8, 2, 250.00, FALSE),
    (8, 3, 250.00, TRUE),
    (8, 4, 250.00, TRUE);

-- Expense 9: Mess Bill Week 3 (₹2400)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (9, 2, 800.00, FALSE),
    (9, 3, 800.00, FALSE),
    (9, 4, 800.00, TRUE);

-- Expense 10: Laundry (₹480)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (10, 2, 160.00, TRUE),
    (10, 3, 160.00, FALSE),
    (10, 4, 160.00, FALSE);

-- Expense 11: May Rent Room 201 (₹10000 split Sneha, Karthik)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (11, 5, 5000.00, TRUE),
    (11, 6, 5000.00, FALSE);

-- Expense 12: WiFi Room 201 (₹1500)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (12, 5, 750.00, FALSE),
    (12, 6, 750.00, TRUE);

-- Expense 13: Electricity Room 201 (₹1200)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (13, 5, 600.00, TRUE),
    (13, 6, 600.00, FALSE);

-- Expense 14: Mess Bill Room 201 (₹3600)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (14, 5, 1800.00, FALSE),
    (14, 6, 1800.00, TRUE);

-- Expense 15: Groceries Room 201 (₹1100)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (15, 5, 550.00, TRUE),
    (15, 6, 550.00, FALSE);

-- Expense 16-17: Room 202 solo expenses (Deepa pays for herself)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (16, 7, 5000.00, TRUE),
    (17, 7,  800.00, TRUE);

-- Expense 18: Birthday Party (₹3000 split among all 6 students)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (18, 2, 500.00, TRUE),
    (18, 3, 500.00, FALSE),
    (18, 4, 500.00, FALSE),
    (18, 5, 500.00, FALSE),
    (18, 6, 500.00, FALSE),
    (18, 7, 500.00, TRUE);

-- Expense 19: Study Materials (₹1200 custom split)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (19, 2, 400.00, FALSE),
    (19, 5, 400.00, TRUE),
    (19, 6, 400.00, FALSE);

-- Expense 20: Cricket Equipment (₹1500 split among 5 students)
INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES
    (20, 2, 300.00, FALSE),
    (20, 3, 300.00, FALSE),
    (20, 4, 300.00, FALSE),
    (20, 5, 300.00, FALSE),
    (20, 6, 300.00, TRUE);

-- ============================================================
-- PAYMENTS (some payments already made)
-- ============================================================
INSERT INTO payments (payer_id, payee_id, amount, expense_share_id, payment_date, notes, payment_method) VALUES
    (3, 2, 800.00,  NULL, '2026-05-16', 'Mess Bill Week 2 payment',  'UPI'),
    (4, 3, 250.00,  NULL, '2026-05-17', 'Snacks payment',            'CASH'),
    (7, 2, 500.00,  NULL, '2026-05-21', 'Birthday party share',      'UPI');

-- ============================================================
-- SETTLEMENTS
-- ============================================================
INSERT INTO settlements (from_user_id, to_user_id, amount, settled_date, status, notes) VALUES
    (3, 2, 800.00,  '2026-05-16', 'COMPLETED', 'Settled mess bill week 2'),
    (6, 5, 5000.00, NULL,         'PENDING',   'May rent balance pending'),
    (4, 2, 1600.00, NULL,         'PENDING',   'Accumulated balance for May');

-- ============================================================
-- NOTIFICATIONS (sample notifications)
-- ============================================================
INSERT INTO notifications (user_id, message, type, is_read) VALUES
    (3, 'Rahul added expense "May Rent" - Your share: ₹4,000',          'EXPENSE_ADDED',     FALSE),
    (4, 'Rahul added expense "May Rent" - Your share: ₹4,000',          'EXPENSE_ADDED',     TRUE),
    (3, 'You have pending dues of ₹4,000 for May Rent',                  'PAYMENT_REMINDER',  FALSE),
    (4, 'You have pending dues of ₹4,000 for May Rent',                  'PAYMENT_REMINDER',  FALSE),
    (2, 'Priya paid ₹800 for Mess Bill Week 2',                          'PAYMENT_RECEIVED',  TRUE),
    (6, 'Sneha added expense "May Rent Room 201" - Your share: ₹5,000', 'EXPENSE_ADDED',     FALSE),
    (5, 'Reminder: Monthly rent is due on 1st June',                      'RENT_REMINDER',     FALSE),
    (2, 'Deepa paid ₹500 for Birthday Party share',                      'PAYMENT_RECEIVED',  FALSE),
    (3, 'Karthik added expense "Cricket Equipment" - Your share: ₹300',  'EXPENSE_ADDED',     FALSE),
    (4, 'Karthik added expense "Cricket Equipment" - Your share: ₹300',  'EXPENSE_ADDED',     FALSE);

-- ============================================================
-- END OF SAMPLE DATA
-- ============================================================
-- Login credentials for testing:
-- Admin:   admin@hostelmate.com    / password123
-- Student: rahul@hostelmate.com    / password123
-- Student: priya@hostelmate.com    / password123
-- Student: amit@hostelmate.com     / password123
-- Student: sneha@hostelmate.com    / password123
-- Student: karthik@hostelmate.com  / password123
-- Student: deepa@hostelmate.com    / password123
-- ============================================================
