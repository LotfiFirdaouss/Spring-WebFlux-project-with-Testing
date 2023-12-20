package net.webflux.demo;

import net.webflux.demo.dtos.EmployeeDto;
import net.webflux.demo.entities.Employee;
import net.webflux.demo.mappers.EmployeeMapper;
import net.webflux.demo.repositories.EmployeeRepository;
import net.webflux.demo.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIT {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeDto employeeDto;

    @BeforeEach
    public void setup(){
        System.out.println("Before each");
        employeeRepository.deleteAll().subscribe();
        employeeDto = EmployeeDto.builder()
                .firstName("Sara")
                .lastName("Nouh")
                .email("sm@gmail.com")
                .build();
    }

    @Test
    public void testSaveEmployee(){
        webTestClient.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeDto), EmployeeDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    public void testGetEmployee(){
        Employee savedEmployee = employeeService.saveEmployee(EmployeeMapper.mapToEmployee(employeeDto)).block();

        webTestClient.get().uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    @Test
    public void getAllEmployees(){
        EmployeeDto secondEmployee = EmployeeDto.builder()
                .firstName("Ahmed")
                .lastName("Nouh")
                .email("ahmed@gmail.com")
                .build();

        employeeService.saveEmployee(EmployeeMapper.mapToEmployee(employeeDto)).block();
        employeeService.saveEmployee(EmployeeMapper.mapToEmployee(secondEmployee)).block();

        webTestClient.get().uri("/api/employees")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .consumeWith(System.out::println);
    }

    @Test
    public void testUpdateEmployee(){
        Employee savedEmployee = employeeService.saveEmployee(EmployeeMapper.mapToEmployee(employeeDto)).block();

        employeeDto.setFirstName("changed");
        employeeDto.setLastName("changed");
        employeeDto.setEmail("changed");

        webTestClient.put().uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeDto), EmployeeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo("changed")
                .jsonPath("$.lastName").isEqualTo("changed")
                .jsonPath("$.email").isEqualTo("changed")
                .jsonPath("$.id").isEqualTo(savedEmployee.getId());
    }

    @Test
    public void testDeleteEmployee(){
        Employee savedEmployee = employeeService.saveEmployee(EmployeeMapper.mapToEmployee(employeeDto)).block();

        webTestClient.delete().uri("/api/employees/{id}", Collections.singletonMap("id", savedEmployee.getId()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().consumeWith(System.out::println);
    }
}
