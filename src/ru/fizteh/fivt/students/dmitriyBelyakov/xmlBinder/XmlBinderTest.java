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

    @Test(expected = RuntimeException.class)
    public void testUnsupportedClass() {
        new XmlBinder(ClassForSerializationFields.class).deserialize(new String(
                "<classForSerializationMethods>"
                        + "</classForSerializationMethods>").getBytes());
    }

    @Test(expected = RuntimeException.class)
    public void testNoneBytes() {
        new XmlBinder(ClassForSerializationFields.class).deserialize(null);
    }

    @Test
    public void testXmlBuilder() {
        XmlBinder binder = new XmlBinder(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes1 = binder.serialize(user);
        User anotherUser = (User) binder.deserialize(bytes1);
        assertFalse(user == anotherUser);
        assertEquals(user, anotherUser);
        XmlBinder anotherBinder = new XmlBinder(ClassForSerializationFields.class);
        ClassForSerializationFields classObject = new ClassForSerializationFields();
        byte[] bytes2 = anotherBinder.serialize(classObject);
        ClassForSerializationFields anotherClass = (ClassForSerializationFields) anotherBinder.deserialize(bytes2);
        assertFalse(anotherClass == classObject);
        assertEquals(classObject, anotherClass);
        XmlBinder yetAnotherBinder = new XmlBinder(ClassForSerializationMethods.class);
        ClassForSerializationMethods classMethods = new ClassForSerializationMethods();
        classMethods.setSomething(true);
        byte[] bytes3 = yetAnotherBinder.serialize(classMethods);
        ClassForSerializationMethods clMethods = (ClassForSerializationMethods) yetAnotherBinder.deserialize(bytes3);
        assertEquals(classMethods, clMethods);
        String str = "<classForSerializationFields>"
                + "<boolFieldNotExist>false</boolFieldNotExist><shField>21</shField><c><![CDATA[D]]></c><doubleField>1.1</doubleField>"
                + "<intField>11</intField><flField>1.993</flField><longField>2012</longField>"
                + "<enumField>MESSAGE</enumField><byteField>12</byteField>"
                + "</classForSerializationFields>";
        ClassForSerializationFields classWithNull = (ClassForSerializationFields) anotherBinder.deserialize(str.getBytes());
        assertNull(classWithNull.stringField);
        assertNotEquals(classObject, classWithNull);
    }
}
