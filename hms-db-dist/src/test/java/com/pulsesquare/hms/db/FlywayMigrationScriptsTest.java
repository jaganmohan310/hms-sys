package com.pulsesquare.hms.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flyway Migration Scripts Integrity Tests")
class FlywayMigrationScriptsTest {

    @Test
    @DisplayName("Should verify V1 auth schema migration script exists and defines core tables")
    void testV1SchemaScript() throws Exception {
        InputStream is = getClass().getResourceAsStream("/db/migration/V1__init_auth_schema.sql");
        assertNotNull(is, "V1__init_auth_schema.sql must exist on classpath");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(content.contains("CREATE TABLE hms_roles"), "Must create hms_roles table");
        assertTrue(content.contains("CREATE TABLE hms_users"), "Must create hms_users table");
        assertTrue(content.contains("CREATE TABLE hms_user_roles"), "Must create hms_user_roles table");
    }

    @Test
    @DisplayName("Should verify V2 seed migration script exists and seeds roles")
    void testV2SeedScript() throws Exception {
        InputStream is = getClass().getResourceAsStream("/db/migration/V2__seed_enterprise_users.sql");
        assertNotNull(is, "V2__seed_enterprise_users.sql must exist on classpath");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(content.contains("ROLE_ADMIN"), "Must seed ROLE_ADMIN");
        assertTrue(content.contains("ROLE_DOCTOR"), "Must seed ROLE_DOCTOR");
        assertTrue(content.contains("ROLE_PATIENT"), "Must seed ROLE_PATIENT");
    }

    @Test
    @DisplayName("Should verify V3 purge migration script purges preset users from real Db2 database")
    void testV3PurgeScript() throws Exception {
        InputStream is = getClass().getResourceAsStream("/db/migration/V3__remove_preset_users.sql");
        assertNotNull(is, "V3__remove_preset_users.sql must exist on classpath");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(content.contains("DELETE FROM hms_users"), "Must purge dummy users");
        assertTrue(content.contains("RESTART WITH 1"), "Must reset identity sequence to 1");
    }

    @Test
    @DisplayName("Should verify V4 patient schema migration script creates patients table and sequence")
    void testV4PatientSchemaScript() throws Exception {
        InputStream is = getClass().getResourceAsStream("/db/migration/V4__create_patient_schema.sql");
        assertNotNull(is, "V4__create_patient_schema.sql must exist on classpath");
        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(content.contains("CREATE TABLE hms_patients"), "Must create hms_patients table");
        assertTrue(content.contains("CREATE SEQUENCE seq_hms_mrn"), "Must create seq_hms_mrn sequence");
        assertTrue(content.contains("mrn VARCHAR(50) NOT NULL UNIQUE"), "Must define unique MRN column");
    }
}
