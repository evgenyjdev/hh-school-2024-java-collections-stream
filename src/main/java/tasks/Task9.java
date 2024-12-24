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

  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй
  public List<String> getNames(List<Person> persons) {
    // выполним вместо удаления всегда дешёвую операцию skip:
    return persons.stream().skip(1).map(Person::firstName).collect(Collectors.toList());
    // Дока гласит "If this stream contains fewer than n elements then an empty stream will be returned"
    // то есть если список пуст или содержит 1 элемент, то будет возвращён пустой список -- что нам и нужно!
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  public Set<String> getDifferentNames(List<Person> persons) {
    return new HashSet<>(getNames(persons));
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО
  public String convertPersonToString(Person person) {
    //вместо этого используем Stream строк, фильтруя их на наличие и непустоту:
    return Stream.of(person.secondName(), person.firstName(), person.middleName())
            .filter(name -> name != null && !name.isEmpty())
            .collect(Collectors.joining(" ")); // и объединим через пробел
  }

  // словарь id персоны -> ее имя
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
      // при совпадении ключей оставляем ранее вставленное значение как в исходном коде:
      return persons.stream().collect(Collectors.toMap(Person::id, this::convertPersonToString, (existing, replacement) -> existing));
  }

  // есть ли совпадающие в двух коллекциях персоны?
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
     return persons1.stream().anyMatch(persons2::contains);
  }

  // Посчитать число четных чисел
  public long countEven(Stream<Integer> numbers) {
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
    само число. Далее, для того, чтобы получить индекс в существующем массиве,
     к этому значению применяется вторичная хэшфункция с побитовым сдвигом
    - При каждом достижении loadFactor внутренний массив "растёт", происходит перекопирование массива корзин с пересчётом
    хэшей всех Integer для массива всё большего размера
    - На очередном шаге, когда уже все хэши - подряд идущие целые положительные числа от 1 до N - могут уместиться в новом массиве,
    работа вторичной хэш-функции с побитовым сдвигом автоматически становится вырожденной - она ничего не делает, ей
    "нечего делать", коллизии исчезают и все элементы лежат сплошной последовательностью во внутренем массиве:
    один Integer в корзине с номером, равным этому Integer, после всех подряд идущих Integer несколько тысяч null-значений,
     пропустив которые итератор и остановится в конце массива
     */
  }
}
