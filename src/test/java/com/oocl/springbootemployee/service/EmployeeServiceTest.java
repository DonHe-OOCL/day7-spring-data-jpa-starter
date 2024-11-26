package com.oocl.springbootemployee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oocl.springbootemployee.exception.EmployeeAgeNotValidException;
import com.oocl.springbootemployee.exception.EmployeeAgeSalaryNotMatchedException;
import com.oocl.springbootemployee.exception.EmployeeInactiveException;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.repository.EmployeeInMemoryRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class EmployeeServiceTest {
    @Test
    void should_return_the_given_employees_when_getAllEmployees() {
        //given
        EmployeeInMemoryRepository mockedEmployeeRepository = mock(EmployeeInMemoryRepository.class);
        when(mockedEmployeeRepository.findAll()).thenReturn(List.of(new Employee(1, "Lucy", 18, Gender.FEMALE, 8000.0)));
        EmployeeService employeeService = new EmployeeService(mockedEmployeeRepository);

        //when
        List<Employee> allEmployees = employeeService.findAll();

        //then
        assertEquals(1, allEmployees.size());
        assertEquals("Lucy", allEmployees.get(0).getName());
    }

    @Test
    void should_return_the_created_employee_when_create_given_a_employee() {
        //given
        EmployeeInMemoryRepository mockedEmployeeRepository = mock(EmployeeInMemoryRepository.class);
        Employee lucy = new Employee(1, "Lucy", 18, Gender.FEMALE, 8000.0);
        when(mockedEmployeeRepository.create(any())).thenReturn(lucy);
        EmployeeService employeeService = new EmployeeService(mockedEmployeeRepository);

        //when
        Employee createdEmployee = employeeService.create(lucy);

        //then
        assertEquals("Lucy", createdEmployee.getName());
    }

    @Test
    void should_throw_EmployeeAgeNotValidException_when_create_given_a_employee_with_age_17() {
        //given
        EmployeeInMemoryRepository mockedEmployeeRepository = mock(EmployeeInMemoryRepository.class);
        Employee kitty = new Employee(1, "Kitty", 6, Gender.FEMALE, 8000.0);
        EmployeeService employeeService = new EmployeeService(mockedEmployeeRepository);
        //when
        //then
        assertThrows(EmployeeAgeNotValidException.class, () -> employeeService.create(kitty));
        verify(mockedEmployeeRepository, never()).create(any());
    }

    @Test
    void should_throw_EmployeeAgeNotValidException_when_create_given_a_employee_with_age_66() {
        //given
        EmployeeInMemoryRepository mockedEmployeeRepository = mock(EmployeeInMemoryRepository.class);
        Employee kitty = new Employee(1, "Kitty", 66, Gender.FEMALE, 8000.0);
        EmployeeService employeeService = new EmployeeService(mockedEmployeeRepository);
        //when
        //then
        assertThrows(EmployeeAgeNotValidException.class, () -> employeeService.create(kitty));
        verify(mockedEmployeeRepository, never()).create(any());
    }

    @Test
    void should_created_employee_active_when_create_employee() {
        //given
        EmployeeInMemoryRepository mockedEmployeeRepository = mock(EmployeeInMemoryRepository.class);
        EmployeeService employeeService = new EmployeeService(mockedEmployeeRepository);
        Employee lucy = new Employee(1, "Lucy", 18, Gender.FEMALE, 8000.0);
        //when
        employeeService.create(lucy);
        /* then */
        verify(mockedEmployeeRepository).create(argThat(Employee::getActive));
    }

    @Test
    void should_throw_EmployeeAgeSalaryNotMatchedException_when_save_given_a_employee_with_age_over_30_and_salary_below_20K() {
        //given
        EmployeeInMemoryRepository mockedEmployeeRepository = mock(EmployeeInMemoryRepository.class);
        Employee bob = new Employee(1, "Bob", 31, Gender.FEMALE, 8000.0);
        EmployeeService employeeService = new EmployeeService(mockedEmployeeRepository);
        //when
        //then
        assertThrows(EmployeeAgeSalaryNotMatchedException.class, () -> employeeService.create(bob));
        verify(mockedEmployeeRepository, never()).create(any());
    }

    @Test
    void should_throw_EmployeeInactiveException_when_update_inactive_employee() {
        //given
        EmployeeInMemoryRepository mockedEmployeeRepository = mock(EmployeeInMemoryRepository.class);
        Employee inactiveEmployee = new Employee(1, "Bob", 31, Gender.FEMALE, 8000.0);
        inactiveEmployee.setActive(false);
        when(mockedEmployeeRepository.findById(1)).thenReturn(inactiveEmployee);
        EmployeeService employeeService = new EmployeeService(mockedEmployeeRepository);
        //when
        //then
        assertThrows(EmployeeInactiveException.class, () -> employeeService.update(1, inactiveEmployee));
        verify(mockedEmployeeRepository, never()).create(any());
    }
}
