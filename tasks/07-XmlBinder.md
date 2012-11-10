## XmlBinder
Фреймворк для сериализации-десериализации объектов.

Необходимо написать реализацию абстрактного класса [XmlBinder]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/bind/XmlBinder.java).

Помимо реализации класса должен быть класс со статическим методом
```main(String[] args)```, в котором выполняются проверки на корректность
работы класса (unit-тесты).

XmlBinder поддерживает неабстрактные классы. В качестве корневого элемента
используется название класса (первая буква приведена к нижнему регистру).
Для сериализации используются данные, указанные в аннотации [BindingType]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/bind/BindingType.java).
Если аннотация ```@BindingType``` не указана, то сериализуются/десериализуются
все поля. По умолчанию поля/методы сериализуются в элементы,

В качестве полей должны быть поддержаны все примитивные типы и перечисления.

### Пример
```
XmlBinder<User> binder = ...;
Permissions permissions = new Permissions();
permissions.setQuota(100500);
User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
byte[] bytes = binder.serialize(user);
User deserialized = binder.deserialize(bytes);
assert user != deserialized;
assert user.equals(deserialized);
```

В результате сериализации должен получиться такого вида XML (переносы и выравнивание
не обязательны):
```
<user>
    <id>1</id>
    <userType>USER</userType>
    <name>
        <firstName>first</firstName>
        <lastName>last</lastName>
    </name>
    <permissions>
        <root>false</root>
        <quota>100500</quota>
    </permissions>
</user>
```

### Варианты
Разные варианты реализуют поддержку дополнительной аннотации для
управления форматом XML-документа.

#### 1
[AsXmlAttribute]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/bind/AsXmlAttribute.java).

#### 2
[AsXmlCdata]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/bind/AsXmlCdata.java).

#### 3
[AsXmlElement]
(https://github.com/dkomanov/fizteh-java-task/blob/master/src/ru/fizteh/fivt/bind/AsXmlElement.java).
