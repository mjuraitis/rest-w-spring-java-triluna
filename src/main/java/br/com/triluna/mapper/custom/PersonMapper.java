package br.com.triluna.mapper.custom;

import br.com.triluna.data.vo.v2.PersonVOV2;
import br.com.triluna.model.Person;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PersonMapper {

    public PersonVOV2 convertEntityToVo(Person person) {

        PersonVOV2 vo = new PersonVOV2();

        vo.setId(person.getId());
        vo.setFirstName(person.getFirstName());
        vo.setLastName(person.getLastName());
        vo.setAddress(person.getAddress());
        vo.setGender(person.getGender());
        vo.setBirthDate(new Date());

        return vo;
    }

    public Person convertVoToEntity(PersonVOV2 vo) {

        Person person = new Person();

        person.setId(vo.getId());
        person.setFirstName(vo.getFirstName());
        person.setLastName(vo.getLastName());
        person.setAddress(vo.getAddress());
        person.setGender(vo.getGender());
        // person.setBirthDate();

        return person;
    }
}
