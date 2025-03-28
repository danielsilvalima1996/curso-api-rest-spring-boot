package br.com.daniel.services;

import br.com.daniel.data.dto.v1.PersonDTOV1;
import br.com.daniel.data.dto.v2.PersonDTOV2;
import br.com.daniel.exception.ResourceNotFoundException;
import br.com.daniel.mapper.custom.PersonMapper;
import br.com.daniel.model.Person;
import br.com.daniel.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.daniel.mapper.ObjectMapper.parseListObjects;
import static br.com.daniel.mapper.ObjectMapper.parseObject;

@Service
public class PersonServices {

    private final Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonMapper convert;

    public List<PersonDTOV1> findAll() {
        logger.info("Finding all people!");
        return parseListObjects(repository.findAll(), PersonDTOV1.class);
    }

    public PersonDTOV1 findById(Long id) {
        logger.info("Finding one Person! {}", id);

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return parseObject(entity, PersonDTOV1.class);
    }

    public PersonDTOV1 create(PersonDTOV1 person) {
        logger.info("Creating one Person! ");
        var entity = parseObject(person, Person.class);
        return parseObject(repository.save(entity), PersonDTOV1.class);
    }

    public PersonDTOV2 createV2(PersonDTOV2 person) {
        logger.info("Creating one Person V2!");
        var entity = parseObject(person, Person.class);
        return convert.convertEntityToDTO(repository.save(entity));
    }

    public PersonDTOV1 update(PersonDTOV1 person) {
        logger.info("Updating one Person! ");
        Person entity = repository.findById(person.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());
        return parseObject(repository.save(entity), PersonDTOV1.class);
    }

    public void deleteById(Long id) {
        logger.info("Delete one Person! {}", id);
        Person entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }


}
