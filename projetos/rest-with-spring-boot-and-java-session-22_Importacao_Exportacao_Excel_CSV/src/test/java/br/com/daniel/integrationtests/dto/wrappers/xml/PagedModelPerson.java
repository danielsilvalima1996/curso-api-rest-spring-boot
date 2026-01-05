package br.com.daniel.integrationtests.dto.wrappers.xml;

import br.com.daniel.integrationtests.dto.PersonDTO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class PagedModelPerson {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "content")
    public List<PersonDTO> content;

    public PagedModelPerson() {}

    public PagedModelPerson(List<PersonDTO> content) {
        this.content = content;
    }

    public List<PersonDTO> getContent() {
        return content;
    }

    public void setContent(List<PersonDTO> content) {
        this.content = content;
    }
}