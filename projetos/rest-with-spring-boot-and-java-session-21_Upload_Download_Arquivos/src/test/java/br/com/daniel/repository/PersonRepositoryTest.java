package br.com.daniel.repository;

import br.com.daniel.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.daniel.model.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    static void setUp() {
        person = new Person();
    }

    @Test
    @Order(1)
    void findPeopleByFirstName() {
        Pageable pageable = PageRequest.of(
                0,
                45,
                Sort.by(Sort.Direction.ASC, "firstName")
        );
        person = repository.findPeopleByFirstName("iko", pageable).getContent().getFirst();
        Assertions.assertNotNull(person);

        assertAll("Assert for the first person in the list",
            () -> assertEquals(894, person.getId()),
            () -> assertEquals("Nikolai", person.getFirstName()),
            () -> assertEquals("Delgardillo", person.getLastName()),
            () -> assertEquals("11 Pawling Plaza", person.getAddress()),
            () -> assertEquals("Male", person.getGender()),
            () -> assertTrue(person.getEnabled())
        );
    }

    @Test
    @Order(2)
    void disabledPerson() {
        Long id = person.getId();
        repository.disabledPerson(id);

        var result = repository.findById(id);
        person = result.get();

        Assertions.assertNotNull(person);

        assertAll("Assert for the first person in the list",
                () -> assertEquals(894, person.getId()),
                () -> assertEquals("Nikolai", person.getFirstName()),
                () -> assertEquals("Delgardillo", person.getLastName()),
                () -> assertEquals("11 Pawling Plaza", person.getAddress()),
                () -> assertEquals("Male", person.getGender()),
                () -> assertFalse(person.getEnabled())
        );
    }
}