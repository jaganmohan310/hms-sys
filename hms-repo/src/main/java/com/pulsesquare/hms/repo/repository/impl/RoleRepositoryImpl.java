package com.pulsesquare.hms.repo.repository.impl;

import com.pulsesquare.hms.jooq.tables.pojos.HmsRoles;
import com.pulsesquare.hms.repo.repository.RoleRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.pulsesquare.hms.jooq.Tables.HMS_ROLES;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final DSLContext dsl;

    @Autowired
    public RoleRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<HmsRoles> findById(Long id) {
        if (id == null) return Optional.empty();

        HmsRoles record = dsl.selectFrom(HMS_ROLES)
                .where(HMS_ROLES.ID.eq(id))
                .fetchOneInto(HmsRoles.class);

        return Optional.ofNullable(record);
    }

    @Override
    public Optional<HmsRoles> findByName(String name) {
        if (name == null) return Optional.empty();

        HmsRoles record = dsl.selectFrom(HMS_ROLES)
                .where(HMS_ROLES.NAME.equalIgnoreCase(name.trim()))
                .fetchOneInto(HmsRoles.class);

        return Optional.ofNullable(record);
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null) return false;

        Integer count = dsl.selectCount()
                .from(HMS_ROLES)
                .where(HMS_ROLES.NAME.equalIgnoreCase(name.trim()))
                .fetchOne(0, int.class);
        return count != null && count > 0;
    }

    @Override
    public HmsRoles save(HmsRoles role) {
        LocalDateTime now = LocalDateTime.now();

        if (role.getId() == null) {
            if (role.getCreatedAt() == null) {
                role.setCreatedAt(now);
            }

            Long generatedId = null;
            try {
                org.jooq.Record1<Long> result = dsl.insertInto(HMS_ROLES)
                        .columns(HMS_ROLES.NAME, HMS_ROLES.DESCRIPTION, HMS_ROLES.CREATED_AT)
                        .values(role.getName(), role.getDescription(), role.getCreatedAt())
                        .returningResult(HMS_ROLES.ID)
                        .fetchOne();

                if (result != null) {
                    generatedId = result.value1();
                }
            } catch (Exception ex) {
                dsl.insertInto(HMS_ROLES)
                        .columns(HMS_ROLES.NAME, HMS_ROLES.DESCRIPTION, HMS_ROLES.CREATED_AT)
                        .values(role.getName(), role.getDescription(), role.getCreatedAt())
                        .execute();
            }

            if (generatedId == null) {
                generatedId = dsl.select(HMS_ROLES.ID)
                        .from(HMS_ROLES)
                        .where(HMS_ROLES.NAME.eq(role.getName()))
                        .fetchOneInto(Long.class);
            }
            role.setId(generatedId);
        } else {
            dsl.update(HMS_ROLES)
                    .set(HMS_ROLES.NAME, role.getName())
                    .set(HMS_ROLES.DESCRIPTION, role.getDescription())
                    .where(HMS_ROLES.ID.eq(role.getId()))
                    .execute();
        }

        return role;
    }
}
