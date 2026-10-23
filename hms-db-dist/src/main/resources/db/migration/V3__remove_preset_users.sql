-- ===================================================================
-- PulseSquare Enterprise HMS Database Migration
-- Version: V3
-- Description: Remove Pre-set Users from HMS_USERS & Reset Identity Sequence
-- Dialect: IBM Db2 Enterprise
-- ===================================================================

DELETE FROM hms_user_roles;
DELETE FROM hms_users;
ALTER TABLE hms_users ALTER COLUMN id RESTART WITH 1;
