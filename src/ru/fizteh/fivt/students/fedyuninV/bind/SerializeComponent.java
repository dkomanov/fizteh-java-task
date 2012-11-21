package ru.fizteh.fivt.students.fedyuninV.bind;

import java.lang.reflect.Method;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
class SerializeComponent {
    private Method getter;
    private Method setter;
    private String name;

    public SerializeComponent(String name) {
        getter = null;
        setter = null;
        this.name = name;
    }

    /*
    Setter return false if this Method is already set.
    Mode: 's' - try to set this.setter.
           default - try to set this.getter.
     */

    public boolean setMethod(Method method, char mode) {
        if (mode == 's') {
            if (setter != null) {
                return false;
            }
            setter = method;
        } else {
            if (getter != null) {
                return false;
            }
            getter = method;
        }
        return true;
    }

    public Method getter() {
        return getter;
    }

    public Method setter() {
        return setter;
    }

    public String getName() {
        return name;
    }
}
