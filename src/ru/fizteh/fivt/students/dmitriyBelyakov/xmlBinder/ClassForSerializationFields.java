package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import ru.fizteh.fivt.bind.AsXmlCdata;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.chat.MessageType;

public class ClassForSerializationFields {
    int intField = 11;
    private double doubleField = 1.1;
    public MessageType enumField = MessageType.MESSAGE;
    Boolean boolField = false;
    Byte byteField = 12;
    @AsXmlCdata
    char c = 'D';
    Short shField = 21;
    long longField = 2012;
    Float flField = 1.993f;
    @AsXmlCdata
    String stringField = "fizteh-java-task";
    @AsXmlCdata
    Object nullPointer = null;

    @Override
    public boolean equals(Object o) {
        ClassForSerializationFields object = (ClassForSerializationFields) o;
        return intField == object.intField
                && doubleField == object.doubleField
                && enumField.equals(object.enumField)
                && boolField.equals(object.boolField)
                && byteField.equals(object.byteField)
                && c == object.c
                && shField.equals(object.shField)
                && longField == object.longField
                && flField.equals(object.flField)
                && stringField.equals(object.stringField)
                && nullPointer == object.nullPointer;
    }

    public int get() {
        return 0;
    }

    public void set(int i) {
        return;
    }

    @AsXmlCdata
    public void setIntField(int i) {
        intField = i;
    }

    @AsXmlCdata
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