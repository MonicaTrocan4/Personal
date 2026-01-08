package com.example.demo.services;

import com.example.demo.dtos.*;
import com.example.demo.dtos.builders.PersonBuilder;
import com.example.demo.entities.Person;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final RestTemplate restTemplate;

    @Value("${device.service.url:http://localhost:8081/sync/users}")
    private String DEVICE_CONNECTION_URL;

    @Autowired
    public PersonService(PersonRepository personRepository, RestTemplate restTemplate) {
        this.personRepository = personRepository;
        this.restTemplate = restTemplate;
    }

    public List<PersonDetailsDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDetailsDTO)
                .collect(Collectors.toList());
    }

    public PersonDetailsDTO findPersonById(UUID id) {
        Optional<Person> prosumerOptional = personRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(prosumerOptional.get());
    }


    public UUID insert(PersonDetailsDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);

        if (personDTO.getId() != null) {
            person.setId(personDTO.getId());
        } else {
            person.setId(UUID.randomUUID());
        }

        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was inserted in db", person.getId());

        try {
            UserToDevicesDTO syncUser = new UserToDevicesDTO(person.getId(), person.getName());
            restTemplate.postForObject(DEVICE_CONNECTION_URL, syncUser, Void.class);
        } catch (Exception e) {
            LOGGER.error("Error syncing user to Device Service: {}", e.getMessage());
        }

        return person.getId();
    }

    public PersonDetailsDTO update(UUID id, PersonDetailsDTO personDetailsDTO) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }

        Person personToUpdate = personOptional.get();

        personToUpdate.setName(personDetailsDTO.getName());
        personToUpdate.setAddress(personDetailsDTO.getAddress());
        personToUpdate.setAge(personDetailsDTO.getAge());
        personToUpdate.setRole(personDetailsDTO.getRole());

        Person updatedPerson = personRepository.save(personToUpdate);

        LOGGER.debug("Person with id {} was updated in db", updatedPerson.getId());

        try {
            restTemplate.postForObject(DEVICE_CONNECTION_URL, new UserToDevicesDTO(updatedPerson.getId(), updatedPerson.getName()), Void.class);
        } catch (Exception e) {
            LOGGER.error("Error syncing user to Device Service: {}", e.getMessage());
        }

        return PersonBuilder.toPersonDetailsDTO(updatedPerson);
    }

    public UUID delete(UUID id) {
        Optional<Person> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }

        personRepository.deleteById(id);

        LOGGER.debug("Person with id {} was deleted from db", id);

        try {
            restTemplate.delete(DEVICE_CONNECTION_URL + "/" + id);
            LOGGER.info("Person with id {} was deleted from device-db", id);
        } catch (Exception e) {
            LOGGER.error("Error syncing user to Device Service: {}", e.getMessage());
        }

        return id;
    }
}
