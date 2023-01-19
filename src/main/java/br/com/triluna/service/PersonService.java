package br.com.triluna.service;

import br.com.triluna.data.vo.v1.PersonVO;
import br.com.triluna.exception.ResourceNotFoundException;
import br.com.triluna.mapper.ModelToolMapper;
import br.com.triluna.model.Person;
import br.com.triluna.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    public PersonVO create(PersonVO person) {

        logger.info("Creating a person...");

        var entity = repository.save(ModelToolMapper.parseObject(person, Person.class));

        return ModelToolMapper.parseObject(entity, PersonVO.class);
    }

    public void delete(Long id) {

        logger.info(String.format("Deleting person: %s", id));

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PersonVO not found - id: " + id.toString()));

        repository.delete(entity);
    }

    public List<PersonVO> findAll() {

        logger.info("Finding people...");

        return ModelToolMapper.parseListObject(repository.findAll(), PersonVO.class);
    }

    public PersonVO findById(Long id) {

        logger.info("Finding one person...");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PersonVO not found - id: " + id.toString()));

        return ModelToolMapper.parseObject(entity, PersonVO.class);
    }

    public PersonVO update(PersonVO person) {

        logger.info(String.format("Updating person: %s", person.getId()));

        var id = person.getId();

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PersonVO not found - id: " + id.toString()));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return ModelToolMapper.parseObject(repository.save(entity), PersonVO.class);
    }

    private PersonVO mockPerson(int i) {

        PersonVO person = new PersonVO();

        person.setId(Long.valueOf(i));
        person.setFirstName("Nome" + i);
        person.setLastName("Sobrenome" + i);
        person.setAddress("Endereço" + i);
        person.setGender(i % 2 == 0 ? "Male" : "Female");

        return person;
    }
}
