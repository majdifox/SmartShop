-- ===============================
-- FINAL data.sql – WORKS IMMEDIATELY (2025-12-10)
-- ===============================

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE payments;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE clients;
TRUNCATE TABLE users;
TRUNCATE TABLE promo_codes;
TRUNCATE TABLE products;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. USERS
INSERT INTO users (id, username, password, role, created_at) VALUES
                                                                 (1, 'admin',           'admin123', 'ADMIN',  NOW()),
                                                                 (2, 'amine_tech',      'pass123',  'CLIENT', NOW()),
                                                                 (3, 'fatima_store',    'pass123',  'CLIENT', NOW()),
                                                                 (4, 'hassan_computers','pass123',  'CLIENT', NOW()),
                                                                 (5, 'sara_solutions',  'pass123',  'CLIENT', NOW()),
                                                                 (6, 'youssef_trading', 'pass123',  'CLIENT', NOW());

-- 2. CLIENTS
INSERT INTO clients (id, user_id, name, email, phone, address, tier, total_orders, total_spent, created_at, updated_at) VALUES
                                                                                                                            (1, 2, 'Amine Tech Solutions',   'amine@techsolutions.ma',   '0612345678', 'Casablanca, Maarif',   'BASIC',     0,     0.00, NOW(), NOW()),
                                                                                                                            (2, 3, 'Fatima IT Store',        'fatima@itstore.ma',        '0623456789', 'Rabat, Agdal',         'SILVER',    3,  1200.00, NOW(), NOW()),
                                                                                                                            (3, 4, 'Hassan Computers',       'hassan@computers.ma',      '0634567890', 'Marrakech, Gueliz',    'GOLD',     12,  6500.00, NOW(), NOW()),
                                                                                                                            (4, 5, 'Sara Digital Solutions', 'sara@digitalsolutions.ma', '0645678901', 'Tanger, Centre',       'PLATINUM', 25, 18000.00, NOW(), NOW()),
                                                                                                                            (5, 6, 'Youssef Trading',         'youssef@trading.ma',       '0656789012', 'Fes, Ville Nouvelle', 'BASIC',     1,   450.00, NOW(), NOW());

-- 3. PROMO CODES
INSERT INTO promo_codes (id, code, used, used_at, order_id) VALUES
                                                                (1, 'PROMO-TEST',  FALSE, NULL, NULL),
                                                                (2, 'PROMO-USED',  TRUE,  NULL, NULL),
                                                                (3, 'WELCOME2025', FALSE, NULL, NULL);

-- 4. PRODUCTS
INSERT INTO products (id, name, description, unit_price, stock, deleted, created_at, updated_at) VALUES
                                                                                                     (1,  'Laptop HP ProBook 450',   'Intel i5, 8GB, 256GB SSD', 5500.00, 25, FALSE, NOW(), NOW()),
                                                                                                     (2,  'Dell Monitor 24"',        'Full HD, IPS, HDMI',       1200.00, 50, FALSE, NOW(), NOW()),
                                                                                                     (3,  'Logitech Wireless Mouse', '2.4GHz, ergonomic',         150.00,100, FALSE, NOW(), NOW()),
                                                                                                     (4,  'HP LaserJet Printer',     'Network ready mono laser', 2800.00, 15, FALSE, NOW(), NOW()),
                                                                                                     (5,  'USB-C Hub Adapter',       '7-in-1, 4K HDMI',           350.00, 75, FALSE, NOW(), NOW()),
                                                                                                     (6,  'Mechanical Keyboard RGB', 'Backlit, mechanical',       800.00, 40, FALSE, NOW(), NOW()),
                                                                                                     (7,  'External HDD 2TB',        'USB 3.0 portable',          650.00, 60, FALSE, NOW(), NOW()),
                                                                                                     (8,  'Webcam Full HD 1080p',    'Autofocus, mic',            450.00, 30, FALSE, NOW(), NOW()),
                                                                                                     (9,  'Router WiFi 6',           'Dual band, gigabit',        950.00, 20, FALSE, NOW(), NOW()),
                                                                                                     (10, 'Laptop Stand Aluminum',   'Adjustable, cooling',       280.00, 80, FALSE, NOW(), NOW()),
                                                                                                     (11, 'Old Model Scanner',       'Discontinued',             1200.00,  0, TRUE,  '2024-01-10 10:00:00', NOW());

