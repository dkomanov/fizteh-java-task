package ru.fizteh.fivt.students.myhinMihail.xmlBinder;

import org.junit.*;
import ru.fizteh.fivt.bind.test.*;

public class UnitTests extends Assert {
    
    @Test(expected = RuntimeException.class)
    public void nullPointer() {
        new XmlBinder<User>(User.class).serialize(null);
    }
    
    @Test(expected = RuntimeException.class)
    public void nullPointer2() {
        new XmlBinder<Integer>(Integer.class).deserialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void badClass() {
        new XmlBinder<Integer>(Integer.class).deserialize("<user></user>".getBytes());
    }

    @Test(expected = RuntimeException.class)
    public void badClass2() {
        new XmlBinder(User.class).serialize(new Integer(123));
    }

    @Test
    public void goodTest() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
        Assert.assertTrue(user != deserialized);
    }

}
