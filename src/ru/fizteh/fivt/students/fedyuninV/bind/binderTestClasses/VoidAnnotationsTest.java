package ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses;

import ru.fizteh.fivt.bind.AsXmlElement;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
@BindingType(value = MembersToBind.GETTERS_AND_SETTERS)
public class VoidAnnotationsTest {
    String name;
    String surname;
    int age;

    public VoidAnnotationsTest(String name, String surname, int age) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    @AsXmlElement
    public String getName() {
        return name;
    }

    @AsXmlElement(name = "wtf")
    public String getSurname() {
        return surname;
    }

    @AsXmlElement
    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    @AsXmlElement
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @AsXmlElement
    public void setAge(int age) {
        this.age = age;
    }

    public boolean equals(VoidAnnotationsTest x) {
        return (name.equals(x.getName())  &&  surname.equals(x.getSurname())  &&  age == x.getAge());
    }
}
