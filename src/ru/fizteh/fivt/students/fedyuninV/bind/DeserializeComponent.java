package ru.fizteh.fivt.students.fedyuninV.bind;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */

public class DeserializeComponent {
    DeserializeComponentType type;
    Field field;
    Method method;

    public DeserializeComponent(Field field) {
        this.field = field;
        type = DeserializeComponentType.FIELD;
    }

    public DeserializeComponent(Method method) {
        this.method = method;
        type = DeserializeComponentType.METHOD;
    }
}
