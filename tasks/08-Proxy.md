## Proxy
Проксирование с помощью java.lang.reflect.Proxy.

В зависимости от варианта, надо реализовать соответствующий интерфейс фабрики,
а также написать класс со статическим методом ```main(String[] args)```,
в котором выполняются проверки на корректность работы класса (unit-тесты).

### Вариант 1 (Логирующий прокси)
Необходимо написать реализацию интерфейса [LoggingProxyFactory]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/proxy/LoggingProxyFactory.java).

#### Формат отображения объектов
* Примитивные типы отображаются с помощью toString()
* Строки отображаются в двойных кавычках с экранированием управляющих символов
(т.е. эту строку можно будет вставить в исходный файл на Java)
* Перечисления (enum) отображатся с помощью name()
* Объекты отображаются в квадратных скобках результат toString() с экранированием
управляющих символов
* Массивы отображаются так: сначала длина массива, потом в фигурных скобках
элементы массива через запятую рекурсивно
* null отображается всегда строкой ```null```

#### Формат лога
Для метода из интерфейса List: ```int indexOf("abc")```:
```
List.indexOf("abc") returned -1
```

Для метода из интерфейса List: ```int indexOf(new Object())```:
```
List.indexOf([java.lang.Object@154ebadd]) returned 55
```

Для метода из интерфейса Collection: ```void clear()```:
```
Collection.clear()
```

Если метод выбросил исключение:
```
Collection.clear() threw java.lang.UnsupportedOperationException: Message
  at ru.fizteh.fivt.examples.Main.clear(Main.java:35)
  at ru.fizteh.fivt.examples.Main.main(Main.java:10)
```

Массив: ```void add(String[])```
```
Interface.add(2{"abc", "def"})
```

Если хотя бы один из параметров метода превышает 60 символов, то должна
использоваться расширенная запись:
```
MyInterface.method(
  [a very long result of toString() method of some object with huge amount of data],
  1,
  10
  )
  returned "abc" -- или, в случае исключения (двойной отступ)
  threw java.lang.UnsupportedOperationException: Message
    at ru.fizteh.fivt.examples.Main.clear(Main.java:35)
    at ru.fizteh.fivt.examples.Main.main(Main.java:10)
```

В качестве отступа используется два пробела.

### Вариант 2 (Прокси-диспетчер)
Необходимо написать реализацию интерфейса [ShardingProxyFactory]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/proxy/ShardingProxyFactory.java).

Роль прокси-класса - выполнять вызов метода нужной реализации в зависимости
от параметров метода. Мы будем использовать в качестве диспетчеризации первое
встреченное целое число (int или long) среди аргументов метода. Выбор объекта,
у которого будет производиться вызов будет осуществляться так:
```targets[numberArgument % targets.length]```

Плюс к этом необходимо поддержать аннотации [DoNotProxy](https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/proxy/DoNotProxy.java)
и [Collect](https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/proxy/Collect.java).
```@DoNotProxy``` говорит, что этот метод нельзя вызывать через прокси, а аннотация
```@Collect``` говорит, что необходимо вызвать все targets, а результаты работы
необходимо "слить" (поддерживаются типы void, int, long и List).
