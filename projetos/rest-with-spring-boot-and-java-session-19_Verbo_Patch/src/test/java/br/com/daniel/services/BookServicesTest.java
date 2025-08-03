package br.com.daniel.services;

import br.com.daniel.data.dto.BookDTO;
import br.com.daniel.exception.RequiredObjectIsNullException;
import br.com.daniel.model.Book;
import br.com.daniel.repository.BookRepository;
import br.com.daniel.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {

    MockBook input;

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookServices service;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        List<Book> list = input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        List<BookDTO> books = service.findAll();
        assertNotNull(books);
        assertEquals(14, books.size());

        books.forEach(book -> {
            assertNotNull(book.getId());
            assertNotNull(book.getLinks());

            assertNotNull(book.getLinks().stream()
                    .anyMatch(link -> link.getRel().value().equals("self")
                            && link.getHref().endsWith("/api/book/v1/" + book.getId())
                            && link.getType().equals("GET")
                    ));

            assertNotNull(book.getLinks().stream()
                    .anyMatch(link -> link.getRel().value().equals("create")
                            && link.getHref().endsWith("/api/book/v1")
                            && link.getType().equals("POST")
                    ));

            assertNotNull(book.getLinks().stream()
                    .anyMatch(link -> link.getRel().value().equals("update")
                            && link.getHref().endsWith("/api/book/v1")
                            && link.getType().equals("PUT")
                    ));

            assertNotNull(book.getLinks().stream()
                    .anyMatch(link -> link.getRel().value().equals("delete")
                            && link.getHref().endsWith("/api/book/v1")
                            && link.getType().equals("DELETE")
                    ));

            assertEquals("Author Test" + book.getId(), book.getAuthor());
            assertNotNull(book.getId());
            assertNotNull(book.getLaunchDate());
            assertEquals(2.5 * book.getId(), book.getPrice());
            assertEquals("Title Test" + book.getId(), book.getTitle());
        });

    }

    @Test
    void findById() {
        Book book = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        var result = service.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("DELETE")
                ));

        assertEquals("Author Test" + book.getId(), book.getAuthor());
        assertNotNull(book.getId());
        assertNotNull(book.getLaunchDate());
        assertEquals(2.5 * book.getId(), book.getPrice());
        assertEquals("Title Test" + book.getId(), book.getTitle());
    }

    @Test
    void create() {
        BookDTO bookDTO = input.mockDTO(1);
        Book book = input.mockEntity(1);
        when(repository.save(book)).thenReturn(book);
        var result = service.create(bookDTO);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("DELETE")
                ));

        assertEquals("Author Test" + book.getId(), book.getAuthor());
        assertNotNull(book.getId());
        assertNotNull(book.getLaunchDate());
        assertEquals(2.5 * book.getId(), book.getPrice());
        assertEquals("Title Test" + book.getId(), book.getTitle());
    }

    @Test
    void testCreateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.create(null);
                });
        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.update(null);
                });
        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {
        BookDTO bookDTO = input.mockDTO(1);
        Book book = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);
        var result = service.update(bookDTO);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/book/v1/1")
                        && link.getType().equals("GET")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("POST")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("PUT")
                ));

        assertNotNull(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/book/v1")
                        && link.getType().equals("DELETE")
                ));

        assertEquals("Author Test" + book.getId(), book.getAuthor());
        assertNotNull(book.getId());
        assertNotNull(book.getLaunchDate());
        assertEquals(2.5 * book.getId(), book.getPrice());
        assertEquals("Title Test" + book.getId(), book.getTitle());
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        when(repository.findById(anyLong())).thenReturn(Optional.of(book));
        service.deleteById(anyLong());
        verify(repository, times(1)).delete(book);
    }

}