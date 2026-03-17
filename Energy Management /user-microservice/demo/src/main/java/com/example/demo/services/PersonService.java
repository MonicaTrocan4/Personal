package com.example.demo.services;

import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dtos.PersonDetailsDTO;
import com.example.demo.dtos.UserToDevicesDTO;
import com.example.demo.dtos.builders.PersonBuilder;
import com.example.demo.entities.Person;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PersonService(PersonRepository personRepository, RabbitTemplate rabbitTemplate) {
        this.personRepository = personRepository;
        this.rabbitTemplate = rabbitTemplate;
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
        person.setId(personDTO.getId() != null ? personDTO.getId() : UUID.randomUUID());

        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was inserted in db", person.getId());

        sendSyncMessage(person.getId(), person.getName());

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

        sendSyncMessage(updatedPerson.getId(), updatedPerson.getName());

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

        sendSyncMessage(id, null);

        return id;
    }

    private void sendSyncMessage(UUID id, String name) {
        try {
            UserToDevicesDTO syncDto = new UserToDevicesDTO(id, name);

            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, syncDto);

            LOGGER.info("Sent sync message: {}", syncDto);
        } catch (Exception e) {
            LOGGER.error("Error sending message to RabbitMQ: {}", e.getMessage());
        }
    }
}