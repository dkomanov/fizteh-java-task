package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import com.sun.tools.corba.se.idl.toJavaPortable.StringGen;
import org.junit.Assert;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import org.junit.Test;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import java.util.HashMap;

public class XmlBinderTest extends Assert {
    @Test(expected = RuntimeException.class)
    public void testValueNullPointer() {
        XmlBinder.newInstance(User.class).serialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void testIncorrectTypeOfValue() {
        XmlBinder.newInstance(User.class).serialize(new String("Hello, world!"));
    }

    @Test(expected = RuntimeException.class)
    public void testRecursiveClass() {
        XmlBinder.newInstance(BadClassForSerialization.class).serialize(new BadClassForSerialization());
    }

    @Test(expected = RuntimeException.class)
    public void testUnsupportedClass() {
        XmlBinder.newInstance(ClassForSerializationFields.class).deserialize(new String(
                "<ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerializationMethods>"
                        + "</ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerializationMethods>").getBytes());
    }

    @Test(expected = RuntimeException.class)
    public void testNoneBytes() {
        XmlBinder.newInstance(ClassForSerializationFields.class).deserialize(null);
    }

    @Test
    public void testXmlBuilder() {
        XmlBinder binder = XmlBinder.newInstance(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes1 = binder.serialize(user);
        User anotherUser = (User) binder.deserialize(bytes1);
        assertFalse(user == anotherUser);
        assertEquals(user, anotherUser);
        XmlBinder anotherBinder = XmlBinder.newInstance(ClassForSerializationFields.class);
        ClassForSerializationFields classObject = new ClassForSerializationFields();
        byte[] bytes2 = anotherBinder.serialize(classObject);
        ClassForSerializationFields anotherClass = (ClassForSerializationFields) anotherBinder.deserialize(bytes2);
        assertFalse(anotherClass == classObject);
        assertEquals(classObject, anotherClass);
        XmlBinder yetAnotherBinder = XmlBinder.newInstance(ClassForSerializationMethods.class);
        ClassForSerializationMethods classMethods = new ClassForSerializationMethods();
        classMethods.setSomething(true);
        byte[] bytes3 = yetAnotherBinder.serialize(classMethods);
        ClassForSerializationMethods clMethods = (ClassForSerializationMethods) yetAnotherBinder.deserialize(bytes3);
        assertEquals(classMethods, clMethods);
        String str = "<ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerializationFields>"
                + "<boolFieldNotExist>false</boolFieldNotExist><shField>21</shField><c><![CDATA[D]]></c><doubleField>1.1</doubleField>"
                + "<intField>11</intField><flField>1.993</flField><longField>2012</longField>"
                + "<enumField>MESSAGE</enumField><byteField>12</byteField>"
                + "</ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.ClassForSerializationFields>";
        ClassForSerializationFields classWithNull = (ClassForSerializationFields) anotherBinder.deserialize(str.getBytes());
        assertNull(classWithNull.stringField);
        assertNotEquals(classObject, classWithNull);
    }
}
