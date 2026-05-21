-- =============================================
-- V2__seed_admin.sql — Default admin account
-- Password: Admin@1234  (BCrypt cost=12)
-- CHANGE THIS PASSWORD after first deploy!
-- =============================================

INSERT INTO parents (email, password, name, role, created_at)
VALUES (
           'admin@literacy.kz',
           '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
           'Platform Admin',
           'ADMIN',
           NOW()
       )
    ON CONFLICT (email) DO NOTHING;