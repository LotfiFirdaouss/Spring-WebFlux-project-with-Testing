package net.webflux.demo.controllers;

import lombok.AllArgsConstructor;
import net.webflux.demo.dtos.EmployeeDto;
import net.webflux.demo.entities.Employee;
import net.webflux.demo.mappers.EmployeeMapper;
import net.webflux.demo.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/employees")
@AllArgsConstructor // to inject dependencies
public class EmployeeController {

    private EmployeeService employeeService;

    // build Reactive Save Employee REST API
    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Mono<EmployeeDto> saveEmployee(@RequestBody EmployeeDto employeeDto){
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Mono<Employee> savedEmployee = employeeService.saveEmployee(employee);
        return savedEmployee
                .map((employeeEntity) -> EmployeeMapper.mapToEmployeeDto(employeeEntity));
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<EmployeeDto> getEmployee(@PathVariable("id") String employeeId){
        Mono<Employee> employee = employeeService.getEmployee(employeeId);
        return employee
                .map((employeeEntity) -> EmployeeMapper.mapToEmployeeDto(employeeEntity));
    }

    // Build Reactive Get All Employees REST API
    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<EmployeeDto> getEmployees(){
        Flux<Employee> employeeFlux = employeeService.getAllEmployees();
        return employeeFlux
                .map((employee -> EmployeeMapper.mapToEmployeeDto(employee)))
                .switchIfEmpty(Flux.empty());
    }

    // Build Reactive Update Employee REST API
    @PutMapping("{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<EmployeeDto> updateEmployee(@RequestBody EmployeeDto employeeDto,@PathVariable("id") String employeeId){
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Mono<Employee> savedEmployeeMono = employeeService.updateEmployee(employee, employeeId);
        return savedEmployeeMono
                .map((savedEmployee) -> EmployeeMapper.mapToEmployeeDto(savedEmployee));
    }

    // Build Reactive Delete Employee REST API
    @DeleteMapping("{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Mono<Void> deleteEmployee(@PathVariable("id") String employeeId){
        return employeeService.deleteEmployee(employeeId);
    }
}
