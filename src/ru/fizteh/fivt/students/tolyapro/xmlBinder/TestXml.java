package ru.fizteh.fivt.students.tolyapro.xmlBinder;

import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import org.junit.*;

public class TestXml {
    @ru.fizteh.fivt.bind.BindingType(MembersToBind.GETTERS_AND_SETTERS)
    public static class TestGetAndSet {
        int a = 100500;

        @ru.fizteh.fivt.bind.AsXmlElement(name = "a")
        int getA() {
            return a;
        }

        @ru.fizteh.fivt.bind.AsXmlElement(name = "b")
        void setA(int i) {
            a = i;
        }

    }

    public static class InLol {
        double inMinus = -1.0;
        double inPlus = 1.0;

        double getInPlus() {
            return inPlus;
        }

        void setInPlus(double plus) {
            inPlus = plus;
        }
    }

    public static class Lol {
        @ru.fizteh.fivt.bind.AsXmlElement(name = "olol")
        int a = 1;

        int b = 2;
        int c = 3;

        InLol in = new InLol();

        int getA() {
            return a;
        }

        void setA(int A) {
            a = A;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Lol other = (Lol) o;
            return a == other.a && b == other.b && c == other.c;

        }
    }

    public static class BadAnnotations {
        @ru.fizteh.fivt.bind.AsXmlElement(name = "bad")
        int a = 1;
        @ru.fizteh.fivt.bind.AsXmlElement(name = "bad")
        int b = 2;
        @ru.fizteh.fivt.bind.AsXmlElement(name = "bad")
        int c = 3;

        InLol in = new InLol();

        int getA() {
            return a;
        }

        void setA(int A) {
            a = A;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BadAnnotations other = (BadAnnotations) o;
            return a == other.a && b == other.b && c == other.c;

        }
    }

    @Test(expected = Exception.class)
    public void testBadGetterAndSetter() {
        TestGetAndSet testGetAndSet = new TestGetAndSet();
        XmlBinder<TestGetAndSet> binder = new XmlBinder<TestXml.TestGetAndSet>(
                TestGetAndSet.class);
    }

    @Test(expected = Exception.class)
    public void testException1() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        binder.serialize(null);
    }

    @Test(expected = Exception.class)
    public void testException2() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        binder.deserialize(null);
    }

    @Test(expected = Exception.class)
    public void testBadAsXmlElement() {
        BadAnnotations badAnnotations = new BadAnnotations();
        XmlBinder<BadAnnotations> binder = new XmlBinder<TestXml.BadAnnotations>(
                BadAnnotations.class);
    }

    @Test
    public void testAnn() {
        Lol l = new Lol();
        XmlBinder<Lol> binder = new XmlBinder<TestXml.Lol>(Lol.class);
        byte[] bytes = binder.serialize(l);
        Lol deserialized = binder.deserialize(bytes);
        Assert.assertTrue((new String(bytes)).contains("olol"));
        Assert.assertEquals(deserialized, l);
    }

    @Test
    public void testSimlpe1() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"),
                permissions);
        byte[] bytes = binder.serialize(user);
        String result = new String(bytes);
        User deserialized = binder.deserialize(bytes);
        Assert.assertEquals(deserialized, user);
    }

    @Test
    public void testSimple2() {
        XmlBinder<Permissions> binder = new XmlBinder<Permissions>(
                Permissions.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(1);
        permissions.setRoot(true);
        byte[] bytes = binder.serialize(permissions);
        Permissions deserialized = binder.deserialize(bytes);
        Assert.assertEquals(deserialized, permissions);
    }

    @Test
    public void test() {
        XmlBinder binder = new XmlBinder(Permissions.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(1);
        permissions.setRoot(true);
        byte[] bytes = binder.serialize(permissions);
        Permissions deserialized = (Permissions) binder.deserialize(bytes);
        Assert.assertEquals(deserialized, permissions);
    }

    @Test
    public void testNoGenerics() {
        boolean excep = false;
        XmlBinder binder = null;
        try {
            binder = new XmlBinder(Object.class);
        } catch (Exception e) {
            excep = true;
        }
        Assert.assertFalse(excep);

    }

}
