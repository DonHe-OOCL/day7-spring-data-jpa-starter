package com.oocl.springbootemployee.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.oocl.springbootemployee.exception.EmployeeInactiveException;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;

import java.util.List;

import com.oocl.springbootemployee.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class EmployeeControllerTest {

    @Autowired
    private MockMvc client;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JacksonTester<List<Employee>> employeesJacksonTester;

    @Autowired
    private JacksonTester<Employee> employeeJacksonTester;

    @BeforeEach
    void setUp() {
        givenDataToJpaRepository();
    }

    private void givenDataToJpaRepository() {
        employeeRepository.deleteAll();
        employeeRepository.save(new Employee(null, "John Smith", 32, Gender.MALE, 5000.0));
        employeeRepository.save(new Employee(null, "Jane Johnson", 28, Gender.FEMALE, 6000.0));
        employeeRepository.save(new Employee(null, "David Williams", 35, Gender.MALE, 5500.0));
        employeeRepository.save(new Employee(null, "Emily Brown", 23, Gender.FEMALE, 4500.0));
        employeeRepository.save(new Employee(null, "Michael Jones", 40, Gender.MALE, 7000.0));
    }

    @Test
    void should_return_employees_when_get_all_given_employee_exist() throws Exception {
        //given
        final List<Employee> givenEmployees = employeeRepository.findAll();

        //when
        //then
        final String jsonResponse = client.perform(MockMvcRequestBuilders.get("/employees"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();

        final List<Employee> employeesResult = employeesJacksonTester.parseObject(jsonResponse);
        assertThat(employeesResult)
            .usingRecursiveFieldByFieldElementComparator()
            .isEqualTo(givenEmployees);
    }

    @Test
    void should_return_employee_when_get_by_id() throws Exception {
        // Given
        final Employee givenEmployee = employeeRepository.findAll().get(0);

        // When
        // Then
        client.perform(MockMvcRequestBuilders.get("/employees/" + givenEmployee.getId()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(givenEmployee.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(givenEmployee.getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(givenEmployee.getAge()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(givenEmployee.getGender().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(givenEmployee.getSalary()));
    }


    @Test
    void should_return_employees_when_get_by_gender() throws Exception {
        //given
        List<Employee> employees = employeeRepository.getByGender(Gender.FEMALE);

        //when
        //then
        final String jsonResponse = client.perform(MockMvcRequestBuilders.get("/employees")
                        .param("gender", "FEMALE"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        final List<Employee> employeesResult = employeesJacksonTester.parseObject(jsonResponse);
        assertThat(employeesResult)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(employees);
    }

    @Test
    void should_create_employee_success() throws Exception {
        // Given
        String givenName = "New Employee";
        Integer givenAge = 18;
        Gender givenGender = Gender.FEMALE;
        Double givenSalary = 5000.0;
        String givenEmployee = String.format(
            "{\"name\": \"%s\", \"age\": \"%s\", \"gender\": \"%s\", \"salary\": \"%s\"}",
            givenName,
            givenAge,
            givenGender,
            givenSalary
        );

        // When
        // Then
        String contentAsString = client.perform(MockMvcRequestBuilders.post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenEmployee)
        ).andReturn().getResponse().getContentAsString();
        Employee employee = employeeJacksonTester.parseObject(contentAsString);

        Employee findEmployee = employeeRepository.findById(employee.getId()).orElseThrow(EmployeeInactiveException::new);
        assertThat(findEmployee.getName()).isEqualTo(givenName);
        assertThat(findEmployee.getAge()).isEqualTo(givenAge);
        assertThat(findEmployee.getGender()).isEqualTo(givenGender);
        assertThat(findEmployee.getSalary()).isEqualTo(givenSalary);
    }

    @Test
    void should_update_employee_success() throws Exception {
        // Given
        List<Employee> givenEmployees = employeeRepository.findAll();
        Integer givenId = givenEmployees.get(0).getId();
        String givenName = "New Employee";
        Integer givenAge = 30;
        Gender givenGender = Gender.FEMALE;
        Double givenSalary = 5432.0;
        String givenEmployee = String.format(
            "{\"id\": %s, \"name\": \"%s\", \"age\": \"%s\", \"gender\": \"%s\", \"salary\": \"%s\"}",
            givenId,
            givenName,
            givenAge,
            givenGender,
            givenSalary
        );

        // When
        // Then
        client.perform(MockMvcRequestBuilders.put("/employees/" + givenId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(givenEmployee)
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(givenName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(givenAge))
            .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(givenGender.name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(givenSalary));
        Employee employee = employeeRepository.findById(givenId).orElseThrow(EmployeeInactiveException::new);
        assertThat(employee.getId()).isEqualTo(givenId);
        assertThat(employee.getName()).isEqualTo(givenName);
        assertThat(employee.getAge()).isEqualTo(givenAge);
        assertThat(employee.getGender()).isEqualTo(givenGender);
        assertThat(employee.getSalary()).isEqualTo(givenSalary);
    }

    @Test
    void should_remove_employee_success() throws Exception {
        // Given
        List<Employee> givenEmployees = employeeRepository.findAll();
        Integer givenId = givenEmployees.get(0).getId();

        // When
        // Then
        client.perform(MockMvcRequestBuilders.delete("/employees/" + givenId))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees).hasSize(4);
        assertThat(employees.get(0).getId()).isEqualTo(givenEmployees.get(1).getId());
        assertThat(employees.get(1).getId()).isEqualTo(givenEmployees.get(2).getId());
        assertThat(employees.get(2).getId()).isEqualTo(givenEmployees.get(3).getId());
        assertThat(employees.get(3).getId()).isEqualTo(givenEmployees.get(4).getId());
    }

    @Test
    void should_return_employees_when_get_by_pageable() throws Exception {
        //given
        final List<Employee> givenEmployees = employeeRepository.findAll();

        //when
        //then
        client.perform(MockMvcRequestBuilders.get("/employees")
                .param("pageIndex", "2")
                .param("pageSize", "2"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(givenEmployees.get(2).getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(givenEmployees.get(3).getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(givenEmployees.get(2).getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(givenEmployees.get(2).getAge()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(givenEmployees.get(2).getGender().name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(givenEmployees.get(2).getSalary()));
    }
}