package br.com.daniel.integrationtests.controllers.withxml;

import br.com.daniel.config.TestConfigs;
import br.com.daniel.integrationtests.dto.BookDTO;
import br.com.daniel.integrationtests.dto.wrappers.json.WrapperBookDTO;
import br.com.daniel.integrationtests.dto.wrappers.xml.PagedModelBook;
import br.com.daniel.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerXMLTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper xmlMapper;
    private static BookDTO book;

    @BeforeAll
    public static void setUp() {
        xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        book = new BookDTO();
        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(1)
    void create() throws JsonProcessingException {
        mockBook();

        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(book)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();

        BookDTO createdBook = xmlMapper.readValue(content, BookDTO.class);
        book = createdBook;

        assertNotNull(createdBook.getId());
        assertNotNull(createdBook.getAuthor());
        assertNotNull(createdBook.getPrice());
        assertNotNull(createdBook.getLaunchDate());
        assertNotNull(createdBook.getTitle());

        assertTrue(createdBook.getId() > 0);

        assertEquals("Author Test", createdBook.getAuthor());
        assertEquals(4.99, createdBook.getPrice());
        assertEquals("Title Test", createdBook.getTitle());
        assertNotNull(createdBook.getLaunchDate());
    }

    @Test
    @Order(2)
    void findById() throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParams("id", book.getId())
                .when()
                .get("{id}")
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        BookDTO bookDTO = xmlMapper.readValue(content, BookDTO.class);

        assertNotNull(bookDTO.getId());
        assertEquals("Author Test", bookDTO.getAuthor());
        assertEquals(4.99, bookDTO.getPrice());
        assertEquals("Title Test", bookDTO.getTitle());
        assertNotNull(bookDTO.getLaunchDate());

        assertTrue(bookDTO.getId() > 0);
    }

    @Test
    @Order(3)
    void findAll()  throws JsonProcessingException {
        var content = given(specification)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .queryParams("page", 3,"size", 12, "direction", "asc")
                .when()
                .get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        PagedModelBook wrapper = xmlMapper.readValue(content, PagedModelBook.class);
        List<BookDTO> books = wrapper.getContent();

        assertNotNull(books);
        assertFalse(books.isEmpty(), "The list of books should not be empty");

        BookDTO foundBook = books.stream()
                .findFirst().get();

        assertNotNull(foundBook);

        assertAll("Assert for the first book in the list",
                () -> assertEquals(218, foundBook.getId()),
                () -> assertEquals("Brandy Antonomolii", foundBook.getAuthor()),
                () -> assertEquals(24.99, foundBook.getPrice()),
                () -> assertNotNull(foundBook.getLaunchDate()),
                () -> assertEquals("After the Wedding (Efter brylluppet)", foundBook.getTitle())
        );

    }

    @Test
    @Order(4)
    void update() throws JsonProcessingException {
        mockUpdateBook();
        var content = given(specification)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .accept(MediaType.APPLICATION_XML_VALUE)
                .body(book)
                .when()
                .put()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_XML_VALUE)
                .extract()
                .body()
                .asString();

        BookDTO updatedBook = xmlMapper.readValue(content, BookDTO.class);

        assertNotNull(updatedBook.getId());
        assertNotNull(updatedBook.getLaunchDate());
        assertEquals("Author Test 1", updatedBook.getAuthor());
        assertEquals(9.99, updatedBook.getPrice());
        assertEquals("Title Test 1", updatedBook.getTitle());
    }

    @Test
    @Order(5)
    void delete() {
        given(specification)
                .pathParams("id", book.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    private void mockBook() {
        book.setAuthor("Author Test");
        book.setLaunchDate(new Date());
        book.setPrice(4.99);
        book.setTitle("Title Test");
    }

    private void mockUpdateBook() {
        book.setAuthor("Author Test 1");
        book.setLaunchDate(new Date());
        book.setPrice(9.99);
        book.setTitle("Title Test 1");
    }

}