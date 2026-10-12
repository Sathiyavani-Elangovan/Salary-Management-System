package com.acme.salary.service;

import com.acme.salary.dto.EmployeeDTO;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.EmployeeRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Employee createEmployee(EmployeeDTO dto) {
        EmployeeDTO normalized = normalizeEmployeeInput(dto);
        validateEmployeeInput(normalized);

        if (employeeRepository.existsByEmail(normalized.getEmail())) {
            throw new IllegalArgumentException("Employee with email " + normalized.getEmail() + " already exists");
        }
        if (employeeRepository.existsByEmployeeCode(normalized.getEmployeeCode())) {
            throw new IllegalArgumentException("Employee with code " + normalized.getEmployeeCode() + " already exists");
        }

        Employee employee = mapToEntity(normalized);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(UUID id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

        EmployeeDTO normalized = normalizeEmployeeInput(dto);
        validateEmployeeInput(normalized);

        if ((employee.getEmail() == null || !employee.getEmail().equalsIgnoreCase(normalized.getEmail()))
            && employeeRepository.existsByEmail(normalized.getEmail())) {
            throw new IllegalArgumentException("Email " + normalized.getEmail() + " is already in use");
        }

        employee.setFirstName(normalized.getFirstName());
        employee.setLastName(normalized.getLastName());
        employee.setEmail(normalized.getEmail());
        employee.setDepartment(normalized.getDepartment());
        employee.setCountry(normalized.getCountry());
        employee.setJobTitle(normalized.getJobTitle());
        employee.setSalary(normalized.getSalary());
        employee.setCurrency(normalized.getCurrency());
        employee.setDateJoined(normalized.getDateJoined());
        employee.setGender(normalized.getGender());
        employee.setExperienceYears(normalized.getExperienceYears());
        employee.setPerformanceRating(normalized.getPerformanceRating());
        employee.setIsActive(normalized.getIsActive() != null ? normalized.getIsActive() : true);

        return employeeRepository.update(employee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

        employee.setIsActive(false);
        employeeRepository.update(employee);
    }

    public Optional<Employee> findById(UUID id) {
        return employeeRepository.findById(id);
    }

    public Page<Employee> findAll(Pageable pageable) {
        return employeeRepository.findByIsActiveTrue(pageable);
    }

    public Page<Employee> searchEmployees(String searchTerm, String department, String country,
                                          BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable) {
        if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot exceed maximum salary");
        }

        return employeeRepository.searchActiveEmployees(
                blankToNull(searchTerm), blankToNull(department), blankToNull(country), minSalary, maxSalary, pageable);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public List<Employee> findAllActive() {
        return employeeRepository.findAllActive();
    }

    @Transactional
    public void bulkSalaryAdjustment(List<UUID> employeeIds, BigDecimal percentage) {
        if (percentage == null) {
            throw new IllegalArgumentException("Salary adjustment percentage is required");
        }
        if (percentage.compareTo(new BigDecimal("-100")) < 0) {
            throw new IllegalArgumentException("Salary adjustment cannot reduce salary by more than 100%");
        }

        for (UUID id : employeeIds) {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

            BigDecimal adjustment = employee.getSalary().multiply(percentage.divide(new BigDecimal("100")));
            BigDecimal newSalary = employee.getSalary().add(adjustment).setScale(2, RoundingMode.HALF_UP);

            if (newSalary.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Salary adjustment would result in negative or zero salary for employee: " + employee.getEmployeeCode());
            }

            employee.setSalary(newSalary);
            employeeRepository.update(employee);
        }
    }

    private EmployeeDTO normalizeEmployeeInput(EmployeeDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Employee details are required");
        }

        EmployeeDTO normalized = new EmployeeDTO();
        normalized.setId(dto.getId());
        normalized.setEmployeeCode(normalizeRequired(dto.getEmployeeCode(), "Employee code"));
        normalized.setFirstName(normalizeName(dto.getFirstName(), "First name"));
        normalized.setLastName(normalizeName(dto.getLastName(), "Last name"));
        normalized.setEmail(normalizeEmail(dto.getEmail()));
        normalized.setDepartment(normalizeRequired(dto.getDepartment(), "Department"));
        normalized.setCountry(normalizeRequired(dto.getCountry(), "Country"));
        normalized.setJobTitle(normalizeRequired(dto.getJobTitle(), "Job title"));
        normalized.setCurrency(normalizeRequired(dto.getCurrency(), "Currency").toUpperCase(Locale.ROOT));
        normalized.setDateJoined(dto.getDateJoined());
        normalized.setGender(normalizeRequired(dto.getGender(), "Gender"));
        normalized.setExperienceYears(dto.getExperienceYears());
        normalized.setPerformanceRating(normalizeRequired(dto.getPerformanceRating(), "Performance rating"));
        normalized.setIsActive(dto.getIsActive());

        if (dto.getSalary() == null) {
            throw new IllegalArgumentException("Salary is required");
        }
        normalized.setSalary(dto.getSalary().setScale(2, RoundingMode.HALF_UP));

        return normalized;
    }

    private void validateEmployeeInput(EmployeeDTO dto) {
        validateRequiredField(dto.getEmployeeCode(), "Employee code");
        validateRequiredField(dto.getFirstName(), "First name");
        validateRequiredField(dto.getLastName(), "Last name");
        validateRequiredField(dto.getEmail(), "Email");
        validateRequiredField(dto.getDepartment(), "Department");
        validateRequiredField(dto.getCountry(), "Country");
        validateRequiredField(dto.getJobTitle(), "Job title");
        validateRequiredField(dto.getCurrency(), "Currency");
        validateRequiredField(dto.getGender(), "Gender");
        validateRequiredField(dto.getPerformanceRating(), "Performance rating");
        validateLength(dto.getEmployeeCode(), 3, 20, "Employee code");
        validateLength(dto.getFirstName(), 2, 50, "First name");
        validateLength(dto.getLastName(), 2, 50, "Last name");
        validateLength(dto.getCurrency(), 3, 3, "Currency");

        if (dto.getSalary() == null || dto.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than zero");
        }

        if (dto.getDateJoined() == null) {
            throw new IllegalArgumentException("Date joined is required");
        }
        if (dto.getDateJoined().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date joined cannot be in the future");
        }

        if (dto.getExperienceYears() == null || dto.getExperienceYears() < 0 || dto.getExperienceYears() > 50) {
            throw new IllegalArgumentException("Experience years must be between 0 and 50");
        }
    }

    private void validateLength(String value, int minLength, int maxLength, String fieldName) {
        if (value.length() < minLength || value.length() > maxLength) {
            throw new IllegalArgumentException(
                    fieldName + " must be between " + minLength + " and " + maxLength + " characters");
        }
    }

    private void validateRequiredField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private String normalizeRequired(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return normalized;
    }

    private String normalizeName(String value, String fieldName) {
        String normalized = normalizeRequired(value, fieldName);
        return normalized.replaceAll("\\s+", " ");
    }

    private String normalizeEmail(String email) {
        String normalized = normalizeRequired(email, "Email");
        return normalized.trim().toLowerCase(Locale.ROOT);
    }

    private Employee mapToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setCountry(dto.getCountry());
        employee.setJobTitle(dto.getJobTitle());
        employee.setSalary(dto.getSalary());
        employee.setCurrency(dto.getCurrency());
        employee.setDateJoined(dto.getDateJoined());
        employee.setGender(dto.getGender());
        employee.setExperienceYears(dto.getExperienceYears());
        employee.setPerformanceRating(dto.getPerformanceRating());
        employee.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return employee;
    }
}

