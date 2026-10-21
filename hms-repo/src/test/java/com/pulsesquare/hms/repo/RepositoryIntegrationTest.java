package com.pulsesquare.hms.repo;

import com.pulsesquare.hms.jooq.tables.pojos.HmsAuditLogs;
import com.pulsesquare.hms.jooq.tables.pojos.HmsRefreshTokens;
import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;
import com.pulsesquare.hms.jooq.tables.pojos.HmsUsers;
import com.pulsesquare.hms.repo.repository.AuditLogRepository;
import com.pulsesquare.hms.repo.repository.RefreshTokenRepository;
import com.pulsesquare.hms.repo.repository.RoleRepository;
import com.pulsesquare.hms.repo.repository.UserRepository;
import com.pulsesquare.hms.repo.repository.impl.AuditLogRepositoryImpl;
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
    }

    @Test
    @DisplayName("RoleRepository: Save and find role by name and id")
    void testRoleRepository() {
        HmsRoles role = new HmsRoles();
        role.setName("ROLE_SURGEON");
        role.setDescription("Surgeon specialist");
        role.setCreatedAt(LocalDateTime.now());

        HmsRoles saved = roleRepository.save(role);
        assertNotNull(saved.getId(), "Generated role ID should not be null");

        Optional<HmsRoles> foundByName = roleRepository.findByName("ROLE_SURGEON");
        assertTrue(foundByName.isPresent());
        assertEquals("ROLE_SURGEON", foundByName.get().getName());

        Optional<HmsRoles> foundById = roleRepository.findById(saved.getId());
        assertTrue(foundById.isPresent());
        assertEquals("ROLE_SURGEON", foundById.get().getName());

        assertTrue(roleRepository.existsByName("ROLE_SURGEON"));
        assertFalse(roleRepository.existsByName("ROLE_NON_EXISTENT"));
    }

    @Test
    @DisplayName("UserRepository: Insert, find, update and assign role to user")
    void testUserRepository() {
        HmsRoles role = new HmsRoles();
        role.setName("ROLE_ANESTHESIOLOGIST");
        role.setDescription("Anesthesiology Doctor");
        role.setCreatedAt(LocalDateTime.now());
        HmsRoles savedRole = roleRepository.save(role);

        HmsUsers user = new HmsUsers();
        user.setUsername("dr_anand");
        user.setEmail("dr.anand@pulsesquare.com");
        user.setPasswordHash("$2a$10$hashedPasswordSample");
        user.setFullName("Dr. Anand K");
        user.setContactNumber("+91-9988776655");
        user.setDepartment("Anesthesia");
        user.setAccountEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);

        HmsUsers savedUser = userRepository.save(user, new HashSet<>(Collections.singletonList(savedRole)));
        assertNotNull(savedUser.getId(), "Generated user ID should not be null");

        Optional<HmsUsers> byUsername = userRepository.findByUsername("dr_anand");
        assertTrue(byUsername.isPresent());
        assertEquals("dr_anand", byUsername.get().getUsername());

        Optional<HmsUsers> byEmail = userRepository.findByEmail("dr.anand@pulsesquare.com");
        assertTrue(byEmail.isPresent());

        Optional<HmsUsers> byUserOrEmail = userRepository.findByUsernameOrEmail("dr_anand", "dr.anand@pulsesquare.com");
        assertTrue(byUserOrEmail.isPresent());

        assertTrue(userRepository.existsByUsername("dr_anand"));
        assertTrue(userRepository.existsByEmail("dr.anand@pulsesquare.com"));
        assertFalse(userRepository.existsByUsername("ghost_user"));

        Set<HmsRoles> roles = userRepository.findRolesByUserId(savedUser.getId());
        assertFalse(roles.isEmpty());
        assertTrue(roles.stream().anyMatch(r -> "ROLE_ANESTHESIOLOGIST".equals(r.getName())));

        // Test update
        savedUser.setFullName("Dr. Anand Kumar");
        userRepository.save(savedUser);
        Optional<HmsUsers> updated = userRepository.findById(savedUser.getId());
        assertTrue(updated.isPresent());
        assertEquals("Dr. Anand Kumar", updated.get().getFullName());
    }

    @Test
    @DisplayName("RefreshTokenRepository: Save, find by token and delete by user ID")
    void testRefreshTokenRepository() {
        // Create an owning user first to satisfy foreign key FK_REFRESH_TOKEN_USER
        HmsUsers user = new HmsUsers();
        user.setUsername("token_test_user");
        user.setEmail("token_test@pulsesquare.com");
        user.setPasswordHash("$2a$10$tokenHashSample");
        user.setFullName("Token Test User");
        user.setAccountEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        HmsUsers savedUser = userRepository.save(user);

        HmsRefreshTokens token = new HmsRefreshTokens();
        token.setToken("sample-refresh-uuid-token-12345");
        token.setUserId(savedUser.getId());
        token.setExpiryDate(LocalDateTime.now().plusDays(7));
        token.setRevoked(false);

        HmsRefreshTokens saved = refreshTokenRepository.save(token);
        assertNotNull(saved.getId());

        Optional<HmsRefreshTokens> found = refreshTokenRepository.findByToken("sample-refresh-uuid-token-12345");
        assertTrue(found.isPresent());
        assertEquals(savedUser.getId(), found.get().getUserId());

        int deleted = refreshTokenRepository.deleteByUserId(savedUser.getId());
        assertTrue(deleted >= 1);

        Optional<HmsRefreshTokens> deletedLookup = refreshTokenRepository.findByToken("sample-refresh-uuid-token-12345");
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
}
