package ru.fizteh.fivt.students.altimin.binder;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.students.altimin.binder.test.A;

/**
 * User: altimin
 * Date: 12/1/12
 * Time: 3:14 PM
 */
public class XmlBinderTest {
    public static void main(String[] args) {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        assert user != deserialized;
        assert user.equals(deserialized);
        XmlBinder<A> binderA = new XmlBinder<A>(A.class);
        A a = new A(4, 5);
        bytes = binderA.serialize(a);
        System.out.println(new String(bytes));
        A deserializedA = binderA.deserialize(bytes);
        assert deserializedA != a;
        assert deserializedA.equals(a);
        System.out.println("OK");
    }
}
