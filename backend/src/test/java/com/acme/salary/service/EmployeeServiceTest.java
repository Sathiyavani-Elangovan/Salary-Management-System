package com.acme.salary.service;

import com.acme.salary.dto.EmployeeDTO;
import com.acme.salary.model.Employee;
import com.acme.salary.repository.EmployeeRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MicronautTest
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    void createEmployee_WithValidData_ReturnsCreatedEmployee() {
        // Arrange
        EmployeeDTO dto = createValidEmployeeDTO();
        Employee employee = createValidEmployee();
        
        when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        Employee result = employeeService.createEmployee(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getEmail(), result.getEmail());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_WithDuplicateEmail_ThrowsException() {
        // Arrange
        EmployeeDTO dto = createValidEmployeeDTO();
        when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> employeeService.createEmployee(dto)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void createEmployee_WithDuplicateEmployeeCode_ThrowsException() {
        // Arrange
        EmployeeDTO dto = createValidEmployeeDTO();
        when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(employeeRepository.existsByEmployeeCode(dto.getEmployeeCode())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> employeeService.createEmployee(dto)
        );
        
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void updateEmployee_WithValidData_UpdatesEmployee() {
        // Arrange
        UUID id = UUID.randomUUID();
        EmployeeDTO dto = createValidEmployeeDTO();
        Employee existingEmployee = createValidEmployee();
        existingEmployee.setId(id);
        
        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(employeeRepository.update(any(Employee.class))).thenReturn(existingEmployee);

        // Act
        Employee result = employeeService.updateEmployee(id, dto);

        // Assert
        assertNotNull(result);
        verify(employeeRepository).update(any(Employee.class));
    }

    @Test
    void updateEmployee_WithNonExistentId_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();
        EmployeeDTO dto = createValidEmployeeDTO();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(id, dto));
    }

    @Test
    void createEmployee_WithWhitespaceInput_NormalizesValues() {
        EmployeeDTO dto = createValidEmployeeDTO();
        dto.setEmployeeCode(" EMP001 ");
        dto.setFirstName("  john   smith  ");
        dto.setLastName("  doe  ");
        dto.setEmail("  JOHN.DOE@ACME.COM  ");
        dto.setCurrency(" usd ");

        when(employeeRepository.existsByEmail("john.doe@acme.com")).thenReturn(false);
        when(employeeRepository.existsByEmployeeCode("EMP001")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        employeeService.createEmployee(dto);

        ArgumentCaptor<Employee> savedEmployee = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(savedEmployee.capture());
        assertEquals("EMP001", savedEmployee.getValue().getEmployeeCode());
        assertEquals("john smith", savedEmployee.getValue().getFirstName());
        assertEquals("doe", savedEmployee.getValue().getLastName());
        assertEquals("john.doe@acme.com", savedEmployee.getValue().getEmail());
        assertEquals("USD", savedEmployee.getValue().getCurrency());
        assertEquals(new BigDecimal("100000.00"), savedEmployee.getValue().getSalary());
        assertEquals("  john   smith  ", dto.getFirstName());
        assertEquals("  JOHN.DOE@ACME.COM  ", dto.getEmail());
        assertEquals(" usd ", dto.getCurrency());
        assertEquals(" EMP001 ", dto.getEmployeeCode());
    }

    @Test
    void createEmployee_WithExperienceAboveLimit_ThrowsException() {
        EmployeeDTO dto = createValidEmployeeDTO();
        dto.setExperienceYears(80);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.createEmployee(dto)
        );

        assertEquals("Experience years must be between 0 and 50", exception.getMessage());
        assertEquals(80, dto.getExperienceYears());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void createEmployee_WithNameTooShortAfterNormalization_ThrowsException() {
        EmployeeDTO dto = createValidEmployeeDTO();
        dto.setFirstName(" J ");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.createEmployee(dto)
        );

        assertEquals("First name must be between 2 and 50 characters", exception.getMessage());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void createEmployee_WithEmployeeCodeTooShortAfterNormalization_ThrowsException() {
        EmployeeDTO dto = createValidEmployeeDTO();
        dto.setEmployeeCode(" AB ");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.createEmployee(dto)
        );

        assertEquals("Employee code must be between 3 and 20 characters", exception.getMessage());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void updateEmployee_WithUnchangedEmailDifferentCase_DoesNotCheckDuplicate() {
        UUID id = UUID.randomUUID();
        EmployeeDTO dto = createValidEmployeeDTO();
        dto.setEmail("JOHN.DOE@ACME.COM");
        Employee existingEmployee = createValidEmployee();
        existingEmployee.setId(id);
        existingEmployee.setEmail("John.Doe@Acme.com");

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.update(any(Employee.class))).thenReturn(existingEmployee);

        employeeService.updateEmployee(id, dto);

        verify(employeeRepository, never()).existsByEmail(any());
    }

    @Test
    void updateEmployee_WithFutureDateJoined_ThrowsException() {
        UUID id = UUID.randomUUID();
        EmployeeDTO dto = createValidEmployeeDTO();
        dto.setDateJoined(LocalDate.now().plusDays(1));
        Employee existingEmployee = createValidEmployee();
        existingEmployee.setId(id);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.updateEmployee(id, dto)
        );

        assertTrue(exception.getMessage().contains("Date joined cannot be in the future"));
    }

    @Test
    void searchEmployees_WithMinimumSalaryAboveMaximum_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> employeeService.searchEmployees(null, null, null,
                new BigDecimal("90000"), new BigDecimal("80000"), Pageable.from(0, 20))
        );

        assertEquals("Minimum salary cannot exceed maximum salary", exception.getMessage());
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void deleteEmployee_WithValidId_SoftDeletesEmployee() {
        // Arrange
        UUID id = UUID.randomUUID();
        Employee employee = createValidEmployee();
        employee.setId(id);
        
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.update(any(Employee.class))).thenReturn(employee);

        // Act
        employeeService.deleteEmployee(id);

        // Assert
        assertFalse(employee.getIsActive());
        verify(employeeRepository).update(employee);
    }

    @Test
    void bulkSalaryAdjustment_WithValidPercentage_AdjustsSalaries() {
        // Arrange
        UUID id = UUID.randomUUID();
        Employee employee = createValidEmployee();
        employee.setId(id);
        employee.setSalary(new BigDecimal("1000.01"));
        
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
        when(employeeRepository.update(any(Employee.class))).thenReturn(employee);

        // Act
        employeeService.bulkSalaryAdjustment(java.util.List.of(id), new BigDecimal("3.333"));

        // Assert
        assertEquals(new BigDecimal("1033.34"), employee.getSalary());
        verify(employeeRepository).update(employee);
    }

    @Test
    void bulkSalaryAdjustment_WithNegativeSalaryResult_ThrowsException() {
        // Arrange
        UUID id = UUID.randomUUID();
        Employee employee = createValidEmployee();
        employee.setId(id);
        employee.setSalary(new BigDecimal("50000"));
        
        when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class, 
            () -> employeeService.bulkSalaryAdjustment(java.util.List.of(id), new BigDecimal("-110"))
        );
    }

    // Helper methods
    private EmployeeDTO createValidEmployeeDTO() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setEmployeeCode("EMP001");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@acme.com");
        dto.setDepartment("Engineering");
        dto.setCountry("United States");
        dto.setJobTitle("Software Engineer");
        dto.setSalary(new BigDecimal("100000"));
        dto.setCurrency("USD");
        dto.setDateJoined(LocalDate.now().minusYears(2));
        dto.setGender("Male");
        dto.setExperienceYears(5);
        dto.setPerformanceRating("Meets Expectations");
        dto.setIsActive(true);
        return dto;
    }

    private Employee createValidEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@acme.com");
        employee.setDepartment("Engineering");
        employee.setCountry("United States");
        employee.setJobTitle("Software Engineer");
        employee.setSalary(new BigDecimal("100000"));
        employee.setCurrency("USD");
        employee.setDateJoined(LocalDate.now().minusYears(2));
        employee.setGender("Male");
        employee.setExperienceYears(5);
        employee.setPerformanceRating("Meets Expectations");
        employee.setIsActive(true);
        return employee;
    }
}
