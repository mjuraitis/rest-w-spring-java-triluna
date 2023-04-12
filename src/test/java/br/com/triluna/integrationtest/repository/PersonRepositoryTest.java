package br.com.triluna.integrationtest.repository;

import br.com.triluna.config.TestConfig;
import br.com.triluna.integrationtest.testcontainer.AbstractIntegrationTest;
import br.com.triluna.integrationtest.vo.PersonVO;
import br.com.triluna.integrationtest.vo.wrapper.WrapperPersonVO;
import br.com.triluna.model.Person;
import br.com.triluna.repository.PersonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    public PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup() {

        person = new Person();
    }

    @Test
    @Order(1)
    public void testFindPersonsByName() throws JsonMappingException, JsonProcessingException {

        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("aly", pageable).getContent().get(0);

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertFalse(person.getEnabled());

        assertEquals(463, person.getId());
        assertEquals("Alysa", person.getFirstName());
        assertEquals("Bandy", person.getLastName());
        assertEquals("73 Moulton Alley", person.getAddress());
        assertEquals("Female", person.getGender());
    }

    @Test
    @Order(2)
    public void testDisablePerson() throws JsonMappingException, JsonProcessingException {

        repository.disablePerson(person.getId());

        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("aly", pageable).getContent().get(0);

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertFalse(person.getEnabled());

        assertEquals(463, person.getId());
        assertEquals("Alysa", person.getFirstName());
        assertEquals("Bandy", person.getLastName());
        assertEquals("73 Moulton Alley", person.getAddress());
        assertEquals("Female", person.getGender());
    }
}
