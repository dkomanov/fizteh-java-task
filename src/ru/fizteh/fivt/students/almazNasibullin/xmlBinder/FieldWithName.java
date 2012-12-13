package ru.fizteh.fivt.students.almazNasibullin.xmlBinder;

import java.lang.reflect.Field;

/**
 * 1.12.12
 * @author almaz
 */

public class FieldWithName {
    public Field f;
    public String name;

    public FieldWithName(Field f) {
        this.f = f;
        this.name = f.getName();
    }
}
