package ru.fizteh.fivt.students.tolyapro.xmlBinder;

import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import org.junit.*;

public class TestXml {

    @ru.fizteh.fivt.bind.BindingType(MembersToBind.FIELDS)
    public class WithElement2 {
        private String value1;
        private String value2;
        private String value3;

        @ru.fizteh.fivt.bind.AsXmlElement(name = "value-1")
        public String getValue1() {
            return value1;
        }

        @ru.fizteh.fivt.bind.AsXmlElement(name = "value-1")
        public void setValue1(String value1) {
            this.value1 = value1;
        }

        @ru.fizteh.fivt.bind.AsXmlElement(name = "value-2")
        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        @SuppressWarnings("UnusedDeclaration")
        public String getValue3() {
            return value3;
        }

        @ru.fizteh.fivt.bind.AsXmlElement(name = "value-3")
        public void setValue3(String value3) {
            this.value3 = value3;
        }

        public boolean equals(WithElement2 x) {
            return (value1.equals(x.getValue1())
                    && value2.equals(x.getValue2()) && value3.equals(x
                    .getValue3()));
        }
    }

    @ru.fizteh.fivt.bind.BindingType(MembersToBind.FIELDS)
    public static class TestMe {
        private String val1;

        @ru.fizteh.fivt.bind.AsXmlElement(name = "asdval1")
        String getVal1() {
            return val1;
        }

        void setVal1(String x) {
            val1 = x;
        }

        @Override
        public boolean equals(Object o) {
            TestMe t = (TestMe) o;
            return val1.equals(t.val1);
        }
    }

    @ru.fizteh.fivt.bind.BindingType(MembersToBind.GETTERS_AND_SETTERS)
    public static class TestSmth100500 {
        int a = 100500;
        double c = 1.1;

        double getC() {
            return c;
        }

        void setC(double x) {
            c = x;
        }

        @ru.fizteh.fivt.bind.AsXmlElement(name = "OLOLOL")
        int getA() {
            return a;
        }

        @ru.fizteh.fivt.bind.AsXmlElement(name = "OLOLOL")
        void setA(int i) {
            a = i;
        }

    }

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

    // @Test(expected = Exception.class)
    public void testBadGetterAndSetter() {
        TestGetAndSet testGetAndSet = new TestGetAndSet();
        XmlBinder<TestGetAndSet> binder = new XmlBinder<TestXml.TestGetAndSet>(
                TestGetAndSet.class);
    }

    // @Test(expected = Exception.class)
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

    @Test
    public void testAgain() {
        TestMe testMe = new TestMe();
        XmlBinder<TestMe> binder = new XmlBinder<TestMe>(TestMe.class);
        testMe.setVal1("asd");
        byte[] bytes = binder.serialize(testMe);
        TestMe deserialized = binder.deserialize(bytes);
        Assert.assertEquals(testMe, deserialized);
    }

    @Test
    public void testTTT() {
        WithElement2 withElement2 = new WithElement2();
        XmlBinder<WithElement2> binder = new XmlBinder<TestXml.WithElement2>(
                WithElement2.class);
    }
}
