package ru.fizteh.fivt.students.altimin.binder;

import org.junit.Test;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.students.altimin.binder.XmlBinder;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


/**
 * User: altimin
 * Date: 12/1/12
 * Time: 3:14 PM
 */
public class XmlBinderTest {
    @Test
    public void testUser() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        assertTrue(user != deserialized);
        assertEquals(user, deserialized);
    }

    @Test
    public void testUsers() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user1 = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        Permissions permissions2 = new Permissions();
        permissions2.setQuota(500100);
        User user2 = new User(2, UserType.MODERATOR, new UserName("a", "b"), permissions);
        byte[] bytes = binder.serialize(user1, user2);
        User[] deserialized = binder.deserializeObjects(bytes);
        assertTrue(deserialized.length == 2);
        assertTrue(user1 != deserialized[0]);
        assertTrue(user2 != deserialized[1]);
        assertEquals(user1, deserialized[0]);
        assertEquals(user2, deserialized[1]);
    }
}
