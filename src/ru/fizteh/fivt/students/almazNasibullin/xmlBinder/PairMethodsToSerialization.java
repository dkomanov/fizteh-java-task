package ru.fizteh.fivt.students.almazNasibullin.xmlBinder;

import java.lang.reflect.Method;

/**
 * 26.11.12
 * @author almaz
 */

public class PairMethodsToSerialization {
    public String name;
    public Method getter = null;
    public Method setter = null;

    PairMethodsToSerialization(String name) {
        this.name = name;
    }

    public void setMethod(Method m, String getterOrSetter) {
        if (getterOrSetter.equals("getter")) {
            if (getter != null) {
                throw new RuntimeException("There are two same method-getters in class");
            }
            getter = m;
        } else {
            if (setter != null) {
                throw new RuntimeException("There are two same method-setters in class");
            }
            setter = m;
        }
    }
}
