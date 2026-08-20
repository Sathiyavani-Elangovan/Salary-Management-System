import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService } from '../../services/analytics.service';
import { AnalyticsData } from '../../models/employee.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  analytics: AnalyticsData | null = null;
  loading = true;
  error: string | null = null;

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.loading = true;
    this.analyticsService.getOverview().subscribe({
      next: (data) => {
        this.analytics = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load analytics data';
        this.loading = false;
        console.error('Error loading analytics:', err);
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(value);
  }

  getDepartmentNames(): string[] {
    return this.analytics ? Object.keys(this.analytics.departmentBreakdown) : [];
  }

  getCountryNames(): string[] {
    return this.analytics ? Object.keys(this.analytics.countryBreakdown) : [];
  }

  getSalaryRanges(): string[] {
    return this.analytics ? Object.keys(this.analytics.salaryDistribution) : [];
  }
}
