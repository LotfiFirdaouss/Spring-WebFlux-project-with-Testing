package net.webflux.demo.services.impl;

import lombok.AllArgsConstructor;
import net.webflux.demo.entities.Employee;
import net.webflux.demo.repositories.EmployeeRepository;
import net.webflux.demo.services.EmployeeService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Override
    public Mono<Employee> saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Mono<Employee> getEmployee(String employeeId) {
        return employeeRepository.findById(employeeId);
    }

    @Override
    public Flux<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Mono<Employee> updateEmployee(Employee employee, String employeeId) {
        Mono<Employee> employeeMono = employeeRepository.findById(employeeId);
        return employeeMono.flatMap((existingEmployee) -> {
            existingEmployee.setFirstName(employee.getFirstName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setEmail(employee.getEmail());

            return employeeRepository.save(existingEmployee);
        });
    }

    @Override
    public Mono<Void> deleteEmployee(String employeeId) {
        return employeeRepository.deleteById(employeeId);
    }
}
