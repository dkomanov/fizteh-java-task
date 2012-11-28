package ru.fizteh.fivt.students.almazNasibullin.xmlBinder;

import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.bind.AsXmlAttribute;
import ru.fizteh.fivt.bind.BindingType;

/**
 * 28.11.12
 * @author almaz
 */

@BindingType(MembersToBind.GETTERS_AND_SETTERS)
public class TestClassSerializationMethods {
    private int number = 1;
    protected String name = "John";
    boolean isGood = true;

    @AsXmlAttribute(name = "newNumber")
    public int getNumber() {
        return number;
    }

    @AsXmlAttribute(name = "newNumber")
    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean isGood) {
        this.isGood = isGood;
    }

    @Override
    public boolean equals(Object o) {
        TestClassSerializationMethods sm = (TestClassSerializationMethods)o;
        return sm.getNumber() == number && sm.getName().equals(name) && sm.isGood() == isGood;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.number;
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 71 * hash + (this.isGood ? 1 : 0);
        return hash;
    }
}
