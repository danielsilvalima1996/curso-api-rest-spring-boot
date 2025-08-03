package br.com.daniel.integrationtests.controllers.withxml;

import br.com.daniel.config.TestConfigs;
import br.com.daniel.integrationtests.dto.PersonDTO;
import br.com.daniel.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXMLTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper xmlMapper;
    private static PersonDTO person;

    @BeforeAll
    public static void setUp() {
        xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        person = new PersonDTO();
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

    }

    @Test
    @Order(1)
    void create() throws JsonProcessingException {
        mockPerson();

        var content = given().spec(specification) // Reutiliza a spec
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .body(person)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        PersonDTO createdPerson = xmlMapper.readValue(content, PersonDTO.class);
        person = createdPerson;

        assertNotNull(createdPerson.getId());
        assertTrue(createdPerson.getId() > 0);
        assertEquals("Daniel", createdPerson.getFirstName());
        assertEquals( "Lima", createdPerson.getLastName());
        assertEquals( "Guarulhos - São Paulo - BR", createdPerson.getAddress());
        assertEquals( "Male", createdPerson.getGender());
        assertTrue(createdPerson.getEnabled());
    }

    @Test
    @Order(2)
    void findById() throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParams("id", person.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTO personDto = xmlMapper.readValue(content, PersonDTO.class);

        assertNotNull(personDto.getId());
        assertTrue(personDto.getId() > 0);
        assertEquals("Daniel", personDto.getFirstName());
        assertEquals( "Lima", personDto.getLastName());
        assertEquals( "Guarulhos - São Paulo - BR", personDto.getAddress());
        assertEquals( "Male", personDto.getGender());
        assertTrue(personDto.getEnabled());
    }

    @Test
    @Order(3)
    void findAll()  throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        List<PersonDTO> people = xmlMapper.readValue(content, new TypeReference<>() {});

        assertNotNull(people);
        assertFalse(people.isEmpty(), "The list of people should not be empty");

        PersonDTO foundPerson = people.stream()
                .filter(fil -> fil.getId().equals(person.getId()))
                .toList().getFirst();

        assertNotNull(foundPerson);

        assertAll("Assert for the first person in the list",
                () -> assertEquals(person.getId(), foundPerson.getId()),
                () -> assertEquals(person.getFirstName(), foundPerson.getFirstName()),
                () -> assertEquals(person.getLastName(), foundPerson.getLastName()),
                () -> assertEquals(person.getAddress(), foundPerson.getAddress()),
                () -> assertEquals(person.getGender(), foundPerson.getGender()),
                () -> assertNotNull(person.getEnabled())
        );
    }

    @Test
    @Order(4)
    void update() throws JsonProcessingException {
        mockUpdatePerson();

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .body(person)
                .when()
                .put()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTO updatedPerson = xmlMapper.readValue(content, PersonDTO.class);

        assertNotNull(updatedPerson.getId());
        assertEquals(person.getId(), updatedPerson.getId());
        assertEquals(person.getFirstName(), updatedPerson.getFirstName());
        assertEquals(person.getLastName(), updatedPerson.getLastName());
        assertEquals(person.getAddress(), updatedPerson.getAddress());
        assertEquals(person.getGender(), updatedPerson.getGender());
        assertTrue(updatedPerson.getEnabled());
    }

    @Test
    @Order(5)
    void disabledPerson() throws JsonProcessingException {
        mockUpdatePerson();

        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParams("id", person.getId())
                .when()
                .patch("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        PersonDTO updatedPerson = xmlMapper.readValue(content, PersonDTO.class);

        assertNotNull(updatedPerson.getId());
        assertEquals(person.getId(), updatedPerson.getId());
        assertEquals(person.getFirstName(), updatedPerson.getFirstName());
        assertEquals(person.getLastName(), updatedPerson.getLastName());
        assertEquals(person.getAddress(), updatedPerson.getAddress());
        assertEquals(person.getGender(), updatedPerson.getGender());
        assertFalse(updatedPerson.getEnabled());
    }

    @Test
    @Order(6)
    void delete() {
        given(specification)
                .pathParams("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    private void mockPerson() {
        person.setFirstName("Daniel");
        person.setLastName("Lima");
        person.setAddress("Guarulhos - São Paulo - BR");
        person.setGender("Male");
        person.setEnabled(true);
    }

    private void mockUpdatePerson() {
        person.setFirstName("Emelly");
        person.setLastName("Jesus");
        person.setAddress("Guarulhos - São Paulo - BR");
        person.setGender("Female");
        person.setEnabled(true);
    }

}