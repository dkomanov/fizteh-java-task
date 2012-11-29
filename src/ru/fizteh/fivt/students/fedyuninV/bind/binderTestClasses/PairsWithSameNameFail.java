package ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses;

import ru.fizteh.fivt.bind.AsXmlElement;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
@BindingType(value = MembersToBind.GETTERS_AND_SETTERS)
public class PairsWithSameNameFail {
    private int x;
    private int y;

    @AsXmlElement(name = "wtf")
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    @AsXmlElement(name = "wtf")
    public void setY(int y) {
        this.y = y;
    }
}
