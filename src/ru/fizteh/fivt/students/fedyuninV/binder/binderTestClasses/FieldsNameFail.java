package ru.fizteh.fivt.students.fedyuninV.binder.binderTestClasses;

import ru.fizteh.fivt.bind.AsXmlElement;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */


/*
Contains 2 fields with the sam name in @AsXmlElement
 */
@BindingType(value = MembersToBind.FIELDS)
public class FieldsNameFail {

    @AsXmlElement(name = "wtf")
    private int declaredField1 = 1;

    @AsXmlElement(name = "wtf")
    private double declaredField2 = 2;
}
