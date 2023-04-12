package br.com.triluna.service;

import br.com.triluna.controller.PersonController;
import br.com.triluna.data.vo.v1.PersonVO;
import br.com.triluna.data.vo.v2.PersonVOV2;
import br.com.triluna.exception.RequiredObjectIsNullException;
import br.com.triluna.exception.ResourceNotFoundException;
import br.com.triluna.mapper.ModelToolMapper;
import br.com.triluna.mapper.custom.PersonMapper;
import br.com.triluna.model.Person;
import br.com.triluna.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PersonMapper personMapper;

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;

    public PersonVO create(PersonVO person) {

        if (person == null) {

            throw new RequiredObjectIsNullException();
        }

        logger.info("Creating a person...");

        var entity = repository.save(ModelToolMapper.parseObject(person, Person.class));

        PersonVO vo = ModelToolMapper.parseObject(entity, PersonVO.class);

        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey().toString())).withSelfRel());

        return vo;

    }

    public PersonVOV2 createV2(PersonVOV2 person) {

        logger.info("Creating a person with v2...");

        return personMapper.convertEntityToVo(repository.save(personMapper.convertVoToEntity(person)));
    }

    public void delete(Long id) {

        logger.info(String.format("Deleting person: %s", id));

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PersonVO not found - id: " + id.toString()));

        repository.delete(entity);
    }

    @Transactional
    public PersonVO disablePerson(Long id) {

        logger.info("Disabling one person...");

        repository.disablePerson(id);

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PersonVO not found - id: " + id.toString()));

        PersonVO vo = ModelToolMapper.parseObject(entity, PersonVO.class);

        vo.add(linkTo(methodOn(PersonController.class).findById(id.toString())).withSelfRel());

        return vo;
    }

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {

        logger.info("Finding people...");

        var personPage = repository.findAll(pageable);

        var personVosPage = personPage.map(p -> ModelToolMapper.parseObject(p, PersonVO.class));
        personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey().toString())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "asc")).withSelfRel();

        return assembler.toModel(personVosPage, link);
    }

    public PersonVO findById(Long id) {

        logger.info("Finding one person...");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("PersonVO not found - id: " + id.toString()));

        PersonVO vo = ModelToolMapper.parseObject(entity, PersonVO.class);

        vo.add(linkTo(methodOn(PersonController.class).findById(id.toString())).withSelfRel());

        return vo;
    }

    public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pageable) {

        logger.info("Finding people by a specified name...");

        var personPage = repository.findPersonsByName(firstName, pageable);

        var personVosPage = personPage.map(p -> ModelToolMapper.parseObject(p, PersonVO.class));
        personVosPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey().toString())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class)
                .findAll(pageable.getPageNumber(),
                        pageable.getPageSize(),
                        "asc")).withSelfRel();

        return assembler.toModel(personVosPage, link);
    }

    public PersonVO update(PersonVO person) {

        if (person == null) {

            throw new RequiredObjectIsNullException();
        }

        logger.info(String.format("Updating person: %s", person.getKey()));

        var key = person.getKey();

        var entity = repository.findById(key).orElseThrow(() -> new ResourceNotFoundException("PersonVO not found - id: " + key.toString()));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        PersonVO vo = ModelToolMapper.parseObject(repository.save(entity), PersonVO.class);

        vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey().toString())).withSelfRel());

        return vo;
    }

    private PersonVO mockPerson(int i) {

        PersonVO person = new PersonVO();

        person.setKey(Long.valueOf(i));
        person.setFirstName("Nome" + i);
        person.setLastName("Sobrenome" + i);
        person.setAddress("Endereço" + i);
        person.setGender(i % 2 == 0 ? "Male" : "Female");

        return person;
    }
}
