package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import org.junit.Assert;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import org.junit.Test;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

public class XmlBinderTest extends Assert {
    @Test(expected = RuntimeException.class)
    public void testValueNullPointer() {
        new XmlBinder(User.class).serialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void testIncorrectTypeOfValue() {
        new XmlBinder(User.class).serialize(new String("Hello, world!"));
    }

    @Test(expected = RuntimeException.class)
    public void testRecursiveClass() {
        new XmlBinder(BadClassForSerialization.class).serialize(new BadClassForSerialization());
    }

    @Test
    public void testXmlBuilder() {
        XmlBinder binder = new XmlBinder(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        /*assertEquals("<ru.fizteh.fivt.bind.test.User>"
                + "<id>1</id>"
                + "<userType>USER</userType>"
                + "<name><firstName>first</firstName><lastName>last</lastName></name>"
                + "<permissions><quota>100500</quota><root>false</root></permissions>"
                + "</ru.fizteh.fivt.bind.test.User>", new String(bytes));
        */
        XmlBinder anotherBinder = new XmlBinder(ClassForSerialization.class);
        assertEquals("<ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerialization>"
                + "<intField><![CDATA[11]]></intField>"
                + "</ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerialization>",
                new String(anotherBinder.serialize(new ClassForSerialization())));
        binder.deserialize(binder.serialize(user));
    }
}
