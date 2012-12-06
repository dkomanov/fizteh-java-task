package ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses;

import ru.fizteh.fivt.bind.AsXmlElement;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
@BindingType(value = MembersToBind.GETTERS_AND_SETTERS)
public class MethodsAnnotationsFail {
    String name;

    @AsXmlElement(name = "wtf")
    public String getName() {
        return name;
    }

    @AsXmlElement               //non-void + void = fail
    public void setName(String name) {
        this.name = name;
    }
}
