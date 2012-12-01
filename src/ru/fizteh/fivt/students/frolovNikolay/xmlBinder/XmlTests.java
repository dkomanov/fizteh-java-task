package ru.fizteh.fivt.students.frolovNikolay.xmlBinder;

import org.junit.*;
import org.junit.rules.ExpectedException;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

public class XmlTests {
    
    private static XmlBinder<User> binder = new XmlBinder<User>(User.class);;
    
    @Rule
    public ExpectedException expt = ExpectedException.none();
    
    @Test
    public void nullPointer() {
        expt.expectMessage("null pointer");
        binder.serialize(null);
    }
    
    @Test
    public void emptyString() throws Throwable {
        expt.expectMessage("this String don't have first character");
        XmlBinder.lowerFirstCharacter(new String(""));
    }
    
    @Test
    public void usersTest() {
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
        Assert.assertTrue(user != deserialized);
    }
    
}
