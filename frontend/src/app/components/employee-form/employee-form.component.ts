import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { Employee } from '../../models/employee.model';

@Component({
  selector: 'app-employee-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.scss']
})
export class EmployeeFormComponent implements OnInit {
  employeeForm: FormGroup;
  isEditMode = false;
  employeeId: string | null = null;
  loading = false;
  error: string | null = null;

  departments = ['Engineering', 'Sales', 'Marketing', 'Finance', 'Human Resources', 'Operations', 'Customer Support', 'Product Management', 'Legal'];
  countries = ['United States', 'India', 'United Kingdom', 'Canada', 'Germany', 'Singapore', 'Australia', 'France', 'Netherlands'];
  genders = ['Male', 'Female', 'Non-Binary'];
  performanceRatings = ['Outstanding', 'Exceeds Expectations', 'Meets Expectations', 'Needs Improvement'];
  currencies = ['USD', 'INR', 'GBP', 'CAD', 'EUR', 'SGD', 'AUD'];

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.employeeForm = this.fb.group({
      employeeCode: ['', [Validators.required, Validators.minLength(3)]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      department: ['', Validators.required],
      country: ['', Validators.required],
      jobTitle: ['', Validators.required],
      salary: ['', [Validators.required, Validators.min(0)]],
      currency: ['USD', Validators.required],
      dateJoined: ['', Validators.required],
      gender: ['', Validators.required],
      experienceYears: ['', [Validators.required, Validators.min(0), Validators.max(50)]],
      performanceRating: ['', Validators.required],
      isActive: [true]
    });
  }

  ngOnInit(): void {
    this.employeeId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = this.route.snapshot.url[this.route.snapshot.url.length - 1].path === 'edit';

    if (this.employeeId && this.isEditMode) {
      this.loadEmployee();
    }
  }

  loadEmployee(): void {
    if (!this.employeeId) return;

    this.loading = true;
    this.employeeService.getEmployee(this.employeeId).subscribe({
      next: (employee) => {
        this.employeeForm.patchValue({
          employeeCode: employee.employeeCode,
          firstName: employee.firstName,
          lastName: employee.lastName,
          email: employee.email,
          department: employee.department,
          country: employee.country,
          jobTitle: employee.jobTitle,
          salary: employee.salary,
          currency: employee.currency,
          dateJoined: employee.dateJoined,
          gender: employee.gender,
          experienceYears: employee.experienceYears,
          performanceRating: employee.performanceRating,
          isActive: employee.isActive
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load employee';
        this.loading = false;
        console.error('Error loading employee:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.employeeForm.invalid) {
      this.markFormGroupTouched(this.employeeForm);
      return;
    }

    this.loading = true;
    this.error = null;

    const employeeData: Employee = this.employeeForm.value;

    const operation = this.isEditMode && this.employeeId
      ? this.employeeService.updateEmployee(this.employeeId, employeeData)
      : this.employeeService.createEmployee(employeeData);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/employees']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to save employee';
        this.loading = false;
        console.error('Error saving employee:', err);
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/employees']);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.employeeForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  getFieldError(fieldName: string): string {
    const field = this.employeeForm.get(fieldName);
    if (!field) return '';

    if (field.hasError('required')) return `${fieldName} is required`;
    if (field.hasError('email')) return 'Invalid email format';
    if (field.hasError('minlength')) return `Minimum length not met`;
    if (field.hasError('min')) return 'Value must be positive';
    if (field.hasError('max')) return 'Value exceeds maximum';

    return '';
  }
}
