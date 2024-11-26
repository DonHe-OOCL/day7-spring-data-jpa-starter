package com.oocl.springbootemployee.service;

import com.oocl.springbootemployee.exception.EmployeeAgeNotValidException;
import com.oocl.springbootemployee.exception.EmployeeAgeSalaryNotMatchedException;
import com.oocl.springbootemployee.exception.EmployeeInactiveException;
import com.oocl.springbootemployee.model.Employee;
import com.oocl.springbootemployee.model.Gender;
import com.oocl.springbootemployee.repository.EmployeeInMemoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class EmployeeService {
    private final EmployeeInMemoryRepository employeeRepository;
    public EmployeeService(EmployeeInMemoryRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findAll(Gender gender) {
        return employeeRepository.findAllByGender(gender);
    }

    public List<Employee> findAll(Integer page, Integer pageSize) {
        return employeeRepository.findAllByPage(page, pageSize);
    }

    public Employee findById(Integer employeeId) {
        return employeeRepository.findById(employeeId);
    }

    public Employee create(Employee employee) {
        if(employee.getAge() < 18 || employee.getAge() > 65)
            throw new EmployeeAgeNotValidException();
        if(employee.getAge() >= 30 && employee.getSalary() < 20000.0)
            throw new EmployeeAgeSalaryNotMatchedException();

        employee.setActive(true);
        return employeeRepository.create(employee);
    }

    public Employee update(Integer employeeId , Employee employee) {
        Employee employeeExisted = employeeRepository.findById(employeeId);
        if(!employeeExisted.getActive())
            throw new EmployeeInactiveException();

        return employeeRepository.update(employeeId, employee);
    }

    public void delete(Integer employeeId) {
        employeeRepository.deleteById(employeeId);
    }
}
