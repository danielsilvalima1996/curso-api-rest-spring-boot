package br.com.daniel.unittests.services;

import br.com.daniel.controllers.PersonController;
import br.com.daniel.data.dto.PersonDTO;
import br.com.daniel.exception.RequiredObjectIsNullException;
import br.com.daniel.exception.ResourceNotFoundException;
import br.com.daniel.model.Person;
import br.com.daniel.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.daniel.mapper.ObjectMapper.parseListObjects;
import static br.com.daniel.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonServices {

    private final Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    private PersonRepository repository;

    public List<PersonDTO> findAll() {
        logger.info("Finding all people!");
        var dtos = parseListObjects(repository.findAll(), PersonDTO.class);
        dtos.forEach(PersonServices::addHateosLinks);
        return dtos;
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
        if(person == null) throw new RequiredObjectIsNullException();
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

    public void deleteById(Long id) {
        logger.info("Delete one Person! {}", id);
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

    public static void addHateosLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).deleteById(dto.getId())).withRel("delele").withType("DELETE"));
    }

}
