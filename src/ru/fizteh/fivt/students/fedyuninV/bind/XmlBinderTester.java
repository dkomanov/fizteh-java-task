package ru.fizteh.fivt.students.fedyuninV.bind;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class XmlBinderTester {
    public static void main(String[] args) {
        XmlBinder<User> binder = new XmlBinder<>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        user = null;
        byte[] bytes2 = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        assert user != deserialized;
        assert user.equals(deserialized);
    }
}
