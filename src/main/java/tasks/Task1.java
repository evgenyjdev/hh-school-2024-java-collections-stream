package tasks;

import common.Person;
import common.PersonService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
Задача 1
Метод на входе принимает List<Integer> id людей, ходит за ними в сервис
(он выдает несортированный Set<Person>, внутренняя работа сервиса неизвестна)
нужно их отсортировать в том же порядке, что и переданные id.
Оценить асимптотику работы
 */
public class Task1 {

  private final PersonService personService;

  public Task1(PersonService personService) {
    this.personService = personService;
  }

  public List<Person> findOrderedPersons(List<Integer> personIds) {
    Set<Person> persons = personService.findPersons(personIds);
    Map<Integer, Person> idToPerson = persons.stream().collect(Collectors.toMap(Person::id, Function.identity()));
    return personIds.stream().map(idToPerson::get).collect(Collectors.toList());
    /*
    СЛОЖНОСТЬ:
    1) карта строится за O(n), тк вставка в карту O(1), т. к. хэш-функция хорошая (она равно самому значению int)
    2) список формируется за O(n), тк получение из карты O(1)
    3) Итого O(n)
     */
  }
}
