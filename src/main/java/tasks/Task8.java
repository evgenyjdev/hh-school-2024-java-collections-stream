package tasks;

import common.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
  Еще один вариант задачи обогащения
  На вход имеем коллекцию персон
  Сервис умеет по personId искать их резюме (у каждой персоны может быть несколько резюме)
  На выходе хотим получить объекты с персоной и ее списком резюме
 */
public class Task8 {
  private final PersonService personService;

  public Task8(PersonService personService) {
    this.personService = personService;
  }

  public Set<PersonWithResumes> enrichPersonsWithResumes(Collection<Person> persons) {
      Map<Integer, Set<Resume>> personIdToResumes = personService.findResumes(persons.stream().map(Person::id).collect(Collectors.toList())).stream()
              .collect(Collectors.groupingBy(Resume::personId,
                       Collectors.mapping(Function.identity(), Collectors.toSet())));
      return persons.stream()
              .map(person -> new PersonWithResumes(person, personIdToResumes.getOrDefault(person.id(), Collections.emptySet())))
              .collect(Collectors.toSet());
  }
}
