package com.example.demo.dtos.builders;

import com.example.demo.dtos.PersonDTO;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(),
                person.getName(),
                person.getAge(),
                person.getRole());
    }

    public static PersonDetailsDTO toPersonDetailsDTO(Person person) {
        return new PersonDetailsDTO(person.getId(),
                person.getName(),
                person.getAddress(),
                person.getAge(),
                person.getRole());
    }

    public static Person toEntity(PersonDetailsDTO personDetailsDTO) {
        Person person = new Person(personDetailsDTO.getName(),
                personDetailsDTO.getAddress(),
                personDetailsDTO.getAge(),
                personDetailsDTO.getRole());
        person.setId(personDetailsDTO.getId());

        return person;
    }
}