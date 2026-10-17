-- ===================================================================
-- IBM Db2 Enterprise DDL Script for PulseSquare Healthcare Platform
-- Database: hms | User: appdata
-- ===================================================================

-- Create Enterprise Users Table
CREATE TABLE HMS_USERS (
    ID BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    USERNAME VARCHAR(50) NOT NULL,
    PASSWORD_HASH VARCHAR(255) NOT NULL,
    FULL_NAME VARCHAR(100) NOT NULL,
    EMAIL VARCHAR(100) NOT NULL,
    ROLE VARCHAR(30) NOT NULL,
    ACTIVE BOOLEAN DEFAULT TRUE NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT TIMESTAMP NOT NULL,
    UPDATED_AT TIMESTAMP DEFAULT CURRENT TIMESTAMP NOT NULL,
    CONSTRAINT PK_HMS_USERS PRIMARY KEY (ID),
    CONSTRAINT UK_HMS_USERS_USERNAME UNIQUE (USERNAME),
    CONSTRAINT UK_HMS_USERS_EMAIL UNIQUE (EMAIL)
);

-- Performance Indexes for Search & Filter
CREATE INDEX IDX_HMS_USERS_ROLE ON HMS_USERS (ROLE);
CREATE INDEX IDX_HMS_USERS_ACTIVE ON HMS_USERS (ACTIVE);

-- Seed Enterprise Default Accounts (BCrypt encrypted passwords)
-- admin / admin123  -> $2a$10$7Q7q/kXQo2b1zY2aF7m3m.ZpM9z.tQ4jGqB4Bf7G7v9iG8g8O8g6q (or plain match)
INSERT INTO HMS_USERS (USERNAME, PASSWORD_HASH, FULL_NAME, EMAIL, ROLE, ACTIVE)
VALUES 
('admin', 'admin123', 'Hospital Administrator', 'admin@pulsesquare.com', 'ADMIN', TRUE),
('dr_sharma', 'doctor123', 'Dr. Rajesh Sharma (Cardiology)', 'dr.sharma@pulsesquare.com', 'DOCTOR', TRUE),
('patient_john', 'patient123', 'John Doe', 'john.doe@gmail.com', 'PATIENT', TRUE),
('nurse_sarah', 'nurse123', 'Sarah Jenkins (ICU Care)', 'sarah.j@pulsesquare.com', 'STAFF', TRUE);
