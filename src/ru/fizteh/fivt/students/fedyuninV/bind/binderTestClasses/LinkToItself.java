package ru.fizteh.fivt.students.fedyuninV.bind.binderTestClasses;

import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
@BindingType(value = MembersToBind.GETTERS_AND_SETTERS)
public class LinkToItself {

    private int x;
    private LinkToItself link;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public LinkToItself getLink() {
        return link;
    }

    public void setLink(LinkToItself link) {
        this.link = link;
    }
}
