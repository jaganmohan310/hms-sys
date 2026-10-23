package com.pulsesquare.hms.repo.repository.impl;

import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;
import com.pulsesquare.hms.jooq.tables.pojos.HmsUsers;
import com.pulsesquare.hms.repo.repository.RoleRepository;
import com.pulsesquare.hms.repo.repository.UserRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.pulsesquare.hms.jooq.Tables.HMS_USERS;
import static com.pulsesquare.hms.jooq.Tables.HMS_USER_ROLES;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dsl;
    private final RoleRepository roleRepository;

    @Autowired
    public UserRepositoryImpl(DSLContext dsl, RoleRepository roleRepository) {
        this.dsl = dsl;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<HmsUsers> findById(Long id) {
        if (id == null) return Optional.empty();
        HmsUsers record = dsl.selectFrom(HMS_USERS)
                .where(HMS_USERS.ID.eq(id))
                .fetchOneInto(HmsUsers.class);

        return Optional.ofNullable(record);
    }

    @Override
    public Optional<HmsUsers> findByUsername(String username) {
        if (username == null) return Optional.empty();

        HmsUsers record = dsl.selectFrom(HMS_USERS)
                .where(HMS_USERS.USERNAME.equalIgnoreCase(username.trim()))
                .fetchOneInto(HmsUsers.class);

        return Optional.ofNullable(record);
    }

    @Override
    public Optional<HmsUsers> findByEmail(String email) {
        if (email == null) return Optional.empty();

        HmsUsers record = dsl.selectFrom(HMS_USERS)
                .where(HMS_USERS.EMAIL.equalIgnoreCase(email.trim()))
                .fetchOneInto(HmsUsers.class);

        return Optional.ofNullable(record);
    }

    @Override
    public Optional<HmsUsers> findByUsernameOrEmail(String username, String email) {
        String identifier = username != null ? username.trim() : "";
        String emailId = email != null ? email.trim() : identifier;

        HmsUsers record = dsl.selectFrom(HMS_USERS)
                .where(HMS_USERS.USERNAME.equalIgnoreCase(identifier)
                        .or(HMS_USERS.EMAIL.equalIgnoreCase(emailId)))
                .fetchOneInto(HmsUsers.class);

        return Optional.ofNullable(record);
    }

    @Override
    public boolean existsByUsername(String username) {
        if (username == null) return false;
        Integer count = dsl.selectCount()
                .from(HMS_USERS)
                .where(HMS_USERS.USERNAME.equalIgnoreCase(username.trim()))
                .fetchOne(0, int.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        Integer count = dsl.selectCount()
                .from(HMS_USERS)
                .where(HMS_USERS.EMAIL.equalIgnoreCase(email.trim()))
                .fetchOne(0, int.class);
        return count != null && count > 0;
    }

    @Override
    public HmsUsers save(HmsUsers user) {
        return save(user, null);
    }

    @Override
    public HmsUsers save(HmsUsers user, Set<HmsRoles> roles) {
        LocalDateTime now = LocalDateTime.now();

        if (user.getId() == null) {
            // INSERT via jOOQ
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(now);
            }
            user.setUpdatedAt(now);

            Long generatedId = null;
            try {
                org.jooq.Record1<Long> result = dsl.insertInto(HMS_USERS)
                        .columns(HMS_USERS.USERNAME, HMS_USERS.EMAIL, HMS_USERS.PASSWORD_HASH, HMS_USERS.FULL_NAME,
                                HMS_USERS.CONTACT_NUMBER, HMS_USERS.DEPARTMENT, HMS_USERS.ACCOUNT_ENABLED,
                                HMS_USERS.ACCOUNT_LOCKED, HMS_USERS.FAILED_LOGIN_ATTEMPTS, HMS_USERS.LAST_LOGIN_AT,
                                HMS_USERS.CREATED_AT, HMS_USERS.UPDATED_AT)
                        .values(user.getUsername(), user.getEmail(), user.getPasswordHash(), user.getFullName(),
                                user.getContactNumber(), user.getDepartment(), user.getAccountEnabled(),
                                user.getAccountLocked(), user.getFailedLoginAttempts(), user.getLastLoginAt(),
                                user.getCreatedAt(), user.getUpdatedAt())
                        .returningResult(HMS_USERS.ID)
                        .fetchOne();

                if (result != null) {
                    generatedId = result.value1();
                }
            } catch (Exception ex) {
                dsl.insertInto(HMS_USERS)
                        .columns(HMS_USERS.USERNAME, HMS_USERS.EMAIL, HMS_USERS.PASSWORD_HASH, HMS_USERS.FULL_NAME,
                                HMS_USERS.CONTACT_NUMBER, HMS_USERS.DEPARTMENT, HMS_USERS.ACCOUNT_ENABLED,
                                HMS_USERS.ACCOUNT_LOCKED, HMS_USERS.FAILED_LOGIN_ATTEMPTS, HMS_USERS.LAST_LOGIN_AT,
                                HMS_USERS.CREATED_AT, HMS_USERS.UPDATED_AT)
                        .values(user.getUsername(), user.getEmail(), user.getPasswordHash(), user.getFullName(),
                                user.getContactNumber(), user.getDepartment(), user.getAccountEnabled(),
                                user.getAccountLocked(), user.getFailedLoginAttempts(), user.getLastLoginAt(),
                                user.getCreatedAt(), user.getUpdatedAt())
                        .execute();
            }

            if (generatedId == null) {
                generatedId = dsl.select(HMS_USERS.ID)
                        .from(HMS_USERS)
                        .where(HMS_USERS.USERNAME.eq(user.getUsername()))
                        .fetchOneInto(Long.class);
            }

            user.setId(generatedId);

            // Save role associations
            if (roles != null && !roles.isEmpty()) {
                for (HmsRoles role : roles) {
                    if (role.getId() != null) {
                        assignRole(user.getId(), role.getId());
                    }
                }
            }
        } else {
            // UPDATE via jOOQ
            user.setUpdatedAt(now);
            dsl.update(HMS_USERS)
                    .set(HMS_USERS.PASSWORD_HASH, user.getPasswordHash())
                    .set(HMS_USERS.FULL_NAME, user.getFullName())
                    .set(HMS_USERS.CONTACT_NUMBER, user.getContactNumber())
                    .set(HMS_USERS.DEPARTMENT, user.getDepartment())
                    .set(HMS_USERS.ACCOUNT_ENABLED, user.getAccountEnabled())
                    .set(HMS_USERS.ACCOUNT_LOCKED, user.getAccountLocked())
                    .set(HMS_USERS.FAILED_LOGIN_ATTEMPTS, user.getFailedLoginAttempts())
                    .set(HMS_USERS.LAST_LOGIN_AT, user.getLastLoginAt())
                    .set(HMS_USERS.UPDATED_AT, user.getUpdatedAt())
                    .where(HMS_USERS.ID.eq(user.getId()))
                    .execute();
        }

        return user;
    }

    @Override
    public void assignRole(Long userId, Long roleId) {
        Integer count = dsl.selectCount()
                .from(HMS_USER_ROLES)
                .where(HMS_USER_ROLES.USER_ID.eq(userId).and(HMS_USER_ROLES.ROLE_ID.eq(roleId)))
                .fetchOne(0, int.class);

        if (count == null || count == 0) {
            dsl.insertInto(HMS_USER_ROLES)
                    .columns(HMS_USER_ROLES.USER_ID, HMS_USER_ROLES.ROLE_ID)
                    .values(userId, roleId)
                    .execute();
        }
    }

    @Override
    public Set<HmsRoles> findRolesByUserId(Long userId) {
        List<Long> roleIds = dsl.select(HMS_USER_ROLES.ROLE_ID)
                .from(HMS_USER_ROLES)
                .where(HMS_USER_ROLES.USER_ID.eq(userId))
                .fetchInto(Long.class);

        Set<HmsRoles> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            roleRepository.findById(roleId).ifPresent(roles::add);
        }
        return roles;
    }
}
