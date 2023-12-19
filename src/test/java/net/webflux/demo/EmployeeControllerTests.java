package net.webflux.demo;

import net.webflux.demo.controllers.EmployeeController;
import net.webflux.demo.dtos.EmployeeDto;
import net.webflux.demo.entities.Employee;
import net.webflux.demo.mappers.EmployeeMapper;
import net.webflux.demo.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = EmployeeController.class)
public class EmployeeControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeDto employeeDto;

    @BeforeEach
    public void setup(){
        employeeDto = EmployeeDto.builder()
                .firstName("Sara")
                .lastName("Mouha")
                .email("sm@gmail.com")
                .build();
    }

    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnSavedEmployee(){
        // given - precondition or setup
        BDDMockito.given(employeeService.saveEmployee(ArgumentMatchers.any(Employee.class)))
                .willReturn(Mono.just(EmployeeMapper.mapToEmployee(employeeDto)));

        // when - action or behaviour
        WebTestClient.ResponseSpec response = webTestClient.post().uri("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeDto), EmployeeDto.class)
                .exchange();

        // then - verify the result or output
        response.expectStatus().isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    // JUnit test for getEmployee REST api
    @Test
    public void givenEmployeeId_whenGetEmployee_thenReturnEmployeeObject() {
        // given - precondition or setup
        String employeeId = "ba321";
        BDDMockito.given(employeeService.getEmployee(employeeId))
                .willReturn(Mono.just(EmployeeMapper.mapToEmployee(employeeDto)));

        // when - action or the behaviour that we are going to test
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees/{id}", Collections.singletonMap("id", employeeId))
                .exchange();

        // then -verify the output
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo(employeeDto.getFirstName())
                .jsonPath("$.lastName").isEqualTo(employeeDto.getLastName())
                .jsonPath("$.email").isEqualTo(employeeDto.getEmail());
    }

    // JUnit test for
    @Test
    public void givenEmployeeList_whenGetAllEmployees_thenReturnListOfEmployees() {
        // given - precondition or setup
        BDDMockito.given(employeeService.getAllEmployees())
                .willReturn(Flux.just(EmployeeMapper.mapToEmployee(employeeDto)));

        // when - action or the behaviour that we are going to test
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/employees")
                .exchange();

        // then -verify the output
        response.expectStatus().isOk()
                .expectBodyList(EmployeeDto.class)
                .consumeWith(System.out::println);
    }

    // JUnit test for
    @Test
    public void givenNewEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() {
        // given - precondition or setup
        String employeeId = "ab123";

        employeeDto.setFirstName("newFirst");
        employeeDto.setLastName("newLast");
        employeeDto.setEmail("newEmail@gmail");

        BDDMockito.given(employeeService.updateEmployee(EmployeeMapper.mapToEmployee(employeeDto), employeeId))
                .willReturn(Mono.just(EmployeeMapper.mapToEmployee(employeeDto)));

        // when - action or the behaviour that we are going to test
        WebTestClient.ResponseSpec response = webTestClient.put()
                .uri("/api/employees/{id}", Collections.singletonMap("id",employeeId))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(employeeDto), EmployeeDto.class)
                .exchange();

        // then -verify the output
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.firstName").isEqualTo("newFirst")
                .jsonPath("$.lastName").isEqualTo("newLast")
                .jsonPath("$.email").isEqualTo("newEmail@gmail");

    }

    // JUnit test for
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenEReturnNothing() {
        // given - precondition or setup
        String employeeId = "ab123";
        Mono<Void> voidMono = Mono.empty();

        BDDMockito.given(employeeService.deleteEmployee(employeeId))
                .willReturn(voidMono);

        // when - action or the behaviour that we are going to test
        WebTestClient.ResponseSpec response = webTestClient.delete().uri("/api/employees/{id}", Collections.singletonMap("id", employeeId)).exchange();

        // then -verify the output
        response.expectStatus().isNoContent()
                .expectBody()
                .consumeWith(System.out::println);
    }
}
