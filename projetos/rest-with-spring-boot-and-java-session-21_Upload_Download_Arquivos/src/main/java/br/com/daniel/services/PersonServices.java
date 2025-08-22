package br.com.daniel.services;

import br.com.daniel.controllers.PersonController;
import br.com.daniel.data.dto.PersonDTO;
import br.com.daniel.exception.RequiredObjectIsNullException;
import br.com.daniel.exception.ResourceNotFoundException;
import br.com.daniel.model.Person;
import br.com.daniel.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import static br.com.daniel.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {

    private final Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    private PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonDTO> assembler;

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding all people!");

        var people = repository.findAll(pageable);

        var peopleWithLink = people.map(person -> {
            var personDTO = parseObject(person, PersonDTO.class);
            addHateosLinks(personDTO);
            return personDTO;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PersonController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                pageable.getSort().toString())
                ).withSelfRel();
        return assembler.toModel(peopleWithLink, findAllLink);
    }

    public PagedModel<EntityModel<PersonDTO>> findPeopleByFirstName(String firstName, Pageable pageable) {
        logger.info("Finding people by name!");

        var people = repository.findPeopleByFirstName(firstName, pageable);

        var peopleWithLink = people.map(person -> {
            var personDTO = parseObject(person, PersonDTO.class);
            addHateosLinks(personDTO);
            return personDTO;
        });
        Link findAllLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PersonController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                pageable.getSort().toString())
        ).withSelfRel();
        return assembler.toModel(peopleWithLink, findAllLink);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person! {}", id);

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        var dto = parseObject(entity, PersonDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public PersonDTO create(PersonDTO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Creating one Person! ");
        var entity = parseObject(person, Person.class);
        var dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public PersonDTO update(PersonDTO person) {
        if (person == null) throw new RequiredObjectIsNullException();
        logger.info("Updating one Person! ");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        PersonDTO dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Delete one Person! {}", id);
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

    @Transactional
    public PersonDTO disabledPerson(Long id) {
        logger.info("Disabled person, id! {}", id);
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No book found for this ID! " + id));
        repository.disabledPerson(id);
        Person entity = repository.findById(id).get();
        PersonDTO dto = parseObject(entity, PersonDTO.class);
        addHateosLinks(dto);
        return dto;
    }

    public static void addHateosLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(0, 12, "ASC")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findPeopleByFirstName(dto.getFirstName(), 0, 12, "ASC")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disabledPerson(dto.getId())).withRel("disabledPerson").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }

}
