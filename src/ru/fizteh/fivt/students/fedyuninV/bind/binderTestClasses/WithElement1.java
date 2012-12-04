package ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses;

import ru.fizteh.fivt.bind.AsXmlElement;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

@BindingType(MembersToBind.GETTERS_AND_SETTERS)
public class WithElement1 {
    private String value1;
    private String value2;
    private String value3;

    @AsXmlElement(name = "value-1")
    public String getValue1() {
        return value1;
    }

    @AsXmlElement(name = "value-l")  //look carefully at the names...
    public void setValue1(String value1) {
        this.value1 = value1;
    }

    @AsXmlElement(name = "value-2")
    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getValue3() {
        return value3;
    }

    @AsXmlElement(name = "value-3")
    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public boolean equals(WithElement1 x) {
        return (value1.equals(x.getValue1())  &&  value2.equals(x.getValue2())  &&  value3.equals(x.getValue3()));
    }
}