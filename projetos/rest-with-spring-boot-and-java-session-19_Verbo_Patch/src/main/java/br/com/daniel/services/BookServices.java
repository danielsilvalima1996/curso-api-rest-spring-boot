package br.com.daniel.services;

import br.com.daniel.controllers.BookController;
import br.com.daniel.data.dto.BookDTO;
import br.com.daniel.exception.RequiredObjectIsNullException;
import br.com.daniel.exception.ResourceNotFoundException;
import br.com.daniel.model.Book;
import br.com.daniel.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.daniel.mapper.ObjectMapper.parseListObjects;
import static br.com.daniel.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookServices {

    private final Logger logger = LoggerFactory.getLogger(BookServices.class.getName());

    @Autowired
    private BookRepository repository;

    public List<BookDTO> findAll() {
        logger.info("Finding all books!");
        var dtos = parseListObjects(repository.findAll(), BookDTO.class);
        dtos.forEach(BookServices::addHateosLinks);
        return dtos;
    }

    public BookDTO findById(Long id) {
        logger.info("Finding one Book! {}", id);
        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var dto = parseObject(entity, BookDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public BookDTO create(BookDTO bookDTO) {
        if(bookDTO == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one book!");
        var entity = parseObject(bookDTO, Book.class);
        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public BookDTO update(BookDTO bookDTO) {
        if(bookDTO == null) throw new RequiredObjectIsNullException();
        var entity = repository.findById(bookDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No book found for this ID! " + bookDTO.getId()));
        entity.setAuthor(bookDTO.getAuthor());
        entity.setLaunchDate(bookDTO.getLaunchDate());
        entity.setPrice(bookDTO.getPrice());
        entity.setTitle(bookDTO.getTitle());
        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public void deleteById(Long id) {
        logger.info("Delete one Book! {}", id);
        Book entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book found for this ID! " + id));
        repository.delete(entity);
    }

    public static void addHateosLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookController.class).deleteById(dto.getId())).withRel("delele").withType("DELETE"));
    }


}
