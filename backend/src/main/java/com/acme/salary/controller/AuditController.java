package com.acme.salary.controller;

import com.acme.salary.model.AuditLog;
import com.acme.salary.service.AuditService;
import io.micronaut.data.model.Page;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.List;

@Controller("/api/audit")
public class AuditController {

    @Inject
    private AuditService auditService;

    @Get
    public Page<AuditLog> getAllAuditLogs(@QueryValue(defaultValue = "0") int page,
                                           @QueryValue(defaultValue = "20") int size) {
        return auditService.getAllAuditLogs(page, size);
    }

    @Get("/entity/{entityType}/{entityId}")
    public List<AuditLog> getAuditLogsByEntity(@PathVariable String entityType, @PathVariable Long entityId) {
        return auditService.getAuditLogsByEntity(entityType, entityId);
    }

    @Get("/user/{userId}")
    public List<AuditLog> getAuditLogsByUser(@PathVariable Long userId) {
        return auditService.getAuditLogsByUser(userId);
    }

    @Get("/action/{action}")
    public Page<AuditLog> getAuditLogsByAction(@PathVariable String action,
                                                @QueryValue(defaultValue = "0") int page,
                                                @QueryValue(defaultValue = "20") int size) {
        return auditService.getAuditLogsByAction(action, page, size);
    }
}
