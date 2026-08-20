package com.acme.salary.repository;

import com.acme.salary.model.Employee;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // Find by employee code
    Optional<Employee> findByEmployeeCode(String employeeCode);

    // Check existence
    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);

    // Search with filters
    Page<Employee> findByDepartmentAndCountry(String department, String country, Pageable pageable);
    Page<Employee> findByDepartment(String department, Pageable pageable);
    Page<Employee> findByCountry(String country, Pageable pageable);

    // Salary range queries
    Page<Employee> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable);

    // Search by name (case-insensitive)
    @Query(value = "SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))",
           countQuery = "SELECT COUNT(e) FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Employee> searchByName(String searchTerm, Pageable pageable);

    // Combined search with all filters (AND logic)
    @Query(value = "SELECT e FROM Employee e WHERE " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND e.department = :department AND e.country = :country AND e.isActive = true",
           countQuery = "SELECT COUNT(e) FROM Employee e WHERE " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND e.department = :department AND e.country = :country AND e.isActive = true")
    Page<Employee> searchByNameAndDepartmentAndCountry(String searchTerm, String department, String country, Pageable pageable);

    @Query(value = "SELECT e FROM Employee e WHERE " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND e.department = :department AND e.isActive = true",
           countQuery = "SELECT COUNT(e) FROM Employee e WHERE " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND e.department = :department AND e.isActive = true")
    Page<Employee> searchByNameAndDepartment(String searchTerm, String department, Pageable pageable);

    @Query(value = "SELECT e FROM Employee e WHERE " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND e.country = :country AND e.isActive = true",
           countQuery = "SELECT COUNT(e) FROM Employee e WHERE " +
           "(LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND e.country = :country AND e.isActive = true")
    Page<Employee> searchByNameAndCountry(String searchTerm, String country, Pageable pageable);

    // Analytics queries
    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.isActive = true")
    BigDecimal findAverageSalary();

    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.department = :department AND e.isActive = true")
    BigDecimal findAverageSalaryByDepartment(String department);

    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.country = :country AND e.isActive = true")
    BigDecimal findAverageSalaryByCountry(String country);

    @Query("SELECT MIN(e.salary) FROM Employee e WHERE e.isActive = true")
    BigDecimal findMinSalary();

    @Query("SELECT MAX(e.salary) FROM Employee e WHERE e.isActive = true")
    BigDecimal findMaxSalary();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.isActive = true")
    long countActiveEmployees();

    @Query("SELECT e.department, COUNT(e), AVG(e.salary) FROM Employee e WHERE e.isActive = true GROUP BY e.department")
    List<Object[]> findDepartmentStatistics();

    @Query("SELECT e.country, COUNT(e), AVG(e.salary) FROM Employee e WHERE e.isActive = true GROUP BY e.country")
    List<Object[]> findCountryStatistics();

    // Active employees
    Page<Employee> findByIsActiveTrue(Pageable pageable);

    // Find all active employees for analytics
    @Query("SELECT e FROM Employee e WHERE e.isActive = true")
    List<Employee> findAllActive();
}
