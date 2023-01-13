package de.gedoplan.showcase.rest;

import de.gedoplan.showcase.domain.Person;
import de.gedoplan.showcase.persistence.PersonRepository;
import de.gedoplan.showcase.service.PersonService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("persons")
public class PersonResource {

  @Inject
  PersonRepository personRepository;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<Person> get() {
    return this.personRepository.findAll();
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Transactional
  public Response post(Person person, @Context UriInfo uriInfo) {
    if (person.getId() != null) {
      throw new BadRequestException("Id must not be set");
    }

    this.personRepository.persist(person);

    URI uri = uriInfo
        .getAbsolutePathBuilder()
        .path(person.getId().toString())
        .build();
    return Response
        .created(uri)
        .build();
  }

  @Inject
  PersonService personService;

  @Path("avgAge")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public double getAverageAge() {
    return this.personService.getAverageAge();
  }

  @PostConstruct
  void initTestData() {
    if (this.personRepository.findAll().isEmpty()) {
      this.personRepository.persist(new Person("Dagobert Duck", LocalDate.of(1905,12,5)));
      this.personRepository.persist(new Person("Donald Duck", LocalDate.of(1931,3,13)));
    }
  }
}
