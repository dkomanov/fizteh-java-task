## StringFormatter
Фреймворк для форматирования строк.

Необходимо написать реализации интерфейсов [StringFormatter]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/format/StringFormatter.java)
и [StringFormatterFactory]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/format/StringFormatterFactory.java),
а также по два наследника от [StringFormatterExtension]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/format/StringFormatterExtension.java)
для каждого варианта. По выполнению задания должно быть 5 классов: перечисленные плюс класс с функцией main(),
в которой выполняется несколько проверок (unit-тестов) для собственных классов
(должны быть покрыты все строки кода), в том числе проверка того, что
выбрасываются нужные исключения во всех возможных случаях.

Формат строки:
```
Любые символы {0}, {0:pattern}, {0.field}, {1.field.field:pattern}
```

Число означает номер аргумента в массиве args (нумерация с нуля). После точки должно
осуществляться получение соответствующего поля у объекта. После первого двоеточия
начинается шаблон (pattern) форматирования полученного значения. За форматирование
по шаблону отвечают классы StringFormatterExtension.

Форматирование объекта, если шаблон не указан, должно выполняться с помощью
метода toString().

В случае null всегда выводится пустая строка.

Экранирование открывающейся фигурной скобки: ```{{```, зарывающей: ```}}```.
Экранирование делается слева-направо, т.е. ```{{{0}}}``` означает, что надо напечатать ```{аргумент}```.

При возникновении любых ошибок должно выбрасываться исключение FormatterException,
и только оно.

Все классы должны быть Thread-Safe.

Важна производительность работы, поскольку форматирование строк &mdash; потенциально
частая операция.

### Варианты
Разные варианты реализуют StringFormatterExtension для разных типов
#### 1
* java.util.Date (с помощью SimpleDateFormat)
* java.lang.Integer

#### 2
* java.util.Calendar (с помощью SimpleDateFormat)
* java.lang.Long

#### 3
* [b (массив байтов, с помощью Arrays.toString)
* java.lang.Float

#### 4
* java.lang.Double
* java.math.BigInteger
