package br.com.daniel.services;

import br.com.daniel.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonServices {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonServices.class.getName());

    public Person findById(String id) {
        logger.info("Finding one Person! " + id);

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Daniel");
        person.setLastName("Lima");
        person.setAddress("Guarulhos - São Paulo - Brasil");
        person.setGender("Male");
        return person;
    }

    public List<Person> findAll() {
        logger.info("Finding all people!");

        List<Person> people = new ArrayList<Person>();
        for (int i = 0; i < 8; i++) {
            people.add(mockPerson(i));
        }
        return people;
    }

    public Person create(Person person) {
        logger.info("Creating one Person! ");
        person.setId(counter.incrementAndGet());
        return person;
    }

    public Person update(Person person) {
        logger.info("Updating one Person! ");
        person.setId(counter.incrementAndGet());
        return person;
    }

    public void deleteById(String id) {
        logger.info("Delete one Person! " + id);
    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("FirstName " + i);
        person.setLastName("LastName " + i);
        person.setAddress("Some address in Brasil");
        person.setGender(i % 2 == 0 ? "Male" : "Female");
        return person;
    }


}
