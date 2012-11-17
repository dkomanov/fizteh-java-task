package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import org.junit.Assert;
import ru.fizteh.fivt.bind.test.User;
import org.junit.Test;

public class XmlBinderTest extends Assert {
    @Test(expected = RuntimeException.class)
    public void testValueNullPointer() {
        new XmlBinder(User.class).serialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void testIncorrectTypeOfValue() {
        new XmlBinder(User.class).serialize(new String("Hello, world!"));
    }
}
