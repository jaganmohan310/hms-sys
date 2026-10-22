-- ===================================================================
-- PulseSquare Enterprise HMS Database Migration
-- Version: V6
-- Description: Expand Patient Schema for Complete Registration Form Attributes
-- Dialect: IBM Db2 Enterprise / ANSI SQL
-- ===================================================================

ALTER TABLE hms_patients ADD COLUMN reg_date DATE;
ALTER TABLE hms_patients ADD COLUMN reg_expiry_date DATE;
ALTER TABLE hms_patients ADD COLUMN dob_raw VARCHAR(30);
ALTER TABLE hms_patients ADD COLUMN landline VARCHAR(30);
ALTER TABLE hms_patients ADD COLUMN area VARCHAR(100);
ALTER TABLE hms_patients ADD COLUMN education_level VARCHAR(50);
ALTER TABLE hms_patients ADD COLUMN occupation VARCHAR(50);
ALTER TABLE hms_patients ADD COLUMN birth_mark VARCHAR(150);
ALTER TABLE hms_patients ADD COLUMN comments VARCHAR(500);
ALTER TABLE hms_patients ADD COLUMN photo_file_name VARCHAR(255);
ALTER TABLE hms_patients ADD COLUMN photo_data CLOB;
ALTER TABLE hms_patients ADD COLUMN discount_type VARCHAR(50) DEFAULT 'No Discount';
ALTER TABLE hms_patients ADD COLUMN discount_percent DECIMAL(5, 2) DEFAULT 0.00;
ALTER TABLE hms_patients ADD COLUMN discount_amount DECIMAL(10, 2) DEFAULT 0.00;
ALTER TABLE hms_patients ADD COLUMN net_amount DECIMAL(10, 2) DEFAULT 0.00;
ALTER TABLE hms_patients ADD COLUMN paid_amount DECIMAL(10, 2) DEFAULT 0.00;
ALTER TABLE hms_patients ADD COLUMN due_amount DECIMAL(10, 2) DEFAULT 0.00;
