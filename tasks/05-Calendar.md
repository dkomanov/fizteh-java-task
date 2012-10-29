## Calendar
Консольное приложение, выводящее календарь.

```
java Calendar [-m MONTH] [-y YEAR] [-w] [-t TIMEZONE]
```

Ключи:
* -m &mdash; месяц, за который необходимо выводить календарь (по умолчанию
текущий). Задаётся числами от 1 до 12.
* -y &mdash; год (по умолчанию текущий). 4 цифры.
* -w &mdash; если указан, выводит номер недели
* -t &mdash; если указан, выводит под календарём текущее время в указанной
временной зоне (для календаря должна быть использована эта временная зона).

Пример вывода ```java Calendar -m 9 -y 2012```:
```
   September 2012
Mo Tu We Th Fr Sa Su
                1  2
 3  4  5  6  7  8  9
10 11 12 13 14 15 16
17 18 19 20 21 22 23
24 25 26 27 28 29 30
```

Пример вывода ```java Calendar -m 9 -y 2012 -w```:
```
      September 2012
   Mo Tu We Th Fr Sa Su
35                 1  2
36  3  4  5  6  7  8  9
37 10 11 12 13 14 15 16
38 17 18 19 20 21 22 23
39 24 25 26 27 28 29 30
```

Пример вывода ```java Calendar -m 9 -y 2012 -w -t Asia/Omsk```:
```
      September 2012
   Mo Tu We Th Fr Sa Su
35                 1  2
36  3  4  5  6  7  8  9
37 10 11 12 13 14 15 16
38 17 18 19 20 21 22 23
39 24 25 26 27 28 29 30

Now: 2012.10.19 15:25:00 Omsk Time
```

[Пример работы с классами Calendar, TimeZone и SimpleDateFormat]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/examples/CalendarExample.java)
