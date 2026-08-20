package com.acme.salary.service;

import com.acme.salary.model.AuditLog;
import com.acme.salary.repository.AuditLogRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class AuditService {

    @Inject
    private AuditLogRepository auditLogRepository;

    public AuditLog logAction(String action, String entityType, Long entityId, Long userId, String username, 
                              String oldValue, String newValue, String ipAddress) {
        AuditLog log = new AuditLog(action, entityType, entityId, userId, username);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setIpAddress(ipAddress);
        return auditLogRepository.save(log);
    }

    public Page<AuditLog> getAllAuditLogs(int page, int size) {
        return auditLogRepository.findAll(Pageable.from(page, size));
    }

    public List<AuditLog> getAuditLogsByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public Page<AuditLog> getAuditLogsByAction(String action, int page, int size) {
        return auditLogRepository.findByAction(action, Pageable.from(page, size));
    }
}
