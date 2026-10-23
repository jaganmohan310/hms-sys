package com.pulsesquare.hms.service.impl;

import com.pulsesquare.hms.jooq.tables.pojos.HmsAuditLogs;
import com.pulsesquare.hms.repo.repository.AuditLogRepository;
import com.pulsesquare.hms.service.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void logEvent(String action, String username, String ipAddress, String userAgent, String status, String details) {
        try {
            HmsAuditLogs auditLog = buildAuditLogEntity(action, username, ipAddress, userAgent, status, details);
            auditLogRepository.save(auditLog);
            logger.debug("Audit Log Recorded: {} | User: {} | Status: {}", action, username, status);
        } catch (Exception e) {
            logger.warn("Could not persist audit log: {}", e.getMessage());
        }
    }

    /**
     * Helper method to construct and populate an audit log entity.
     */
    private HmsAuditLogs buildAuditLogEntity(String action, String username, String ipAddress,
                                             String userAgent, String status, String details) {
        HmsAuditLogs auditLog = new HmsAuditLogs();
        populateAuditLogAttributes(auditLog, action, username, ipAddress, userAgent, status, details);
        return auditLog;
    }

    /**
     * Helper method encapsulating setter calls for audit log fields.
     */
    private void populateAuditLogAttributes(HmsAuditLogs auditLog, String action, String username,
                                            String ipAddress, String userAgent, String status, String details) {
        auditLog.setAction(action);
        auditLog.setUsername(username);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setStatus(status);
        auditLog.setDetails(details);
        auditLog.setCreatedAt(LocalDateTime.now());
    }
}
