package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import org.junit.Assert;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import org.junit.Test;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.test.BadClassForSerialization;
import ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.test.ClassForSerializationFields;
import ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.test.ClassForSerializationMethods;

public class XmlBinderTest extends Assert {
    @Test(expected = RuntimeException.class)
    public void testValueNullPointer() {
        new XmlBinder<User>(User.class).serialize(null);
    }

    @Test(expected = RuntimeException.class)
    public void testRecursiveClass() {
        new XmlBinder<BadClassForSerialization>(BadClassForSerialization.class).serialize(new BadClassForSerialization());
    }

    @Test(expected = RuntimeException.class)
    public void testUnsupportedClass() {
        new XmlBinder<ClassForSerializationFields>(ClassForSerializationFields.class).deserialize("<classForSerializationMethods></classForSerializationMethods>".getBytes());
    }

    @Test(expected = RuntimeException.class)
    public void testNoneBytes() {
        new XmlBinder<ClassForSerializationFields>(ClassForSerializationFields.class).deserialize(null);
    }

    @Test
    public void testXmlBuilder() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes1 = binder.serialize(user);
        User anotherUser = binder.deserialize(bytes1);
        assertFalse(user == anotherUser);
        assertEquals(user, anotherUser);
        XmlBinder<ClassForSerializationFields> anotherBinder = new XmlBinder<ClassForSerializationFields>(ClassForSerializationFields.class);
        ClassForSerializationFields classObject = new ClassForSerializationFields();
        byte[] bytes2 = anotherBinder.serialize(classObject);
        ClassForSerializationFields anotherClass = anotherBinder.deserialize(bytes2);
        assertFalse(anotherClass == classObject);
        assertEquals(classObject, anotherClass);
        XmlBinder<ClassForSerializationMethods> yetAnotherBinder = new XmlBinder<ClassForSerializationMethods>(ClassForSerializationMethods.class);
        ClassForSerializationMethods classMethods = new ClassForSerializationMethods();
        classMethods.setSomething(true);
        byte[] bytes3 = yetAnotherBinder.serialize(classMethods);
        ClassForSerializationMethods clMethods = yetAnotherBinder.deserialize(bytes3);
        assertEquals(classMethods, clMethods);
        String str = "<classForSerializationFields>"
                + "<boolFieldNotExist>false</boolFieldNotExist><shField>21</shField><c><![CDATA[D]]></c><doubleField>1.1</doubleField>"
                + "<intField>11</intField><flField>1.993</flField><longField>2012</longField>"
                + "<enumField>MESSAGE</enumField><byteField>12</byteField>"
                + "</classForSerializationFields>";
        ClassForSerializationFields classWithNull = anotherBinder.deserialize(str.getBytes());
        assertNull(classWithNull.stringField);
        assertNotEquals(classObject, classWithNull);
        XmlBinder<ClassForSerializationFields.InnerClass> xmlBinderInner = new XmlBinder<>(ClassForSerializationFields.InnerClass.class);
        ClassForSerializationFields.InnerClass inner = new ClassForSerializationFields.InnerClass();
        byte[] bytes4 = xmlBinderInner.serialize(inner);
        assertEquals("<innerClass><string>Eleven</string></innerClass>", new String(bytes4));
    }
}
