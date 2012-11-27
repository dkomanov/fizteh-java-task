package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import ru.fizteh.fivt.bind.AsXmlCdata;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

@BindingType(value = MembersToBind.GETTERS_AND_SETTERS)
public class ClassForSerializationMethods {
    boolean isSmth = false;

    @AsXmlCdata
    public boolean isSomething() {
        return isSmth;
    }

    @AsXmlCdata
    public void setSomething(boolean smth) {
        isSmth = smth;
    }

    public void setSomething(int unused) {
        isSmth = false;
    }

    public long getSomething() {
        return 1;
    }

    public boolean isSmth() {
        return false;
    }

    public void setSmth(int i) {
        return;
    }

    public void setWithoutPair(String str) {
        return;
    }

    public void setIncorrect(int a, int b) {
        return;
    }

    @Override
    public boolean equals(Object o) {
        return isSmth == ((ClassForSerializationMethods) o).isSmth;
    }
}