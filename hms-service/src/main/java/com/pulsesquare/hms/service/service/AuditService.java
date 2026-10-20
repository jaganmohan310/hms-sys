package com.pulsesquare.hms.service.service;

public interface AuditService {

    void logEvent(String action, String username, String ipAddress, String userAgent, String status, String details);
}
