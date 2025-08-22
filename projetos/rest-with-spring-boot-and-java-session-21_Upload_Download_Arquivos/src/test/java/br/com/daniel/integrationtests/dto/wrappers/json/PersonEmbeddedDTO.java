package br.com.daniel.integrationtests.dto.wrappers.json;

import br.com.daniel.integrationtests.dto.PersonDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PersonEmbeddedDTO {

    @JsonProperty("people")
    private List<PersonDTO> people;

    public PersonEmbeddedDTO() { }

    public PersonEmbeddedDTO(List<PersonDTO> people) {
        this.people = people;
    }

    public List<PersonDTO> getPeople() {
        return people;
    }

    public void setPeople(List<PersonDTO> people) {
        this.people = people;
    }
}
