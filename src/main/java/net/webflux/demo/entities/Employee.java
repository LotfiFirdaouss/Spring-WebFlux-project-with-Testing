package net.webflux.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "employees") // mongodb entity
public class Employee {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;

}
