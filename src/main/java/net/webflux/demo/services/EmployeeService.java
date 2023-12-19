package net.webflux.demo.services;

import net.webflux.demo.entities.Employee;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeService {
    Mono<Employee> saveEmployee(Employee employee);

    Mono<Employee> getEmployee(String EmployeeId);

    Flux<Employee> getAllEmployees();

    Mono<Employee> updateEmployee(Employee employee, String employeeId);

    Mono<Void> deleteEmployee(String employeeId);
}
