package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import ru.fizteh.fivt.bind.AsXmlCdata;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.chat.MessageType;

@BindingType(value = MembersToBind.FIELDS)
public class ClassForSerialization {
    public int intField = 11;
    public double doubleField = 1.1;
    public MessageType enumField = MessageType.MESSAGE;

    public int get() {
        return 0;
    }

    public void set(int i) {
        return;
    }

    //@AsXmlCdata
    public void setIntField(int i) {
        intField = i;
    }

    //@AsXmlCdata
    public int getIntField() {
        return intField;
    }

    int getIntField(int i) {
        return 0;
    }

    boolean setIntField() {
        return false;
    }

    public void setDouble(float f) {
        return;
    }

    public double getDouble() {
        return doubleField;
    }
}