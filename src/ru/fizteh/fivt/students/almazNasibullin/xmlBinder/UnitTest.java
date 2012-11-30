package ru.fizteh.fivt.students.almazNasibullin.xmlBinder;

import org.junit.Test;
import org.junit.Assert;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.bind.MembersToBind;
import ru.fizteh.fivt.bind.AsXmlAttribute;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.students.almazNasibullin.xmlBinder.TestClassSerializationFields;
import ru.fizteh.fivt.students.almazNasibullin.xmlBinder.TestClassSerializationMethods;

/**
 * 26.11.12
 * @author almaz
 */

public class UnitTest {

    @Test(expected = RuntimeException.class)
    public void nullPointerObjectToSerialization() {
        new XmlBinder<User>(User.class).serialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void badTypeOfObjectToSerialization() {
        new XmlBinder(User.class).serialize(new Long(2012));
    }

    @Test(expected = RuntimeException.class)
    public void nullPointerObjectToDeserialization() {
        new XmlBinder<User>(User.class).deserialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void badObjectToDeserialization() {
        new XmlBinder(User.class).serialize(new byte[0]);
    }

    @Test(expected = RuntimeException.class)
    public void badTypeOfObjectToDeserialization() {
        new XmlBinder<User>(User.class).deserialize(new XmlBinder<Integer>
                (Integer.class).serialize(new Integer(2012)));
    }

    @Test
    public void testXMLBinderForUser() {
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
    public void testAnnotationAsXmlAttribute() {
        XmlBinder<TestClass> xb = new XmlBinder<TestClass>(TestClass.class);
        TestClass tc = new TestClass(2012, "Something here");
        String result = xb.mySerialize(tc);
        Assert.assertTrue(result.indexOf("newB=\"Something here\"") != -1);
    }
    
    @Test
    public void testSerializationMethods() {
       TestClassSerializationMethods sm = new TestClassSerializationMethods();
       XmlBinder<TestClassSerializationMethods> xb =
               new XmlBinder<TestClassSerializationMethods>(TestClassSerializationMethods.class);
       sm.setName("Almaz");
       byte[] bytes = xb.serialize(sm);
       TestClassSerializationMethods serialized = xb.deserialize(bytes);
       Assert.assertEquals(serialized, sm);
       Assert.assertTrue(serialized != sm);
    }

    @Test
    public void testSerializationFields() {
        XmlBinder<TestClassSerializationFields> xb =
                new XmlBinder<TestClassSerializationFields>(TestClassSerializationFields.class);
        TestClassSerializationFields BMW = new TestClassSerializationFields();
        TestClassSerializationFields Lada = new TestClassSerializationFields();
        Lada.setCar("Lada", "Granta");
        Lada.setPrice(280L);
        Lada.setOwner("Ivan");

        TestClassSerializationFields serializedLada = xb.deserialize(xb.serialize(Lada));
        TestClassSerializationFields serializedBMW = xb.deserialize(xb.serialize(BMW));
        Assert.assertEquals(Lada, serializedLada);
        Assert.assertEquals(BMW, serializedBMW);
        Assert.assertNotSame(serializedLada, serializedBMW);
    }
    
    @Test
    public void testInnerClass() {
         XmlBinder<TestClassSerializationFields.InnerClass> xb =
                new XmlBinder<TestClassSerializationFields.InnerClass>
                (TestClassSerializationFields.InnerClass.class);
         TestClassSerializationFields.InnerClass ic = new
                 TestClassSerializationFields.InnerClass();
         TestClassSerializationFields.InnerClass serialized = xb.deserialize(xb.serialize(ic));
         Assert.assertEquals(serialized, ic);
         Assert.assertTrue(serialized != ic);
    }
}
