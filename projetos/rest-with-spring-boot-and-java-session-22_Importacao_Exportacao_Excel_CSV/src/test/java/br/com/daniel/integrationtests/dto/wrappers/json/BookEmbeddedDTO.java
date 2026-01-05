package br.com.daniel.integrationtests.dto.wrappers.json;

import br.com.daniel.integrationtests.dto.BookDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BookEmbeddedDTO {

    @JsonProperty("book")
    private List<BookDTO> book;

    public BookEmbeddedDTO() { }

    public BookEmbeddedDTO(List<BookDTO> book) {
        this.book = book;
    }

    public List<BookDTO> getBook() {
        return book;
    }

    public void setBook(List<BookDTO> book) {
        this.book = book;
    }
}
