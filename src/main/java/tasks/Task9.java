package tasks;

import common.Person;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Далее вы увидите код, который специально написан максимально плохо.
Постарайтесь без ругани привести его в надлежащий вид
P.S. Код в целом рабочий (не везде), комментарии оставлен чтобы вам проще понять чего же хотел автор
P.P.S Здесь ваши правки необходимо прокомментировать (можно в коде, можно в PR на Github)
 */
public class Task9 {

  private long count;

  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй
  public List<String> getNames(List<Person> persons) {
    // if (persons.size() == 0) { // не нужно обрабатывать случай пустой коллекции отдельно, тк Stream API, понятное дело,
      // корректно обрабатывает пустые коллекции => тут это лишний код без какого-либо смысла с тз техники или логики
      // с учётом того, что persons.remove(0); мы тоже удалим (см. далее)
      //return Collections.emptyList();
    //}
    // persons.remove(0); // не нужно делать удаление элемента тк 1) испортим переданную коллекцию 2) лишнее,
    // вероятно (скорее всего, на самой популярной реализации а-ля ArrayList) дорогое действие по сдвигу элементов влево
    // 3) получим UnsupportedOperationException, если действие не поддерживается в переданной реализации списка
    // 4) теперь получим IndexOutOfBoundsException на пустом списке, тк убрали проверку на пустую коллекцию выше

    // return persons.stream().map(Person::firstName).collect(Collectors.toList()); // видимо имя null допустимо

    // выполним вместо удаления всегда дешёвую операцию skip:
    return persons.stream().skip(1).map(Person::firstName).collect(Collectors.toList());
    // Дока гласит "If this stream contains fewer than n elements then an empty stream will be returned"
    // то есть если список пуст или содержит 1 элемент, то будет возвращён пустой список -- что нам и нужно!
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  public Set<String> getDifferentNames(List<Person> persons) {
    // return getNames(persons).stream().distinct().collect(Collectors.toSet());
    // return getNames(persons).stream().collect(Collectors.toSet()); // distinct() не нужен, тк строчки и так собираются в Set
    return new HashSet<>(getNames(persons)); // да и вообще можно (нужно) через конструктор HashSet
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО
  public String convertPersonToString(Person person) {
    // некрасиво и трудночитаемо - много однотипного тривиального кода проверок и конкатенаций
    /*
    String result = "";
    if (person.secondName() != null) {
      result += person.secondName();
    }

    if (person.firstName() != null) {
      result += " " + person.firstName();
    }

    if (person.secondName() != null) {
      result += " " + person.secondName(); // ОШИБКА: вместо отчества опять фамилия
    }
    return result;

    */

    // вместо этого используем Stream строк, фильтруя их на наличие и непустоту:
    return Stream.of(person.secondName(), person.firstName(), person.middleName())
            .filter(name -> name != null && !name.isEmpty())
            .collect(Collectors.joining(" ")); // и объединим через пробел
  }

  // словарь id персоны -> ее имя
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
    // Код сложночитаем, при этом не несёт никакой нетривиальной логики
    /*
    Map<Integer, String> map = new HashMap<>(1); // ОШИБКА: если уж задавать capacity, то стоило бы ориентироваться на
     // ... persons.size(). capacity == 1 почти наверняка вызовет лишние действия в начале заполнения карты
    for (Person person : persons) {
      if (!map.containsKey(person.id())) { // мне кажется это лишняя проверка, тк вставить заменой
      // ("If the map previously contained a mapping for the key, the old value is replaced by the specified value.")
      // элемент в карту почти так же дёшево, как проверить его наличие, можно было бы сказать что так гарантируется,
      // что при совпадении ID будет сохранён первый Person, но это неверно, аргумент метода типа Collection (не List!)
      // в общем случае неупорядочен
      map.put(person.id(), convertPersonToString(person));
      }
    }
    return map;
    */
    return persons.stream().collect(Collectors.toMap(Person::id, this::convertPersonToString));
  }

  // есть ли совпадающие в двух коллекциях персоны?
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    // Код сложночитаем, при этом не несёт никакой нетривиальной логики
    /*
    boolean has = false;
    for (Person person1 : persons1) {
      for (Person person2 : persons2) {
        if (person1.equals(person2)) {
          has = true; // ОШИБКА: уже нашли совпадающий элемент, но зачем-то бежим дальше, выходя во всех случаях
          // на максимальную сложность O(n*m)
        }
      }
    }
    return has;
     */

    // буквально: содержит ли первая коллекция какой-либо элемент, который также содержит вторая коллекция
    // возращает true уже на первом совпадении (anyMatch)
     return persons1.stream().anyMatch(persons2::contains);
  }

  // Посчитать число четных чисел
  public long countEven(Stream<Integer> numbers) {
    /*
    count = 0;
    numbers.filter(num -> num % 2 == 0).forEach(num -> count++);
    return count;
     */
    // уберём лишнюю переменную и действия с ней: для подсчёта количества есть встроенный метод
    return numbers.filter(num -> num % 2 == 0).count();
  }

  // Загадка - объясните почему assert тут всегда верен
  // Пояснение в чем соль - мы перетасовали числа, обернули в HashSet, а toString() у него вернул их в сортированном порядке
  void listVsSet() {
    List<Integer> integers = IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList());
    List<Integer> snapshot = new ArrayList<>(integers);
    Collections.shuffle(integers);
    Set<Integer> set = new HashSet<>(integers);
    assert snapshot.toString().equals(set.toString());
    /* ПОЯСНЕНИЕ:
    - Метод toString класса HashMap (его использует HashSet) выводит элементы последовательно с пощью итератора (это логика
     наследуется от AbstractCollection)
    - Итератор по HashMap идёт самым естественным образом: идёт вперёд по массиву от меньшего индекса к большему по всем
    не-null значениям ("do {} while (index < t.length && (next = t[index++]) == null);")
    - То есть дело в том, что элементы в массиве корзин в данном примере хранятся в порядке возрастания значений элементов
     (это видно также в дебагере)... ПОЧЕМУ?
    - Для того, чтобы определить порядковый номер корзины в массиве, вычисляется хэш-функция hashCode, у Integer это
    само число. Далее, для того, чтобы получить индекс в существующем массиве (по умолчению изначальная размерность 16),
     к этому значению применяется вторичная хэшфункция с побитовым сдвигом
    - При каждом достижении loadFactor внутренний массив "растёт", происходит перекопирование массива корзин с пересчётом
    хэшей всех Integer для массива всё большего размера
    - На очередном шаге, когда уже все хэши - подряд идущие целые положительные числа - могут уместиться в новом массиве,
    работа вторичной хэш-функции с побитовым сдвигом автоматически становится вырожденной - она ничего не делает, ей
    "нечего делать", коллизии исчезают и все элементы лежат сплошной последовательностью во внутренем массиве:
    один Integer в корзине с номером, равным этому Integer, после всех подряд идущих Integer несколько тысяч null-значений,
     на первом из которых итератор при переборе и останавливается
     */
  }
}
