package com.pulsesquare.hms.repo;

import com.pulsesquare.hms.jooq.tables.pojos.HmsAuditLogs;
import com.pulsesquare.hms.jooq.tables.pojos.HmsPatients;
import com.pulsesquare.hms.jooq.tables.pojos.HmsRefreshTokens;
import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;
import com.pulsesquare.hms.jooq.tables.pojos.HmsUsers;
import com.pulsesquare.hms.repo.repository.AuditLogRepository;
import com.pulsesquare.hms.repo.repository.PatientRepository;
import com.pulsesquare.hms.repo.repository.RefreshTokenRepository;
import com.pulsesquare.hms.repo.repository.RoleRepository;
import com.pulsesquare.hms.repo.repository.UserRepository;
import com.pulsesquare.hms.repo.repository.impl.AuditLogRepositoryImpl;
import com.pulsesquare.hms.repo.repository.impl.PatientRepositoryImpl;
import com.pulsesquare.hms.repo.repository.impl.RefreshTokenRepositoryImpl;
import com.pulsesquare.hms.repo.repository.impl.RoleRepositoryImpl;
import com.pulsesquare.hms.repo.repository.impl.UserRepositoryImpl;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("jOOQ Repository Layer Integration Tests")
class RepositoryIntegrationTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private AuditLogRepository auditLogRepository;
    private PatientRepository patientRepository;

    @BeforeAll
    void setUp() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:hms_repo_test;MODE=DB2;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");

        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .outOfOrder(true)
                .load();
        flyway.migrate();

        DSLContext dsl = DSL.using(ds, SQLDialect.DEFAULT);

        roleRepository = new RoleRepositoryImpl(dsl);
        userRepository = new UserRepositoryImpl(dsl, roleRepository);
        refreshTokenRepository = new RefreshTokenRepositoryImpl(dsl);
        auditLogRepository = new AuditLogRepositoryImpl(dsl);
        patientRepository = new PatientRepositoryImpl(dsl);
    }

    @Test
    @DisplayName("RoleRepository: Save and find role by name and id")
    void testRoleRepository() {
        HmsRoles role = new HmsRoles();
        role.setName("ROLE_SURGEON");
        role.setDescription("Surgeon specialist");
        role.setCreatedAt(LocalDateTime.now());

        HmsRoles saved = roleRepository.save(role);
        assertNotNull(saved.getId());
        assertEquals("ROLE_SURGEON", saved.getName());

        Optional<HmsRoles> found = roleRepository.findByName("ROLE_SURGEON");
        assertTrue(found.isPresent());
        assertEquals("Surgeon specialist", found.get().getDescription());
    }

    @Test
    @DisplayName("UserRepository: Save user with roles and perform lookups")
    void testUserRepository() {
        HmsRoles roleAnesth = roleRepository.findByName("ROLE_ANESTHESIOLOGIST").orElseGet(() -> {
            HmsRoles r = new HmsRoles();
            r.setName("ROLE_ANESTHESIOLOGIST");
            r.setDescription("Anesthesiology Staff");
            r.setCreatedAt(LocalDateTime.now());
            return roleRepository.save(r);
        });

        HmsUsers user = new HmsUsers();
        user.setUsername("anesth_mike");
        user.setEmail("mike.anesth@pulsesquare.com");
        user.setPasswordHash("$2a$10$dummyHashValueForTestingPurposesOnly");
        user.setFullName("Mike Ross");
        user.setDepartment("Emergency Ward");
        user.setContactNumber("+1-555-4321");
        user.setAccountEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        Set<HmsRoles> roles = new HashSet<>();
        roles.add(roleAnesth);

        HmsUsers savedUser = userRepository.save(user, roles);
        assertNotNull(savedUser.getId());
        assertEquals("anesth_mike", savedUser.getUsername());

        assertTrue(userRepository.existsByUsername("anesth_mike"));
        assertTrue(userRepository.existsByEmail("mike.anesth@pulsesquare.com"));
        assertFalse(userRepository.existsByUsername("non_existent_nurse"));

        Optional<HmsUsers> foundByUsername = userRepository.findByUsername("anesth_mike");
        assertTrue(foundByUsername.isPresent());
        assertEquals("Mike Ross", foundByUsername.get().getFullName());

        Set<HmsRoles> userRoles = userRepository.findRolesByUserId(savedUser.getId());
        assertEquals(1, userRoles.size());
        assertTrue(userRoles.stream().anyMatch(r -> "ROLE_ANESTHESIOLOGIST".equals(r.getName())));
    }

    @Test
    @DisplayName("RefreshTokenRepository: Save, lookup by token, and delete")
    void testRefreshTokenRepository() {
        HmsUsers user = new HmsUsers();
        user.setUsername("token_user");
        user.setEmail("token_user@pulsesquare.com");
        user.setPasswordHash("hashed_secret");
        user.setFullName("Token Test User");
        user.setAccountEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        HmsUsers savedUser = userRepository.save(user, Collections.emptySet());

        HmsRefreshTokens token = new HmsRefreshTokens();
        token.setToken("sample-uuid-token-12345");
        token.setUserId(savedUser.getId());
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);
        token.setCreatedAt(LocalDateTime.now());

        HmsRefreshTokens savedToken = refreshTokenRepository.save(token);
        assertNotNull(savedToken.getId());

        Optional<HmsRefreshTokens> found = refreshTokenRepository.findByToken("sample-uuid-token-12345");
        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getUserId());

        refreshTokenRepository.deleteByUserId(savedUser.getId());
        Optional<HmsRefreshTokens> deletedLookup = refreshTokenRepository.findByToken("sample-uuid-token-12345");
        assertFalse(deletedLookup.isPresent());
    }

    @Test
    @DisplayName("AuditLogRepository: Save audit log and retrieve top 50")
    void testAuditLogRepository() {
        HmsAuditLogs log = new HmsAuditLogs();
        log.setAction("TEST_ACTION");
        log.setUsername("test_user");
        log.setIpAddress("127.0.0.1");
        log.setUserAgent("JUnit-Agent");
        log.setStatus("SUCCESS");
        log.setDetails("Repository test verification");
        log.setCreatedAt(LocalDateTime.now());

        HmsAuditLogs saved = auditLogRepository.save(log);
        assertNotNull(saved.getId());

        List<HmsAuditLogs> topLogs = auditLogRepository.findTop50();
        assertFalse(topLogs.isEmpty());
        assertTrue(topLogs.stream().anyMatch(l -> "TEST_ACTION".equals(l.getAction())));
    }

    @Test
    @DisplayName("PatientRepository: Auto-generate MRN, save patient, lookup by MRN, and search across modules")
    void testPatientRepository() {
        String generatedMrn = patientRepository.generateNextMrn();
        assertNotNull(generatedMrn);
        assertTrue(generatedMrn.startsWith("MRN-"), "MRN should start with enterprise prefix MRN-");

        HmsPatients patient = new HmsPatients();
        patient.setTitle("Mr");
        patient.setFirstName("Vikram");
        patient.setMiddleName("Pratap");
        patient.setLastName("Singh");
        patient.setGender("MALE");
        patient.setDateOfBirth(LocalDate.of(1990, 5, 20));
        patient.setAgeYears(36);
        patient.setBloodGroup("B+");
        patient.setContactNumber("+91-9876543210");
        patient.setEmail("vikram.singh@example.com");
        patient.setMaritalStatus("Married");
        patient.setReligion("Hindu");
        patient.setAddress("45 Lake View Road");
        patient.setCity("Nizamabad");
        patient.setState("Telangana");
        patient.setCountry("India");
        patient.setZipCode("503001");
        patient.setReferralDoctor("Dr. Rajesh Sharma");
        patient.setDepartment("Out-Patient");
        patient.setRegistrationFee(new BigDecimal("100.00"));
        patient.setPaymentType("Cash");
        patient.setActive(true);

        HmsPatients saved = patientRepository.save(patient);
        assertNotNull(saved.getId());
        assertNotNull(saved.getMrn());
        assertEquals("Vikram", saved.getFirstName());
        assertEquals("Singh", saved.getLastName());
        assertEquals("+91-9876543210", saved.getContactNumber());

        // Universal MRN Lookup across modules
        Optional<HmsPatients> byMrn = patientRepository.findByMrn(saved.getMrn());
        assertTrue(byMrn.isPresent(), "Patient must be findable by MRN");
        assertEquals(saved.getId(), byMrn.get().getId());
        assertTrue(patientRepository.existsByMrn(saved.getMrn()));

        // Search by MRN
        List<HmsPatients> searchByMrn = patientRepository.search(saved.getMrn(), 0, 10);
        assertEquals(1, searchByMrn.size());
        assertEquals(saved.getId(), searchByMrn.get(0).getId());

        // Search by Contact Number
        List<HmsPatients> searchByContact = patientRepository.search("9876543210", 0, 10);
        assertFalse(searchByContact.isEmpty());
        assertTrue(searchByContact.stream().anyMatch(p -> p.getId().equals(saved.getId())));

        // Search by Name
        List<HmsPatients> searchByName = patientRepository.search("Vikram", 0, 10);
        assertFalse(searchByName.isEmpty());
        assertTrue(searchByName.stream().anyMatch(p -> p.getId().equals(saved.getId())));

        assertEquals(1, patientRepository.countSearch("Vikram"));
    }
}
