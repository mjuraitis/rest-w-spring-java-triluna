package br.com.triluna.integrationtest.vo.wrapper;

import br.com.triluna.integrationtest.vo.PersonVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PersonEmbeddedVO implements Serializable {

    @JsonProperty("personVOList")
    private List<PersonVO> peopleVO;

    public PersonEmbeddedVO() { }

    public List<PersonVO> getPeopleVO() {
        return peopleVO;
    }

    public void setPeopleVO(List<PersonVO> peopleVO) {
        this.peopleVO = peopleVO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEmbeddedVO that = (PersonEmbeddedVO) o;
        return Objects.equals(peopleVO, that.peopleVO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(peopleVO);
    }
}
