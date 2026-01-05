package br.com.daniel.services;

import br.com.daniel.controllers.PersonController;
import br.com.daniel.data.dto.PersonDTO;
import br.com.daniel.exception.BadRequestException;
import br.com.daniel.exception.FileStorageException;
import br.com.daniel.exception.RequiredObjectIsNullException;
import br.com.daniel.exception.ResourceNotFoundException;
import br.com.daniel.file.exporter.contract.FileExporter;
import br.com.daniel.file.exporter.factory.FileExporterFactory;
import br.com.daniel.file.importer.contract.FileImporter;
import br.com.daniel.file.importer.factory.FileImporterFactory;
import br.com.daniel.model.Person;
import br.com.daniel.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static br.com.daniel.file.exporter.MediaTypes.APPLICATION_XLSX_VALUE;
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

    @Autowired
    FileImporterFactory importer;

    @Autowired
    FileExporterFactory exporter;

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding all people!");

        var people = repository.findAll(pageable);

        return buildPagedModel(pageable, people);
    }

    public PagedModel<EntityModel<PersonDTO>> findPeopleByFirstName(String firstName, Pageable pageable) {
        logger.info("Finding people by name!");

        var people = repository.findPeopleByFirstName(firstName, pageable);

        return buildPagedModel(pageable, people);
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

    public List<PersonDTO> massCreate(MultipartFile file) {
        logger.info("Import people from file!");

        if (file.isEmpty()) {
            throw new BadRequestException("Please a set valid file!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("File name cannot be null!"));
            FileImporter importer = this.importer.getImporter(fileName);

            List<Person> entities = importer.importFile(inputStream).stream()
                    .map(dto -> repository.save(parseObject(dto, Person.class)))
                    .toList();

            return entities.stream()
                    .map(entity -> {
                        var dto = parseObject(entity, PersonDTO.class);
                        addHateosLinks(dto);
                        return dto;
                    })
                    .toList();
        } catch (Exception e) {
            throw new FileStorageException("Error processing de file!");
        }

    }

    public Resource exportPage(String acceptHeader, Pageable pageable) {
        logger.info("Export people from file: {}!", acceptHeader);
        try {
            FileExporter fileExporter = this.exporter.getExporter(acceptHeader);
            var people = repository.findAll(pageable)
                    .map(person -> parseObject(person, PersonDTO.class))
                    .getContent();
            return fileExporter.exportFile(people);
        } catch (Exception e) {
            throw new FileStorageException("Error during file export!", e);
        }
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

    private PagedModel<EntityModel<PersonDTO>> buildPagedModel(Pageable pageable, Page<Person> people) {
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

    public static void addHateosLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(0, 12, "ASC")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findPeopleByFirstName("", 0, 12, "ASC")).withRel("findPeopleByFirstName").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disabledPerson(dto.getId())).withRel("disabledPerson").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
        dto.add(linkTo(methodOn(PersonController.class).exportPage(0, 12, "ASC", APPLICATION_XLSX_VALUE)).withRel("exportPage").withType("GET"));
    }

}
