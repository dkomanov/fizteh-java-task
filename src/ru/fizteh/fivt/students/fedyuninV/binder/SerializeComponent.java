package ru.fizteh.fivt.students.fedyuninV.binder;

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

    public void setGetter(Method method) {
        getter = method;
    }

    public void setSetter(Method method) {
        setter = method;
    }

    public void setName(String newName) {
        name = newName;
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
