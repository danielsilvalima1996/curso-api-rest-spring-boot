package br.com.daniel.controllers;

import br.com.daniel.controllers.docs.PersonControllerDocs;
import br.com.daniel.data.dto.PersonDTO;
import br.com.daniel.file.exporter.MediaTypes;
import br.com.daniel.services.PersonServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/person/v1")
@Tag(name = "People", description = "Endpoints for managing People")
public class PersonController implements PersonControllerDocs {

    @Autowired
    private PersonServices service;

    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_YAML_VALUE
    })
    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sort = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "firstName"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping(
            value = "/findPeopleByName/{firstName}"
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findPeopleByFirstName(
            @PathVariable(value = "firstName") String firstName,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sort = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "firstName"));
        return ResponseEntity.ok(service.findPeopleByFirstName(firstName, pageable));
    }


    @GetMapping(
            value = "/exportPage",
            produces = {
                    MediaTypes.APPLICATION_XLSX_VALUE,
                    MediaTypes.APPLICATION_CSV_VALUE
            }
    )
    @Override
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestHeader(value = "Accept", required = false) String acceptHeader
    ) {
        var sort = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "firstName"));
        var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
        Resource file = service.exportPage(acceptHeader, pageable);
        var fileExtension = MediaTypes.APPLICATION_XLSX_VALUE.equalsIgnoreCase(acceptHeader) ? ".xlsx" : ".csv";
        var filename = "people_exported" + fileExtension;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    //@CrossOrigin(origins = "http://localhost:8080")
    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PersonDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    //@CrossOrigin(origins = {"http://localhost:8080", "https://www.erudio.com.br"})
    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PersonDTO> create(@RequestBody PersonDTO person) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(person));
    }

    @PostMapping(value = "massCreation",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<List<PersonDTO>> massCreation(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.massCreate(file));
    }

    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PersonDTO> update(@RequestBody PersonDTO person) {
        return ResponseEntity.ok(service.update(person));
    }

    @PatchMapping(value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            })
    @Override
    public ResponseEntity<PersonDTO> disabledPerson(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.disabledPerson(id));
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
