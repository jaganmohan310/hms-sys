package com.pulsesquare.hms.repo.repository;

import com.pulsesquare.hms.jooq.tables.pojos.HmsAuditLogs;

import java.util.List;

public interface AuditLogRepository {

    HmsAuditLogs save(HmsAuditLogs auditLog);

    List<HmsAuditLogs> findTop50();
}
