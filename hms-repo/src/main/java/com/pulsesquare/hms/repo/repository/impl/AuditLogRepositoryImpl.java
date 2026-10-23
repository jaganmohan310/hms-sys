package com.pulsesquare.hms.repo.repository.impl;

import com.pulsesquare.hms.jooq.tables.pojos.HmsAuditLogs;
import com.pulsesquare.hms.repo.repository.AuditLogRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.pulsesquare.hms.jooq.Tables.HMS_AUDIT_LOGS;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final DSLContext dsl;

    @Autowired
    public AuditLogRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public HmsAuditLogs save(HmsAuditLogs auditLog) {
        LocalDateTime now = LocalDateTime.now();
        if (auditLog.getCreatedAt() == null) {
            auditLog.setCreatedAt(now);
        }

        Long generatedId = null;
        try {
            org.jooq.Record1<Long> result = dsl.insertInto(HMS_AUDIT_LOGS)
                    .columns(HMS_AUDIT_LOGS.ACTION, HMS_AUDIT_LOGS.USERNAME, HMS_AUDIT_LOGS.IP_ADDRESS,
                            HMS_AUDIT_LOGS.USER_AGENT, HMS_AUDIT_LOGS.STATUS, HMS_AUDIT_LOGS.DETAILS,
                            HMS_AUDIT_LOGS.CREATED_AT)
                    .values(auditLog.getAction(), auditLog.getUsername(), auditLog.getIpAddress(),
                            auditLog.getUserAgent(), auditLog.getStatus(), auditLog.getDetails(),
                            auditLog.getCreatedAt())
                    .returningResult(HMS_AUDIT_LOGS.ID)
                    .fetchOne();

            if (result != null) {
                generatedId = result.value1();
            }
        } catch (Exception ex) {
            dsl.insertInto(HMS_AUDIT_LOGS)
                    .columns(HMS_AUDIT_LOGS.ACTION, HMS_AUDIT_LOGS.USERNAME, HMS_AUDIT_LOGS.IP_ADDRESS,
                            HMS_AUDIT_LOGS.USER_AGENT, HMS_AUDIT_LOGS.STATUS, HMS_AUDIT_LOGS.DETAILS,
                            HMS_AUDIT_LOGS.CREATED_AT)
                    .values(auditLog.getAction(), auditLog.getUsername(), auditLog.getIpAddress(),
                            auditLog.getUserAgent(), auditLog.getStatus(), auditLog.getDetails(),
                            auditLog.getCreatedAt())
                    .execute();
        }

        if (generatedId != null) {
            auditLog.setId(generatedId);
        }

        return auditLog;
    }

    @Override
    public List<HmsAuditLogs> findTop50() {
        return dsl.selectFrom(HMS_AUDIT_LOGS)
                .orderBy(HMS_AUDIT_LOGS.CREATED_AT.desc())
                .limit(50)
                .fetchInto(HmsAuditLogs.class);
    }
}
