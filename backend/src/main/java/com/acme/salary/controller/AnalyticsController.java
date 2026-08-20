package com.acme.salary.controller;

import com.acme.salary.dto.AnalyticsDTO;
import com.acme.salary.service.AnalyticsService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Get("/overview")
    public AnalyticsDTO getOverview() {
        return analyticsService.getOverviewAnalytics();
    }
}
