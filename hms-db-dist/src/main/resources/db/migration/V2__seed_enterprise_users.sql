-- ===================================================================
-- PulseSquare Enterprise HMS Database Migration
-- Version: V2
-- Description: Seed Enterprise Roles & Default Healthcare Staff/Patient Accounts
-- Dialect: IBM Db2 Enterprise
-- ===================================================================

-- 1. Insert Standard Healthcare Roles
INSERT INTO hms_roles (id, name, description, created_at) VALUES 
(1, 'ROLE_ADMIN', 'System Administrator with full healthcare platform access', CURRENT_TIMESTAMP),
(2, 'ROLE_DOCTOR', 'Medical Doctor and Clinician', CURRENT_TIMESTAMP),
(3, 'ROLE_NURSE', 'Registered Inpatient and Outpatient Nurse', CURRENT_TIMESTAMP),
(4, 'ROLE_RECEPTIONIST', 'Front Desk Registration and Patient Admissions', CURRENT_TIMESTAMP),
(5, 'ROLE_PHARMACIST', 'Pharmacy Dispensing and Medication Inventory', CURRENT_TIMESTAMP),
(6, 'ROLE_LAB_TECH', 'Diagnostic Laboratory and Pathology Technician', CURRENT_TIMESTAMP),
(7, 'ROLE_PATIENT', 'Healthcare Patient with Access to Personal Records', CURRENT_TIMESTAMP);

-- 2. Insert Default Enterprise Users (Passwords hashed via BCrypt)
-- admin -> admin123
-- dr_sharma -> doctor123
-- nurse_sarah -> nurse123
-- reception_priya -> reception123
-- patient_john -> patient123
INSERT INTO hms_users (id, username, email, password_hash, full_name, contact_number, department, account_enabled, account_locked, failed_login_attempts, created_at, updated_at) VALUES
(1, 'admin', 'admin@pulsesquare.com', '$2a$10$eLWB/WQFAbg4K5sA7REtcuohgcJifX/kb0WchrNbdu3eABCoqj2mW', 'Hospital Administrator', '+1-800-555-0199', 'Administration', TRUE, FALSE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'dr_sharma', 'dr.sharma@pulsesquare.com', '$2a$10$.F9HR4tNiHhccfNd2yvfdeU02r4SOF1lht2S4FRAJtQgU4juczzH6', 'Dr. Rajesh Sharma', '+1-800-555-0123', 'Cardiology', TRUE, FALSE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'nurse_sarah', 'sarah.j@pulsesquare.com', '$2a$10$NcYBfig2cwNF7d1p7pVt/O9Pqu1nBhR468du1xVYdusGXmzcaw/3y', 'Sarah Jenkins, RN', '+1-800-555-0145', 'ICU Care', TRUE, FALSE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'reception_priya', 'priya.n@pulsesquare.com', '$2a$10$lmR7S.9JjDSsx3NzCOlWd.d1IJVF05NkoGu8jLzQwADN7pxIYAu8W', 'Priya Nair', '+1-800-555-0167', 'Admissions & Front Desk', TRUE, FALSE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'patient_john', 'john.doe@gmail.com', '$2a$10$KUgZuUYtEwauFi8EcHpNSuiNXtXI8gi7cklnnMtkXYd.mfgIE8eZa', 'John Doe', '+1-800-555-0188', 'Patient Care', TRUE, FALSE, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Map Enterprise Users to Roles
INSERT INTO hms_user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ROLE_ADMIN
(2, 2), -- dr_sharma -> ROLE_DOCTOR
(3, 3), -- nurse_sarah -> ROLE_NURSE
(4, 4), -- reception_priya -> ROLE_RECEPTIONIST
(5, 7); -- patient_john -> ROLE_PATIENT

-- 4. Advance Identity Sequences Past Seeded IDs (Compatible with DB2 and H2)
ALTER TABLE hms_users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE hms_roles ALTER COLUMN id RESTART WITH 100;
