package com.acme.salary.service;

import com.acme.salary.dto.EmployeeDTO;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.EmployeeRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
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
        // Validate unique constraints
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Employee with email " + dto.getEmail() + " already exists");
        }
        if (employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            throw new IllegalArgumentException("Employee with code " + dto.getEmployeeCode() + " already exists");
        }

        Employee employee = mapToEntity(dto);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(UUID id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));

        // Check email uniqueness if changed
        if (!employee.getEmail().equals(dto.getEmail()) && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email " + dto.getEmail() + " is already in use");
        }

        // Update fields
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
        employee.setIsActive(dto.getIsActive());

        return employeeRepository.update(employee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
        
        // Soft delete
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
        boolean hasSearchTerm = searchTerm != null && !searchTerm.isEmpty();
        boolean hasDepartment = department != null && !department.isEmpty();
        boolean hasCountry = country != null && !country.isEmpty();
        
        // Combined filters with AND logic
        if (hasSearchTerm && hasDepartment && hasCountry) {
            return employeeRepository.searchByNameAndDepartmentAndCountry(searchTerm, department, country, pageable);
        } else if (hasSearchTerm && hasDepartment) {
            return employeeRepository.searchByNameAndDepartment(searchTerm, department, pageable);
        } else if (hasSearchTerm && hasCountry) {
            return employeeRepository.searchByNameAndCountry(searchTerm, country, pageable);
        } else if (hasSearchTerm) {
            return employeeRepository.searchByName(searchTerm, pageable);
        } else if (hasDepartment && hasCountry) {
            return employeeRepository.findByDepartmentAndCountry(department, country, pageable);
        } else if (hasDepartment) {
            return employeeRepository.findByDepartment(department, pageable);
        } else if (hasCountry) {
            return employeeRepository.findByCountry(country, pageable);
        } else if (minSalary != null && maxSalary != null) {
            return employeeRepository.findBySalaryBetween(minSalary, maxSalary, pageable);
        } else {
            return findAll(pageable);
        }
    }

    public List<Employee> findAllActive() {
        return employeeRepository.findAllActive();
    }

    @Transactional
    public void bulkSalaryAdjustment(List<UUID> employeeIds, BigDecimal percentage) {
        if (percentage.compareTo(new BigDecimal("-100")) < 0) {
            throw new IllegalArgumentException("Salary adjustment cannot reduce salary by more than 100%");
        }

        for (UUID id : employeeIds) {
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with id: " + id));
            
            BigDecimal adjustment = employee.getSalary().multiply(percentage.divide(new BigDecimal("100")));
            BigDecimal newSalary = employee.getSalary().add(adjustment);
            
            if (newSalary.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Salary adjustment would result in negative or zero salary for employee: " + employee.getEmployeeCode());
            }
            
            employee.setSalary(newSalary);
            employeeRepository.update(employee);
        }
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