-- 5. ORDERS – exact enum values from OrderStatus
INSERT INTO orders (id, client_id, status, promo_code, subtotal_ht, loyalty_discount, promo_discount, total_discount, amount_ht, tva_amount, total_ttc, remaining_amount, created_at, updated_at) VALUES
                                                                                                                                                                                                      (1, 2, 'CONFIRMED', NULL,        600.00, 30.00,  0.00, 30.00, 570.00, 114.00, 684.00,   0.00, '2025-11-01 10:00:00', NOW()),
                                                                                                                                                                                                      (2, 3, 'CONFIRMED', 'PROMO-TEST',900.00, 90.00, 45.00,135.00, 765.00, 153.00, 918.00,   0.00, '2025-11-05 14:30:00', NOW()),
                                                                                                                                                                                                      (3, 4, 'PENDING',   NULL,       1500.00,225.00,  0.00,225.00,1275.00, 255.00,1530.00, 530.00, '2025-11-10 09:15:00', NOW()),
                                                                                                                                                                                                      (4, 1, 'PENDING',   NULL,        350.00,  0.00,  0.00,  0.00, 350.00,  70.00, 420.00, 420.00, '2025-11-20 16:00:00', NOW()),
                                                                                                                                                                                                      (5, 5, 'CANCELED',  NULL,        800.00,  0.00,  0.00,  0.00, 800.00, 160.00, 960.00, 960.00, '2025-11-15 11:30:00', NOW()),
                                                                                                                                                                                                      (6, 2, 'REJECTED',  NULL,          0.00,  0.00,  0.00,  0.00,   0.00,   0.00,   0.00,   0.00, '2025-11-18 13:00:00', NOW()),
                                                                                                                                                                                                      (7, 2, 'PENDING',   NULL,       1200.00, 60.00,  0.00, 60.00,1140.00, 228.00,1368.00,   0.00, '2025-11-25 10:00:00', NOW());

-- 6. ORDER ITEMS
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, line_total) VALUES
                                                                                         (1, 1, 3, 4, 150.00, 600.00),
                                                                                         (2, 2, 6, 1, 800.00, 800.00),
                                                                                         (3, 2, 8, 1, 100.00, 100.00),
                                                                                         (4, 3, 2, 1,1200.00,1200.00),
                                                                                         (5, 3,10, 1, 300.00, 300.00),
                                                                                         (6, 4, 5, 1, 350.00, 350.00),
                                                                                         (7, 5, 6, 1, 800.00, 800.00),
                                                                                         (8, 7, 2, 1,1200.00,1200.00);

-- 7. PAYMENTS – REAL ENUM VALUES FROM PaymentStatus (EN_ATTENTE, ENCAISSÉ, REJETÉ)
INSERT INTO payments (id, order_id, payment_number, amount, payment_method, status, reference, payment_date, created_at) VALUES
                                                                                                                             (1, 1, 1, 684.00, 'CASH',          'ENCAISSE',   'REU-001',          '2025-11-01 10:30:00', NOW()),
                                                                                                                             (2, 2, 1, 500.00, 'CASH',          'ENCAISSE',   'REU-002',          '2025-11-05 14:45:00', NOW()),
                                                                                                                             (3, 2, 2, 418.00, 'CHECK',         'ENCAISSE',   'CHQ-12345',        '2025-11-10 09:00:00', NOW()),
                                                                                                                             (4, 3, 1, 600.00, 'CASH',          'ENCAISSE',   'REU-003',          '2025-11-10 09:30:00', NOW()),
                                                                                                                             (5, 3, 2, 400.00, 'CHECK',         'EN_ATTENTE', 'CHQ-67890',        '2025-12-05 00:00:00', NOW()),
                                                                                                                             (6, 7, 1,1368.00, 'WIRE_TRANSFER', 'ENCAISSE',   'VIR-2025-11-25-001','2025-11-25 10:30:00', NOW());