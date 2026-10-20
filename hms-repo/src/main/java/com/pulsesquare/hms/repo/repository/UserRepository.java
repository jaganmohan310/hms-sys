package com.pulsesquare.hms.repo.repository;

import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;
import com.pulsesquare.hms.jooq.tables.pojos.HmsUsers;

import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    Optional<HmsUsers> findById(Long id);

    Optional<HmsUsers> findByUsername(String username);

    Optional<HmsUsers> findByEmail(String email);

    Optional<HmsUsers> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    HmsUsers save(HmsUsers user);

    HmsUsers save(HmsUsers user, Set<HmsRoles> roles);

    void assignRole(Long userId, Long roleId);

    Set<HmsRoles> findRolesByUserId(Long userId);
}
