package com.pulsesquare.hms.repo.repository;

import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;

import java.util.Optional;

public interface RoleRepository {

    Optional<HmsRoles> findById(Long id);

    Optional<HmsRoles> findByName(String name);

    boolean existsByName(String name);

    HmsRoles save(HmsRoles role);
}
