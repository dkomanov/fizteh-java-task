package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.chat.MessageType;

@BindingType(value = MembersToBind.GETTERS_AND_SETTERS)
public class ClassForSerialization {
    private int intField = 11;
    double doubleField = 1.1;
    public MessageType enumField;

    public int get() {
        return 0;
    }

    public void set(int i) {
        return;
    }

    void setIntField(int i) {
        intField = i;
    }

    private int getIntField() {
        return intField;
    }
}