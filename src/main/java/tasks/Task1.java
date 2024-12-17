package tasks;

import common.Person;
import common.PersonService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    // Видимо предполагается, что в personIds нет повторяющихся ID, тк метод findPersons уже может их вернуть в любом
    // порядке и его никак не восстановить
    Set<Person> persons = personService.findPersons(personIds);
    // Карта ID -> его порядковый номер в исходном списке
    //    Map<Integer, Integer> idToOrder = new HashMap<>();
    //    for (int i = 0; i < persons.size(); i++) {
    //        idToOrder.put(personIds.get(i), i);
    //    }
    Map<Integer, Integer> idToOrder = IntStream.range(0, personIds.size())
            .boxed()
            .collect(Collectors.toMap(personIds::get, Function.identity()));
    return persons.stream().sorted(Comparator.comparing(x -> idToOrder.get(x.id()))).collect(Collectors.toList());
    /*
    СЛОЖНОСТЬ:
    1) карта строится за O(n), тк получение элемента из списка по индексу O(1), вставка O(1),
    т. к. хэш-функция хорошая (она равно самому значению int)

    2) сортировка - в Java используется алгоритм Timsort с худшей сложностью n*log(n), на каждом шаге используется
    получение элемента из карты за O(1)

    3) Итого O(n*log(n)) (мы не испортили сложность стандартной сортировки)
     */
  }
}
