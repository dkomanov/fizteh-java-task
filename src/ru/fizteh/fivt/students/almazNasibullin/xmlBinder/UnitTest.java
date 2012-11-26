package ru.fizteh.fivt.students.almazNasibullin.xmlBinder;

import org.junit.Test;
import org.junit.Assert;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
/**
 * 26.11.12
 * @author almaz
 */

public class UnitTest {

    @Test(expected = RuntimeException.class)
    public void NullPointerObjectToSerialization() {
        new XmlBinder<User>(User.class).serialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void BadTypeOfObjectToSerialization() {
        new XmlBinder(User.class).serialize(new Long(2012));
    }

    @Test(expected = RuntimeException.class)
    public void NullPointerObjectToDeserialization() {
        new XmlBinder<User>(User.class).deserialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void BadObjectToDeserialization() {
        new XmlBinder(User.class).serialize(new byte[0]);
    }

    @Test(expected = RuntimeException.class)
    public void BadTypeOfObjectToDeserialization() {
        new XmlBinder<User>(User.class).deserialize(new XmlBinder<Integer>
                (Integer.class).serialize(new Integer(2012)));
    }

    @Test
    public void TestXMLBinderForUser() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        User deserialized = binder.deserialize(bytes);
        Assert.assertEquals(user, deserialized);
        Assert.assertTrue(user != deserialized);
    }

    @BindingType(MembersToBind.FIELDS)
    class TestClass {
        public int a;
        @AsXmlAttribute(name = "newB")
        private String b;

        public TestClass(int a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    @Test
    public void TestAnnotationAsXmlAttribute() {
        XmlBinder<TestClass> xb = new XmlBinder<TestClass>(TestClass.class);
        TestClass tc = new TestClass(2012, "Something here");
        String result = xb.mySerialize(tc);
        Assert.assertTrue(result.indexOf("newB=\"Something here\"") != -1);
    }
}
